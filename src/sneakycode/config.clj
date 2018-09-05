(ns sneakycode.config
  (:require [clojure.string :as string]))


(def initial-config
  {:title "SneakyCode"
   :description "Pieter Koornhof on Domain Driven Design, Clean Code and .Net"
   :author "Pieter Koornhof"
   :domain "http://localhost:4321/"
   :date-format (java.text.SimpleDateFormat. "yyyy-MM-dd")})


(def *config (atom initial-config))

(defn getv [key]
  (if-let [value (get @*config key)]
    value
    (throw (Exception. (str "No config value exists for " key)))))


(defn setv [key value]
  (when-not (contains? @*config key)
    (throw (Exception. (str "No config value for " key))))
  (swap! *config assoc key value))


(defn reset [] (reset! *config initial-config))


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
