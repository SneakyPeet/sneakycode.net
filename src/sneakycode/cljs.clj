(ns sneakycode.cljs
  (:require [cljs.build.api :as build]
            [sneakycode.config :as conf]
            [clojure.string :as string]))


(defmacro compile-cljs
  ([optimize? slug namespace-str & body]
   (let [not-optimize? (and (conf/getv :js-include-goog?) (not optimize?))
         file-name #(str "/out" (if (string/starts-with? % "/") % (str "/" %)) ".js")]
     `(do
        (build/build '[~@body]
                     {:optimizations (if ~not-optimize? :none :advanced)
                      :main (when ~not-optimize? ~namespace-str)
                      :asset-path "out"
                      :output-dir "inline-cljs/out"
                      :output-to (str "inline-cljs/" (~file-name ~slug))})
        (vec
         [:div
          (if ~not-optimize? [:script {:type "text/javascript" :src (conf/url "/out/goog/base.js")}] [:span])
          [:script {:type "text/javascript" :src (conf/url (~file-name ~slug))}]])))))
