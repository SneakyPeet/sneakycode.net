(ns sneakycode.cljs
  (:require [clojure.java.shell :as sh]
            [clojure.string :as string]))


(def source-proj "resources/inline-cljs")
(def source-path (str source-proj "/src/tojs/core.cljs"))
(def dest-path (str source-proj "/out/main.js"))
(def out-ns "tojs.core")


(defn- add-ns [s]
  (str "(ns " out-ns ")\n\n" s))


(defn compile-cljs-str
  ([s] (compile-cljs-str "--optimizations advanced" s))
  ([opt-str s]
   (let [code (add-ns s)]
     (spit source-path code)
     (let [opt (string/split opt-str #"\h")
           options (concat
                    ["clj" "--main" "cljs.main"]
                    opt
                    ["-c" out-ns :dir source-proj])
           compile-result (apply sh/sh options)]
       (when-not (= 0 (:exit compile-result))
         (throw (ex-info "CLJS Compilation Failed" compile-result))))
     (slurp dest-path))))



(defmacro compile-cljs [& body]
  (->> body
       (map str)
       (string/join "\n\n")
       compile-cljs-str))


(comment
  (compile-cjs-str "(+ 1 2)")

  (compile-cljs
   (fn [a] a)
   (prn 1234))

  )
