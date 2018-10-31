(ns sneakycode.render
  (:require [me.raynes.cegdown :as md]
            [clojure.string :as string]
            [clojure.test :refer [function?]]
            [zprint.core :as zp]
            [clojure.java.io :as io]))

(def default-clj-render-opts {:binding {:force-nl? true}
                              :map {:comma? false}
                              :parse {:interpose "\n\n"}})

(defn- deep-merge [v & vs]
  (letfn [(rec-merge [v1 v2]
            (if (and (map? v1) (map? v2))
              (merge-with deep-merge v1 v2)
              v2))]
    (if (some identity vs)
      (reduce #(rec-merge %1 %2) v vs)
      (last vs))))


(defn- render-clj [options s]
  (-> s
      (zp/zprint-str (deep-merge default-clj-render-opts options))
      (string/replace "<" "&lt;")))


(defmacro clj [options & body]
  (let [start "<pre><code class=\"clojure\">"
        end   "</code></pre>"]
    (str
     start
     "\r\n"
     (->> body
          (map #(render-clj options %))
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
