(ns sneakycode.render
  (:require [me.raynes.cegdown :as md]
            [clojure.string :as string]
            [clojure.test :refer [function?]]
            [zprint.core :as zp]
            [clojure.java.io :as io]))


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


(defn markdown [& body]
  [:div.content
   (md/to-html
    (->> body
         (map str)
         (string/join "\r\n\r\n")))])


(defn snipit [k & args]
  (let [path (str "snipits/" (name k) ".edn")
        config (->> path
                    io/resource
                    slurp
                    read-string
                    (map (fn [[k v]] [k (eval v)]))
                    (into {}))
        content (:content config)
        args (into [content] args)]
    (if (function? content)
      (apply content args)
      content)))



(comment
  (markdown
   "# You Ma"

   (code
    "clojure"
    (+ 1 1)
    (- 2 2)))

  (snipit :p5)

  )
