(ns sneakycode.ghost-import
  (:require [cheshire.core :as cheshire]
            [clj-time.coerce :as time.coerce]
            [clj-time.format :as time.format]))


(defn load [path] (-> path
                      slurp
                      (cheshire/parse-string true)))


(defn relevant-data [loaded-data]
  (-> loaded-data
      :db
      first
      :data
      (select-keys [:tags :posts_tags :posts])))


(defn link-post-tags [{:keys [tags posts_tags] :as data}]
  (->> posts_tags
       (map (fn [[post-id tag-ids]]
              [post-id (->> tag-ids
                            (map #(get tags %)))]))
       (into {})
       (assoc data :linked-tags)))


(defn prep-tags [data]
  (-> data
      (update :tags (fn [tags] (->> tags
                                   (map #(vec [(:id %) (:name %)]))
                                   (into {}))))
      (update :posts_tags (fn [tags] (->> tags
                                         (group-by :post_id)
                                         (map (fn [[k v]] [k (map :tag_id v)]))
                                         (into {}))))
      (link-post-tags)))


(defn prep-posts [{:keys [posts linked-tags]}]
  (->> posts
       (filter #(= "published" (:status %)))
       (map (fn [post]
              (-> post
                  (assoc :tags (get linked-tags (:id post) [])
                         :date (time.format/unparse
                                (time.format/formatter "yyyy-MM-dd")
                                (time.coerce/from-long (:published_at post))))
                  (select-keys [:markdown :tags :slug :title :date :layout]))))))


(defn spit-file [{:keys [date slug markdown] :as post}]
  (let [file-name (format "%s-%s.md" date slug)
        meta (str (dissoc post :slug :markdown))
        content (format "%s\n-----\n%s" meta markdown)]
    (spit (format "./resources/posts/%s" file-name) content)))

(defn import-ghost []
  (->> "./sneakycode.ghost.2017-09-05.json"
       load
       relevant-data
       prep-tags
       prep-posts
       (map spit-file)))
