(ns sneakycode.config
  (:require [clojure.string :as string]
            [clojure.java.io :as io]))


(defn- set-date-format [config]
  (if (string? (:date-format config))
    (update config :date-format #(java.text.SimpleDateFormat. %))
    config))


(defn- load-config []
  (let [dev-config (->> (io/resource "dev.edn")
                        slurp
                        read-string)]
    (set-date-format dev-config)))


(def *config (atom (load-config)))


(defn getv [key]
  (if-let [value (get @*config key)]
    value
    (throw (Exception. (str "No config value exists for " key)))))


(defn setv [key value]
  (when-not (contains? @*config key)
    (throw (Exception. (str "No config value for " key))))
  (swap! *config assoc key value))


(defn reset [] (reset! *config (load-config)))


(defn merge-config [new-config]
  (doseq [[k v] new-config]
    (setv k v))
  (swap! *config set-date-format))


(defn url
  ([] (url "/"))
  ([path]
   (let [domain (getv :domain)]
     (cond
       (= "" path) domain
       (= "/" path) domain
       (string? path) (let [path (if (string/starts-with? path "/")
                                   (subs path 1)
                                   path)]
                        (str domain path))
       :else (throw (Exception. "path should be a string"))))))
