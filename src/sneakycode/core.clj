(ns sneakycode.core
  (:require [clojure.tools.logging :as log]
            [clojure.string :as string]
            [stasis.core :as stasis]
            [me.raynes.cegdown :as md]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.resource :refer [wrap-resource]]
            [sneakycode.config :as conf]
            [sneakycode.views :as views]
            [clojure.test :refer [function?]]
            [clj-rss.core :as rss])
  (:import [org.apache.commons.io FilenameUtils]))


(def md-setting-split "\n-----\n")

(def file-extensions #".*\.(md|edn)$")

;;; FILE NAMES

(defn prep-file-extension [s]
  (-> s FilenameUtils/getBaseName  (#(str "/" % ".html"))))

(defn prep-file-slug [s]
  (->> s
       (#(string/split % #"\."))
       drop-last
       (string/join ".")))



(defn remove-file-name-date [file-name]
  (str "/" (subs file-name 12)))

(defn grab-file-name-date [file-name]
  (subs file-name 1 11))


(defmulti prep-file-name (fn [type name] type))

(defmethod prep-file-name :post [_ file-name]
  {:file-name (-> file-name remove-file-name-date prep-file-extension)
   :slug (-> file-name remove-file-name-date prep-file-slug)
   :date (grab-file-name-date file-name)})

(defmethod prep-file-name :page [_ file-name]
  {:file-name (prep-file-extension file-name)
   :slug (prep-file-slug file-name)
   :date nil})


(defn prep-edn
  "parses edn string and converts function lists into functions. assumes a map"
  [s]
  (->> s
       read-string
       (map (fn [[k v]] [k (eval v)]))
       (into {})))


;;;; FILES

(defn file-content-type [s] (keyword (FilenameUtils/getExtension s)))

(defmulti prep-file (fn [file-type [file-name file-content]] (file-content-type file-name)))

(defmethod prep-file :edn [file-type [file-name file-content]]
  (let [file-config (prep-edn file-content)]
    (merge
     file-config
     (prep-file-name file-type file-name)
     {:render (fn [{:keys [content] :as file-config}]
                (if (function? content)
                  (content file-config)
                  content))})))


(defmethod prep-file :md [file-type [file-name file-content]]
  (let [[file-config markdown] (string/split file-content (re-pattern md-setting-split))
        file-config (read-string file-config)]
    (merge
     file-config
     (prep-file-name file-type file-name)
     {:content markdown
      :render (fn [{:keys [content]}]
                (md/to-html content))})))


;;;; PAGES

(defn get-pages [posts tags]
  (->> (stasis/slurp-directory "resources/pages" file-extensions)
       (map #(prep-file :page %))
       (map (fn [{:keys [file-name] :as file-config}]
              [file-name (fn [req] (-> file-config
                                      (assoc :all-tags tags
                                             :all-posts posts)
                                      views/layout-page))]))
       (into {})))


;;;; POSTS

(defn link-posts [posts]
  (let [posts (->> posts (sort-by :date) reverse)]
    (loop [previous  nil
           remainder posts
           result    []]
      (if (empty? remainder)
        result
        (let [next    (second remainder)
              current (assoc (first remainder)
                             :next next
                             :previous previous)]
          (recur current
                 (rest remainder)
                 (into result [current])))))))


(defn get-posts-config []
  (let [posts (->> (stasis/slurp-directory "resources/posts" file-extensions)
                   (map #(prep-file :post %))
                   link-posts)
        tags  (->> posts
                   (map (fn [{:keys [tags] :as post}]
                          (->> tags
                               (map #(assoc post :tag %)))))
                   (reduce concat)
                   (group-by :tag)
                   (map (fn [[tag posts]]
                          (let [file-name (prep-file-extension tag)]
                            {:tag       tag
                             :slug      (str "/tag" (prep-file-slug file-name))
                             :file-name (str "/tag" file-name)
                             :posts     posts}))))]
    {:posts     posts
     :tags      tags
     :posts-map (->> posts
                     (map (fn [{:keys [file-name] :as file-config}]
                            [file-name (fn [req] (-> file-config
                                                    (assoc :all-tags tags)
                                                    views/post-page))]))
                     (into {}))
     :tags-map  (->> tags
                     (map (fn [{:keys [file-name] :as tag-config}]
                            [file-name (fn [req] (-> tag-config views/tags-page))]))
                     (into {}))}))


;;;; RSS


(defn cdata [s] (format "<![CDATA[%s]]>" s))

(defn get-rss [posts]
  (let [get-rss
        (fn [req]
          (let [items
                (->> posts
                     (map
                      (fn [p]
                        (let [{:keys [render slug tags date]} p
                              url                          (str conf/domain slug "/")]
                          (-> p
                              (update :title cdata)
                              (assoc :description (cdata (render p))
                                     :link url
                                     :guid url
                                     :pubDate (.parse conf/date-format date)
                                     :category (map cdata tags)
                                     :source (str conf/domain "/rss"))
                              (select-keys [:title :description :link :guid :category
                                            :pubDate :source]))))))
                feed
                (rss/channel-xml
                 {:title         (cdata conf/title)
                  :description   (cdata conf/description)
                  :link          (str conf/domain "/")
                  :lastBuildDate (java.util.Date.)
                  :ttl           "60"}
                 items)]
            feed))]
    {"/rss.rss" get-rss
     "/feed.rss" get-rss
     "/atom.rss" get-rss}))


;;;; EXPORT

(defn get-site []
  (let [{:keys [posts-map tags-map posts tags]} (get-posts-config)]
    (stasis/merge-page-sources
     {:css   (stasis/slurp-directory "resources/css" #".css")
      :pages (get-pages posts tags)
      :posts posts-map
      :tags  tags-map
      :rss   (get-rss posts)})))


;;;; DEV

(defn wrap-file-extension
  "As part of dev we want /my-route to point to /my-route.html"
  [handler]
  (fn [{:keys [uri] :as req}]
    (cond
      (= uri "/")
      (handler req)
      (not (empty? (FilenameUtils/getBaseName uri)))
      (handler req)
      :else
      (handler
       (assoc req :uri
              (if (string/ends-with? uri "/")
                (-> uri
                    drop-last
                    (concat [".html"])
                    (#(string/join "" %)))
                (str uri ".html")))))))


(defn app-init []
  (log/info "Initialising"))


(def app
  (-> (stasis/serve-pages get-site)
      wrap-file-extension
      (wrap-resource "images")
      wrap-content-type))
