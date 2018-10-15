(ns sneakycode.md
  (:require [me.raynes.cegdown :as md]
            [clojure.string :as string]
            [zprint.core :as zp]))


(defmacro clj [& body]
  (let [start "<pre><code class=\"clojure\">"
        end   "</code></pre>"]
    (str
     start
     "\r\n"
     (->> body
          (map zp/zprint-str)
          (string/join "\r\n\r\n"))
     "\r\n"
     end)))


(defn edn [& body]
  [:div.content
   (md/to-html
    (->> body
         (map str)
         (string/join "\r\n\r\n")))])


(comment
  (edn-md
   "# You Ma"

   (code
    "clojure"
    (+ 1 1)
    (- 2 2))))
