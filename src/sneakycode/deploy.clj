(ns sneakycode.deploy
  (:gen-class)
  (:require [stasis.core :as stasis]
            [sneakycode.site :as site]
            [me.raynes.fs :as fs]
            [pantomime.mime :as mime]
            [clojure.string :as string]
            [sneakycode.config :as conf]))


(def export-dir "docs")
(def cljs-dir "inline-cljs")
(def all-files #"\.[^.]+$")

;;;; EXPORT

(defn fresh []
  (stasis/empty-directory! export-dir)
  (stasis/empty-directory! cljs-dir))


(defn copy-resource [path]
  (fs/copy-dir-into (str "resources/" path) export-dir))


(defn export []
  (fresh)
  (copy-resource site/image-resources)
  (stasis/export-pages (site/get-site) export-dir)
  (fs/copy-dir (str cljs-dir "/out") export-dir))


(defn load-export-dir [] (stasis/slurp-directory export-dir all-files))


(defn export-and-diff []
  (let [old-pages (load-export-dir)]
    (export)
    (stasis/diff-maps old-pages (load-export-dir))))


;;;; DEPLOY LOCAL (GITHUB PAGES)

(defn export-to-local []
  (let [{:keys [added removed changed]} (export-and-diff)]
    (doseq [f added]
      (println (str "Added " f)))
    (doseq [f changed]
      (println (str "Changed " f)))
    (doseq [f removed]
      (println (str "Deleted " f)))))


(defn -main [& [settings-path]]
  (println (str "Starting Partial Deploy of " settings-path))
  (conf/merge-config (conf/load-config settings-path))
  (export-to-local)
  (conf/reset)
  (println "Done"))
