(ns sneakycode.deploy
  (:gen-class)
  (:require [stasis.core :as stasis]
            [sneakycode.site :as site]
            [sneakycode.cloud-storage :as storage]
            [me.raynes.fs :as fs]
            [pantomime.mime :as mime]
            [clojure.string :as string]))


(def export-dir "docs")
(def all-files #"\.[^.]+$")
(def storage-bucket "sneakycode.net")

;;;; EXPORT

(defn fresh [] (stasis/empty-directory! export-dir))


(defn copy-resource [path]
  (fs/copy-dir-into (str "resources/" path) export-dir))


(defn export []
  (fresh)
  (copy-resource site/image-resources)
  (stasis/export-pages (site/get-site) export-dir))


(defn load-export-dir [] (stasis/slurp-directory export-dir all-files))


(defn export-and-diff []
  (let [old-pages (load-export-dir)]
    (export)
    (stasis/diff-maps old-pages (load-export-dir))))


;;;; DEPLOY to GOOGLE CLOUD STORAGE


(defmulti prep-name (fn [path] (mime/mime-type-of path)))

(defmethod prep-name :default [p] p)

#_(defmethod prep-name "text/html" [p]
  (string/replace p ".html" ""))

(defmethod prep-name "application/rss+xml" [p]
  (string/replace p ".rss" ""))


(defn storage-opts [path]
  (hash-map
   :bucket storage-bucket
   :path (str export-dir path)
   :name (-> path
             prep-name
             (#(if (string/starts-with? % "/")
                 (subs % 1)
                 %)))))


(defn deploy-diff []
  (let [{:keys [added removed changed]} (export-and-diff)
        uploads (->> (concat added changed)
                     (map storage-opts)
                     (sort-by :name))
        removals (->> removed
                      (map storage-opts)
                      (sort-by :name))
        client (storage/create-client {})]
    (doseq [f uploads]
      (storage/upload-file client f)
      (println (str "Uploaded " (:name f))))
    (doseq [f removals]
      (storage/delete-object client f)
      (println (str "Deleted " (:name f))))))


(defn deploy-all []
  (fresh)
  (deploy-diff))


;;;; DEPLOY LOCAL (GITHUB PAGES)

(defn export-to-local []
  (let [{:keys [added removed changed]} (export-and-diff)]
    (doseq [f added]
      (println (str "Added " f)))
    (doseq [f changed]
      (println (str "Changed " f)))
    (doseq [f removed]
      (println (str "Deleted " f)))))


(defn -main [& args]
  (println "Starting Partial Deploy")
  (export-to-local)
  (println "Done"))
