(ns sneakycode.config
  (:require [clojure.string :as string]
            [clojure.java.io :as io]))


(defn- set-date-format [config]
  (if (string? (:date-format config))
    (update config :date-format #(java.text.SimpleDateFormat. %))
    config))


(defn load-config
  ([] (load-config "dev.edn"))
  ([path]
   (let [dev-config (->> (io/resource path)
                         slurp
                         read-string)]
     (set-date-format dev-config))))


(def *config (atom (load-config)))


(defn getv [key]
  (let [value (get @*config key)]
    (if-not (nil? value)
      value
      (throw (Exception. (str "No config value exists for " key))))))


(defn setv [key value]
  (when-not (contains? @*config key)
    (throw (Exception. (str "No config value for " key))))
  (swap! *config assoc key value))


(defn reset [] (reset! *config (load-config)))


(defn merge-config [new-config]
  (doseq [[k v] new-config]
    (setv k v))
  (swap! *config set-date-format))


(defn clean-path [path]
  (->> (string/split path #"[^\p{L}\p{Nd}]+" )
       (remove string/blank?)
       (string/join "-")))


(defn url
  ([] (url "/"))
  ([path] (url path (getv :domain)))
  ([path domain]
   (let [url
         (cond
           (= "" path) domain
           (= "/" path) domain
           (string? path) (let [path (if (string/starts-with? path "/")
                                       (subs path 1)
                                       path)]
                            (str domain path))
           :else (throw (Exception. "path should be a string")))]
     url)))
