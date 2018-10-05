(ns sneakycode.cljs
  (:require [cljs.build.api :as build]
            [sneakycode.config :as conf]))


#_(defmacro compile-cljs
  ([& body]
   `(vec [:script (build/build '[~@body] {:optimizations :advanced})])))


(defmacro compile-cljs
  [slug namespace-str & body]
  (let [not-optimize? (conf/getv :js-include-goog?)
        file-name #(str "/out" % ".js")]
    `(do
       (build/build '[~@body]
                    {:optimizations (if ~not-optimize? :none :advanced)
                     :main (when ~not-optimize? ~namespace-str)
                     :asset-path "out"
                     :output-dir "inline-cljs/out"
                     :output-to (str "inline-cljs/" (~file-name ~slug))})
       (vec
        [:div
         (if ~not-optimize? [:script {:type "text/javascript" :src "/out/goog/base.js"}] [:span])
         [:script {:type "text/javascript" :src (~file-name ~slug)}]]))))
