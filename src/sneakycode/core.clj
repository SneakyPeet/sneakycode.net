(ns sneakycode.core
  (:require [clojure.tools.logging :as log]
            [clojure.string :as string]
            [stasis.core :as stasis]
            [me.raynes.cegdown :as md]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.resource :refer [wrap-resource]]
            [sneakycode.views :as views]))

(def edn-ext ".edn")
(def html-ext ".html")
(def md-ext ".md")
(def md-setting-split "\n-----\n")


(defn parse-edn [file-content]
  (let [{:keys [content] :as file-content} (read-string file-content)
        content (eval content)] ;; transforms it into function
    (if (clojure.test/function? content)
      (assoc file-content :content (content file-content))
      file-content)))


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

(defn rename-html [file-name]
  (if (string/ends-with? file-name "index.html")
    file-name
    (string/replace file-name html-ext "/")))

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
                   (-> content parse-edn views/layout-page))))))))

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
                 (-> file-content parse views/post-page)))))) ;; returning a function means we only parse a file on request
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
