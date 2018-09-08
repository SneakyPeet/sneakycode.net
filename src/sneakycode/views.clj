(ns sneakycode.views
  (:require [hiccup.page :refer [html5]]
            [clygments.core :as pygments]
            [net.cgrand.enlive-html :as enlive]
            [clojure.string :as string]
            [sneakycode.config :as conf]))

;;;; CONFIG

(defn- favicons []
  [[:link {:rel "apple-touch-icon" :sizes "57x57" :href (conf/url "/apple-icon-57x57.png")}]
   [:link {:rel "apple-touch-icon" :sizes "60x60" :href (conf/url "/apple-icon-60x60.png")}]
   [:link {:rel "apple-touch-icon" :sizes "72x72" :href (conf/url "/apple-icon-72x72.png")}]
   [:link {:rel "apple-touch-icon" :sizes "76x76" :href (conf/url "/apple-icon-76x76.png")}]
   [:link {:rel "apple-touch-icon" :sizes "114x114" :href (conf/url "/apple-icon-114x114.png")}]
   [:link {:rel "apple-touch-icon" :sizes "120x120" :href (conf/url "/apple-icon-120x120.png")}]
   [:link {:rel "apple-touch-icon" :sizes "144x144" :href (conf/url "/apple-icon-144x144.png")}]
   [:link {:rel "apple-touch-icon" :sizes "152x152" :href (conf/url "/apple-icon-152x152.png")}]
   [:link {:rel "apple-touch-icon" :sizes "180x180" :href (conf/url "/apple-icon-180x180.png")}]
   [:link {:rel "icon" :type "image/png" :sizes "192x192" :href (conf/url "/android-icon-192x192.png")}]
   [:link {:rel "icon" :type "image/png" :sizes "32x32" :href (conf/url "/favicon-32x32.png")}]
   [:link {:rel "icon" :type "image/png" :sizes "96x96" :href (conf/url "/favicon-96x96.png")}]
   [:link {:rel "icon" :type "image/png" :sizes "16x16" :href (conf/url "/favicon-16x16.png")}]
   [:link {:rel "manifest" :href (conf/url "manifest.json")}]
   [:meta {:name "msapplication-TileColor" :content "#ffffff"}]
   [:meta {:name "msapplication-TileImage" :content "ms-icon-144x144.png"}]
   [:meta {:name "theme-color" :content "#ffffff"}]])


(defn- social []
  [{:aria "home" :link (conf/url) :icon "bath"}
   {:aria "history" :link (conf/url "/all-posts") :icon "history"}
   {:aria "tags" :link (conf/url "/tags") :icon "tags"}
   {:aria "rss" :link (conf/url "/rss") :icon "rss"}
   {:aria "github" :link "https://github.com/sneakypeet" :icon "github" :away? true}
   {:aria "twitter ":link "https://twitter.com/PieterKoornhof" :icon "twitter" :away? true}])

;;;; CODE

(defn- extract-code
  [highlighted]
  (-> highlighted
      java.io.StringReader.
      enlive/html-resource
      (enlive/select [:pre])
      first
      :content))


(defn- highlight [node]
  (let [code (->> node :content (apply str))
        lang (->> node :attrs :class keyword)]
    (try
      (assoc node :content (-> code
                               (pygments/highlight lang :html)
                               extract-code))
      (catch Exception e
        (throw (Exception. (str "Issue With Code Highlight for language " lang) e))))))


(defn- highlight-code-blocks [page]
  (enlive/sniptest page
                   [:pre :code] highlight
                   [:pre] #(assoc-in % [:attrs :class] "syntax")))


;;;; PAGES

(defn- post-process-html [html]
  (-> html
      highlight-code-blocks))


(defn- menu []
  [:nav.navbar.is-fixed-top.is-dark.has-gradient
   {:role "navigation"}
   [:div.navbar-brand
    [:a.navbar-item.has-text-weight-bold {:href (conf/url)} "SNEAKYCODE"]]])

(defn- footer []
  [:nav.navbar.is-fixed-bottom.is-dark.has-gradient
   {:role "navigation" :aria-label "main navigation"}
   [:div.navbar-brand
    (->> (social)
         (map
          (fn [{:keys [aria link icon away?]}]
            [:a.navbar-item (merge (when away? {:target "_blank"})
                                   {:href link :aria-label aria})
             [:span.icon
              [:i.fa.fa-lg {:class (str "fa-" icon)}]]])))]])



(defn layout-page [{:keys [description title render meta props slug tags author no-section? no-menu?] :as page}]
  (let [head-head [[:meta {:charset "utf-8" :content "text/html"}]
                   [:meta {:name    "viewport"
                           :content "width=device-width, initial-scale=1"}]
                   [:link {:rel "canonnical" :href (conf/url slug)}]
                   [:link {:rel "alternative" :type "application/rss+xml" :title (conf/getv :title) :href (conf/url "/rss/")}]
                   [:title title]
                   (when description
                     [:meta {:name "description" :content description}])
                   [:meta {:name "author" :content (or author (conf/getv :author))}]]
        styles    [[:link {:rel "stylesheet" :href (conf/url "style.css")}]
                   [:link {:rel "stylesheet" :href "https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css"}]]
        props     (->> (merge {"og:site_name" (conf/getv :title)}
                              props)
                       (map (fn [[p c]] [:meta {:property p :content c}])))
        tags      (->> tags
                       (map (fn [t] [:meta {:property "article:tag" :content t}])))
        head (->> (concat head-head (favicons) (or meta []) props tags styles)
                  (into [:head]))]
    (->
     [:html.has-navbar-fixed-bottom (when-not no-menu? {:class "has-navbar-fixed-top"})
      head
      [:body
       (when-not no-menu? (menu))
       (footer)
       [:div (when-not no-section? {:class "section"})
        (render page)]]]
     html5
     post-process-html)))


(defn post-page [{:keys [title description render tags date next previous all-tags all-posts group slug] :as post}]
  (let [all-tags    (->> all-tags
                         (map #(hash-map (:tag %) (:posts %)))
                         (into {}))
        group-posts (->> all-posts
                         (filter #(= group (:group %)))
                         (sort-by :date)
                         reverse)]
    (layout-page
     (assoc
      post
      :props {"og:type"        "article"
              "og:title"       title
              "og:description" description
              "og:url"         (conf/url slug)}
      :render
      (fn [_]
        [:div.container
         [:div.columns.is-8.is-variable.is-desktop
          [:article.column.is-three-quarters-desktop
           [:h1.title.has-text-weight-light title]
           [:p.subtitle.is-6
            [:span.tags
             [:span.tag.is-primary date]
             (->> tags sort (map (fn [t] [:a.tag {:href (conf/url (str "tag/" t))} t])))]]
           [:div.has-text-justified
            (render post)]]
          [:nav.column {:role "navigation" :aria-label "pagination"}
           (when (and group (> (count group-posts) 1))
             [:div.notification
              [:ul.is-size-6
               [:li "More from " [:strong group]]
               (map-indexed
                (fn [i p]
                  [:li
                   (inc i) ". "
                   (if (= (:slug p) slug)
                     [:span (:title p)]
                     [:a.has-text-primary {:href (conf/url (:slug p))} (:title p)])])
                group-posts)]])
           [:div.notification
            [:ul.is-size-6
             (when-let [{:keys [title slug]} next]
               [:li
                [:strong "Older "]
                [:a.has-text-primary {:href (conf/url slug)} title]])
             (when-let [{:keys [title slug]} previous]
               [:li
                [:strong "Newer  "]
                [:a.has-text-primary {:href (conf/url slug)} title]])
             [:li
              [:strong "Related"]]
             (->> tags
                  (map
                   (fn [tag]
                     (let [tag-posts (->> (get all-tags tag)
                                          (remove #(= title (:title %))))]
                       (if (empty? tag-posts)
                         [:ul]
                         [:ul
                          [:li [:strong tag]]
                          (->> tag-posts
                               (map (fn [{:keys [title slug]}]
                                      [:li
                                       [:a.has-text-primary
                                        {:href (conf/url slug)}
                                        title]])))])))))]]]]])))))


(defn tags-page [{:keys [tag posts slug]}]
  (layout-page
   {:title tag
    :slug slug
    :render
    (fn [_]
      (let [posts
            (->> posts
                 (sort-by :date)
                 reverse
                 (map (fn [{:keys[date title slug]}]
                        (->>
                         posts
                         (map
                          (fn [{:keys [title slug]}]
                            [:div.timeline-item
                             [:div.timeline-marker.is-icon
                              [:i.fa.fa-link]]
                             [:div.timeline-content
                              [:a {:href (sneakycode.config/url slug)} date " " title]]])))
                        ))
                 (reduce into)
                 (into [:div.timeline
                        [:header.timeline-header
                         [:span.tag.is-primary tag]]]))]
        [:div.container.all-posts
         (conj posts [:header.timeline-header
                      [:span.tag.is-primary "end"]])]))}))
