(ns sneakycode.core
  (:require [clojure.tools.logging :as log]
            [clojure.string :as string]
            [stasis.core :as stasis]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.resource :refer [wrap-resource]]
            [sneakycode.site :as site])
  (:import [org.apache.commons.io FilenameUtils]))


;;;; DEV


(defn wrap-file-extension
  "As part of dev we want /my-route to point to /my-route.html"
  [handler]
  (fn [{:keys [uri] :as req}]
    (cond
      (= uri "/")
      (handler req)
      (not (empty? (FilenameUtils/getBaseName uri)))
      (handler req)
      :else
      (handler
       (assoc req :uri
              (if (string/ends-with? uri "/")
                (-> uri
                    drop-last
                    (concat [".html"])
                    (#(string/join "" %)))
                (str uri ".html")))))))


(defn app-init []
  (log/info "Initialising"))


(def app
  (-> (stasis/serve-pages site/get-site)
      wrap-file-extension
      (wrap-resource site/image-resources)
      wrap-content-type))
