(ns sneakycode.cljs
  (:require [cljs.build.api :as build]
            [sneakycode.config :as conf]))


(defmacro compile-cljs
  ([& body]
   `(vec [:script (build/build '[~@body] {:optimizations :advanced})])))


(defmacro compile-cljs-dev
  [namespace-str & body]
  (when-not (conf/getv :js-include-goog?)
    (throw "Please use `compile-cljs` instead of `compile-cljs-dev`"))
  `(do
     (build/build '[~@body] {:optimizations :none
                             :main ~namespace-str
                             :asset-path "out"
                             :output-dir "inline-cljs/out"
                             :output-to "inline-cljs/out/main.js"})
     (vec
      [:div
       [:script {:type "text/javascript" :src "out/goog/base.js"}]
       [:script {:type "text/javascript" :src "out/main.js"}]])))
