(ns sneakycode.site
  (:require [clojure.string :as string]
            [stasis.core :as stasis]
            [me.raynes.cegdown :as md]
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
  (subs file-name 12))

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
                (md/to-html content [:autolinks :fenced-code-blocks :strikethrough]))})))


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
                               (map #(assoc post :tag (string/replace % " " "-")
                                            :tag-name %)))))
                   (reduce concat)
                   (group-by :tag)
                   (map (fn [[tag posts tag-name]]
                          (let [file-name (prep-file-extension (conf/clean-path tag))]
                            {:tag       tag
                             :tag-name  tag-name
                             :slug      (str "/tag" (prep-file-slug file-name))
                             :file-name (str "/tag" file-name)
                             :posts     posts}))))]
    {:posts     posts
     :tags      tags
     :posts-map (->> posts
                     (map (fn [{:keys [file-name] :as file-config}]
                            [file-name (fn [req] (-> file-config
                                                    (assoc :all-tags tags
                                                           :all-posts posts)
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
                              url                          (str (conf/url slug) "/")]
                          (-> p
                              (update :title cdata)
                              (assoc :description (cdata (render p))
                                     :link url
                                     :guid url
                                     :pubDate (.parse (conf/getv :date-format) date)
                                     :category (map cdata tags)
                                     :source (conf/url "/rss.rss"))
                              (select-keys [:title :description :link :guid :category
                                            :pubDate :source]))))))
                feed
                (rss/channel-xml
                 {:title         (cdata (conf/getv :title))
                  :description   (cdata (conf/getv :description))
                  :link          (conf/url)
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
      :rss   (get-rss posts)
      :cljs (if (conf/getv :js-include-goog?)
              (stasis/slurp-directory "inline-cljs" #"\.js$")
              {})})))

(def image-resources "images")
