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
   {:aria "rss" :link (conf/url "/rss.rss") :icon "rss"}
   {:aria "github" :link "https://github.com/sneakypeet" :icon "github" :away? true}
   {:aria "twitter ":link "https://twitter.com/PieterKoornhof" :icon "twitter" :away? true}])


(defn- disqus [slug]
  (let [disqus? (conf/getv :disqus?)
        url (conf/url slug "http://sneakycode.net/")]
    (if-not disqus?
      [:span]
      [:div
       [:div {:id "disqus_thread"}]
       [:script
        "var disqus_config = function () {
           this.page.url = '" url "';
           // this.page.identifier = PAGE_IDENTIFIER;
         };
         (function() { // DON'T EDIT BELOW THIS LINE
           var d = document, s = d.createElement('script');
           s.src = 'https://sneakycode.disqus.com/embed.js';
           s.setAttribute('data-timestamp', +new Date());
           (d.head || d.body).appendChild(s);
         })();"]])))


(defn- google-analytics []
  (let [show? (conf/getv :google-analytics?)]
    (if-not show?
      []
      [[:script {:async true :src "https://www.googletagmanager.com/gtag/js?id=UA-58216004-1"}]
       [:script "window.dataLayer = window.dataLayer || [];
                 function gtag(){dataLayer.push(arguments);}
                 gtag('js', new Date());
                 gtag('config', 'UA-58216004-1');"]])))


(defn- disqus [slug]
  (let [disqus? (conf/getv :disqus?)
        disqus-domain "http://sneakycode.net/"
        url (conf/url slug disqus-domain)]
    (if-not disqus?
      [:span]
      [:div
       [:div {:id "disqus_thread"}]
       [:script
        "var disqus_config = function () {
           this.page.url = '" url "';
           // this.page.identifier = PAGE_IDENTIFIER;
         };
         (function() { // DON'T EDIT BELOW THIS LINE
           var d = document, s = d.createElement('script');
           s.src = 'https://sneakycode.disqus.com/embed.js';
           s.setAttribute('data-timestamp', +new Date());
           (d.head || d.body).appendChild(s);
         })();"]])))

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
    [:a.navbar-item.has-text-weight-bold {:href (conf/url)} "sneakycode"]]])


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
                   [:meta {:http-equiv "Content-Security-Policy" :content "upgrade-insecure-requests"}]
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
        head (->> (concat
                   (google-analytics)
                   head-head
                   (favicons)
                   (or meta [])
                   props
                   tags
                   styles)
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
  (let [all-posts   (filter #(not (:draft? %)) all-posts)
        all-tags    (->> all-tags
                         (map #(hash-map (:tag %) (:posts %)))
                         (into {}))
        group-posts (->> all-posts
                         (filter #(= group (:group %)))
                         (sort-by :date)
                         reverse)
        has-group? (and group (> (count group-posts) 1))]
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
           [:p.subtitle.is-6.post-tags
            [:span.tags
             [:span.tag.is-primary.tag-lite date]
             (->> tags sort (map (fn [t] [:a.tag {:href (conf/url (str "tag/" (conf/clean-path t)))} t])))]]
           [:div.has-text-justified.content
            (render post)]]
          [:div.column {:role "navigation" :aria-label "pagination"}
           [:div.short-timeline
            (let [group-series
                  (when has-group?
                    (->> group-posts
                         (map (fn [{:keys [title slug]}]
                                [[:div.timeline-item
                                  [:div.timeline-marker]
                                  [:div.timeline-content
                                   [:a.header
                                    {:href (conf/url slug)}
                                    title]]]]))
                         (reduce into)
                         (into [[:div.timeline-header
                                 [:span.tag.is-primary.tag-lite "Series"]]])))
                  related
                  (into
                   [(when next
                      [:div.timeline-header
                       [:a.tag.is-primary.tag-lite {:href (conf/url (:slug next))} "Older"]])
                    (when next
                      [:div.timeline-item
                       [:div.timeline-marker]
                       [:div.timeline-content
                        [:a.header {:href (conf/url (:slug next))} (:title next)]]])
                    (when previous
                      [:div.timeline-header
                       [:a.tag.is-primary.tag-lite {:href (conf/url (:slug previous))} "Newer"]])
                    (when previous
                      [:div.timeline-item
                       [:div.timeline-marker]
                       [:div.timeline-content
                        [:a.header {:href (conf/url (:slug previous))} (:title previous)]]])]
                   (->> tags
                        (map
                         (fn [tag]
                           (let [tag-posts (->> (get all-tags tag)
                                                (remove #(= title (:title %))))]
                             (when-not (empty? tag-posts)
                               (->> tag-posts
                                    (filter #(not (:draft? %)))
                                    (map (fn [{:keys [title slug]}]
                                           [[:div.timeline-item
                                             [:div.timeline-marker]
                                             [:div.timeline-content
                                              [:a.header
                                               {:href (conf/url slug)}
                                               title]]]]))
                                    (reduce into)
                                    (into [[:div.timeline-header
                                            [:a.tag.is-primary.tag-lite {:href (conf/url (str "tag/" tag))} tag]]]))))))
                        (keep identity)
                        (#(when-not (empty? %)
                            (reduce into %))))
                   )
                  all-related (concat group-series related)
                  timeline (into [:div.timeline.is-rtl] all-related)]
              (when-not (empty? all-related)
                (conj timeline [:div.timeline-header
                                [:span.tag.is-primary.tag-lite "end"]])))
            ]]]
         (disqus slug)])))))


(defn tags-page [{:keys [tag posts slug]}]
  (layout-page
   {:title tag
    :slug slug
    :render
    (fn [_]
      (let [posts
            (->> posts
                 (filter #(not (:draft? %)))
                 (sort-by :date)
                 reverse
                 (map (fn [{:keys[date title slug]}]
                        [:div.timeline-item
                         [:div.timeline-marker.is-icon
                          [:i.fa.fa-link]]
                         [:div.timeline-content
                          [:a {:href (sneakycode.config/url slug)} date " " title]]]

                        ))
                 (into [:div.timeline
                        [:header.timeline-header
                         [:span.tag.is-primary tag]]]))]
        [:div.container.short-timeline
         (conj posts [:header.timeline-header
                      [:span.tag.is-primary.tag-lite "end"]])]))}))
