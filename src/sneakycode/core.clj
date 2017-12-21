(ns sneakycode.core
  (:require [clojure.tools.logging :as log]
            [clojure.string :as string]
            [stasis.core :as stasis]
            [hiccup.page :refer [html5]]
            [me.raynes.cegdown :as md]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.resource :refer [wrap-resource]]))

(def edn-ext ".edn")
(def html-ext ".html")
(def md-ext ".md")
(def md-setting-split "\n-----\n")


(defn rename-html [file-name]
  (if (string/ends-with? file-name "index.html")
    file-name
    (string/replace file-name html-ext "/")))


;;;; TEMPLATES

(def favicons
  [[:link {:rel "apple-touch-icon" :sizes "57x57" :href "/apple-icon-57x57.png"}]
   [:link {:rel "apple-touch-icon" :sizes "60x60" :href "/apple-icon-60x60.png"}]
   [:link {:rel "apple-touch-icon" :sizes "72x72" :href "/apple-icon-72x72.png"}]
   [:link {:rel "apple-touch-icon" :sizes "76x76" :href "/apple-icon-76x76.png"}]
   [:link {:rel "apple-touch-icon" :sizes "114x114" :href "/apple-icon-114x114.png"}]
   [:link {:rel "apple-touch-icon" :sizes "120x120" :href "/apple-icon-120x120.png"}]
   [:link {:rel "apple-touch-icon" :sizes "144x144" :href "/apple-icon-144x144.png"}]
   [:link {:rel "apple-touch-icon" :sizes "152x152" :href "/apple-icon-152x152.png"}]
   [:link {:rel "apple-touch-icon" :sizes "180x180" :href "/apple-icon-180x180.png"}]
   [:link {:rel "icon" :type "image/png" :sizes "192x192"  :href "/android-icon-192x192.png"}]
   [:link {:rel "icon" :type "image/png" :sizes "32x32" :href "/favicon-32x32.png"}]
   [:link {:rel "icon" :type "image/png" :sizes "96x96" :href "/favicon-96x96.png"}]
   [:link {:rel "icon" :type "image/png" :sizes "16x16" :href "/favicon-16x16.png"}]
   [:link {:rel "manifest" :href "/manifest.json"}]
   [:meta {:name "msapplication-TileColor" :content "#ffffff"}]
   [:meta {:name "msapplication-TileImage" :content "/ms-icon-144x144.png"}]
   [:meta {:name "theme-color" :content "#ffffff"}]])

(defn layout-page [{:keys [title content]}]
  (prn content)
  (let [head-head [[:meta {:charset "utf-8"}]
                   [:meta {:name "viewport"
                           :content "width=device-width, initial-scale=1"}]
                   [:title title]]
        styles [[:link {:rel "stylesheet" :href "/style.css"}]]
        head (->> (concat head-head favicons styles)
                  (into [:head]))]
    (html5
     head
     [:body
      [:section.section
       [:div.container
        content]]])))


(defn parse-edn [file-content]
  (-> file-content read-string))


(defn parse-markdown [file-content]
  (let [[settings markdown] (string/split file-content (re-pattern md-setting-split))]
    (-> settings
        read-string
        (assoc :content markdown)
        (update :content md/to-html))))

(defn swap-extension [ext file-name]
  (string/replace file-name ext html-ext))

(def swap-markdown-extension (partial swap-extension md-ext))

(def swap-edn-extension (partial swap-extension edn-ext))

(defn remove-file-name-date [file-name]
  (str "/" (subs file-name 12)))

;;;; PAGES

(defn get-pages []
  (let [pages (stasis/slurp-directory "resources/pages" (re-pattern edn-ext))]
    (zipmap
     (->> (keys pages)
          (map swap-edn-extension)
          (map rename-html))
     (->> (vals pages)
          (map (fn [content]
                 (fn [req] ;; returning a function means we only parse a file on request
                   (-> content parse-edn layout-page))))))))

;;;; POSTS

(defn get-posts []
  (let [posts (stasis/slurp-directory "resources/posts" #".*\.(md|edn)$")]
    (->> posts
         (map
          (fn [[file-name file-content]]
            (let [is-edn? (string/ends-with? file-name edn-ext)
                  replace-extension (if is-edn? swap-edn-extension swap-markdown-extension)
                  parse (if is-edn? parse-edn parse-markdown)]
              (hash-map
               (-> file-name remove-file-name-date replace-extension rename-html)
               (fn [req]
                 (-> file-content parse layout-page)))))) ;; returning a function means we only parse a file on request
         (apply merge))))


;;;; EXPORT

(defn get-site []
  (stasis/merge-page-sources
   {:css   (stasis/slurp-directory "resources/css" #".css")
    :pages (get-pages)
    :posts (get-posts)}))


;;;; DEV

(defn app-init []
  (log/info "Initialising"))


(def app
  (-> (stasis/serve-pages get-site)
      (wrap-resource "images")
      wrap-content-type))
