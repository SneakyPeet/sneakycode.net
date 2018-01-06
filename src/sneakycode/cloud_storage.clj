(ns sneakycode.cloud-storage
  (:require [clj-http.client :as http]
            [pantomime.mime :as mime]
            [clojure.string :as string])
  (:import com.google.api.client.googleapis.auth.oauth2.GoogleCredential))

;;https://github.com/SimpleValue/sv.gcloud.client/blob/master/src/sv/gcloud/client.clj

(defn wrap-access-token [client config]
  (let [credential (GoogleCredential/getApplicationDefault)
        credential (if-let [scopes (:scopes config)]
                     (.createScoped credential scopes)
                     credential)]
    (fn [request]
      (when (or (not (.getAccessToken credential))
                (> (System/currentTimeMillis)
                   (-
                    (.getExpirationTimeMilliseconds
                     credential)
                    (* 1000 60 10))))
        (.refreshToken credential))
      (client
       (assoc-in
        request
        [:headers "Authorization"]
        (str "Bearer " (.getAccessToken credential)))))))


(defn create-client [config]
  (wrap-access-token
   http/request
   config))


;;https://github.com/SimpleValue/sv.gcloud.storage/blob/master/src/sv/gcloud/storage/client.clj

(def storage-endpoint "https://www.googleapis.com/storage/v1/b/")
(def upload-endpoint "https://www.googleapis.com/upload/storage/v1/b/")


(defn upload-request [params]
  (let [{:keys [bucket path name]} params]
    {:method :post
     :url (str (:upload-endpoint params upload-endpoint) bucket "/o")
     :query-params {:uploadType "media"
                    :name name
                    :predefinedAcl "publicRead"}
     :content-type (mime/mime-type-of path)
     :body (clojure.java.io/file path)
     :as :json}))


(defn delete-object-request [params]
  (let [{:keys [bucket name]} params]
    {:method :delete
     :url (str (:storage-endpoint params storage-endpoint)
               bucket "/o/"
               name)
     :as :json}))


(defn delete-object
  "Expects :bucket :name :path
  Assumes Default Application Credentials
  Assumes Bucket exists"
  [client params]
  (:body (client (delete-object-request params))))


(defn upload-file
  "Expects :bucket :name :path
  Assumes Default Application Credentials
  Assumes Bucket exists"
  [client params]
  (:body (client (upload-request params))))
