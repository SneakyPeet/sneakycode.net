(ns sneakycode.core
  (:require [clojure.tools.logging :as log]
            [clojure.string :as string]
            [stasis.core :as stasis]
            [hiccup.page :refer [html5]]
            [me.raynes.cegdown :as md]
            [ring.middleware.content-type :refer [wrap-content-type]]))

(def edn-ext ".edn")
(def html-ext ".html")
(def md-ext ".md")
(def md-setting-split "-----")


;;;; TEMPLATES

(defn layout-page [{:keys [title content]}]
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1"}]
    [:title title]
    [:link {:rel "stylesheet" :href "/style.css"}]]
   [:body
    [:section.section
     [:div.container
      [:h1.title "HELLO"]
      [:p.subtitle "WORLD"]]]]))


;;;; PAGES

(defn get-pages []
  (let [pages (stasis/slurp-directory "resources/pages" (re-pattern edn-ext))]
    (zipmap
     (->> (keys pages)
          (map #(string/replace % edn-ext html-ext)))
     (->> (vals pages)
          (map read-string)
          (map layout-page)))))

;;;; POSTS

(defn parse-markdown [file-content]
  (let [[settings markdown] (string/split file-content (re-pattern md-setting-split))]
    (-> settings
        read-string
        (assoc :content markdown)
        (update :content md/to-html))))


(defn get-posts[]
  (let [posts (stasis/slurp-directory "resources/posts" (re-pattern md-ext))]
    (zipmap
     (->> (keys posts)
          (map #(string/replace % md-ext html-ext)))
     (->> (vals posts)
          (map parse-markdown)
          (map layout-page)))))


;;;; EXPORT

(defn get-site []
  (merge
   (stasis/slurp-directory "resources/css" #".css")
   (get-pages)
   (get-posts)))


;;;; DEV

(defn app-init []
  (log/info "Initialising"))


(def app
  (-> (stasis/serve-pages get-site)
      wrap-content-type))
