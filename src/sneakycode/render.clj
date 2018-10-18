(ns sneakycode.render
  (:require [me.raynes.cegdown :as md]
            [clojure.string :as string]
            [clojure.test :refer [function?]]
            [zprint.core :as zp]
            [clojure.java.io :as io]))

(def default-clj-render-opts {:binding {:force-nl? true}})

(defmacro clj [& body]
  (let [start "<pre><code class=\"clojure\">"
        end   "</code></pre>"]
    (str
     start
     "\r\n"
     (->> body
          (map #(zp/zprint-str % default-clj-render-opts))
          (string/join "\r\n\r\n"))
     "\r\n"
     end)))


(defn markdown [& body]
  [:div
   (md/to-html
    (->> body
         (map str)
         (string/join "\r\n\r\n")))])


(defn snippet [k & args]
  (let [path (str "snippets/" (name k) ".edn")
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
   "# You Ma")

  (clj
   (defn foo []
     (->>  (+ 1 1)
           dec
           inc))

   (- 2 2))


  (snippet :button "sdf")

  )
