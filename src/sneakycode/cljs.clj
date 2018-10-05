(ns sneakycode.cljs
  (:require [clojure.java.shell :as sh]))


(def source-proj "resources/inline-cljs")
(def source-path (str source-proj "/src/tojs/core.cljs"))
(def dest-path (str source-proj "/out/main.js"))
(def out-ns "tojs.core")


(defn- add-ns [s]
  (str "(ns " out-ns ")\n\n" s))

(defn compile-cjs-str [s]
  (let [code (add-ns s)]
    (spit source-path code)
    (let [compile-result
          (sh/sh "clj" "--main" "cljs.main" "--optimizations" "advanced" "-c" out-ns
                 :dir source-proj
                 )]
      (when-not (= 0 (:exit compile-result))
        (throw (ex-info "CLJS Compilation Failed" compile-result))))
    (slurp dest-path)))


(comment
  (compile-cjs-str "(+ 1 2)"))
