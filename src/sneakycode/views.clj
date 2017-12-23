(ns sneakycode.views
  (:require [hiccup.page :refer [html5]]))


(def favicons
  [[:link {:rel "apple-touch-icon" :sizes "57x57" :href "/apple-icon-57x57.png"}]
   [:link {:rel "apple-touch-icon" :sizes "60x60" :href "/apple-icon-60x60.png"}]
   [:link {:rel "apple-touch-icon" :sizes "72x72" :href "/apple-icon-72x72.png"}]
   [:link {:rel "apple-touch-icon" :sizes "76x76" :href "/apple-icon-76x76.png"}]
   [:link {:rel "apple-touch-icon" :sizes "114x114" :href "/apple-icon-114x114.png"}]
   [:link {:rel "apple-touch-icon" :sizes "120x120" :href "/apple-icon-120x120.png"}]
   [:link {:rel "apple-touch-icon" :sizes "144x144" :href "/apple-icon-144x144.png"}]
   [:link {:rel "apple-touch-icon" :sizes "152x152" :href "/apple-icon-152x152.png"}]
   [:link {:rel "apple-touch-icon" :sizes "180x180" :href "/apple-icon-180x180.png"}]
   [:link {:rel "icon" :type "image/png" :sizes "192x192"  :href "/android-icon-192x192.png"}]
   [:link {:rel "icon" :type "image/png" :sizes "32x32" :href "/favicon-32x32.png"}]
   [:link {:rel "icon" :type "image/png" :sizes "96x96" :href "/favicon-96x96.png"}]
   [:link {:rel "icon" :type "image/png" :sizes "16x16" :href "/favicon-16x16.png"}]
   [:link {:rel "manifest" :href "/manifest.json"}]
   [:meta {:name "msapplication-TileColor" :content "#ffffff"}]
   [:meta {:name "msapplication-TileImage" :content "/ms-icon-144x144.png"}]
   [:meta {:name "theme-color" :content "#ffffff"}]])


(def social
  [{:aria "home" :link "/" :icon "bath"}
   {:aria "about" :link "/about" :icon "meh-o"}
   {:aria "github" :link "https://github.com/sneakypeet" :icon "github" :away? true}
   {:aria "twitter ":link "https://twitter.com/PieterKoornhof" :icon "twitter" :away? true}])


(def menu
  [:nav.navbar.is-fixed-top.is-dark
   {:role "navigation"}
   [:div.navbar-brand
    [:a.navbar-item.has-text-weight-bold {:href "/"} "SNEAKYCODE"]]])

(def footer
  [:nav.navbar.is-fixed-bottom.is-dark
   {:role "navigation" :aria-label "main navigation"}
   [:div.navbar-brand
    (->> social
         (map
          (fn [{:keys [aria link icon away?]}]
            [:a.navbar-item (merge (when away? {:target "_blank"})
                                   {:href link :aria-label aria})
             [:span.icon
              [:i.fa.fa-lg {:class (str "fa-" icon)}]]])))]])


(defn layout-page [{:keys [title render] :as page}]
  (let [head-head [[:meta {:charset "utf-8"}]
                   [:meta {:name "viewport"
                           :content "width=device-width, initial-scale=1"}]
                   [:title title]]
        styles [[:link {:rel "stylesheet" :href "/style.css"}]
                [:link {:rel "stylesheet" :href "https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css"}]]
        head (->> (concat head-head favicons styles)
                  (into [:head]))]
    (html5
     [:html.has-navbar-fixed-top.has-navbar-fixed-bottom
      head
      [:body
       menu
       footer
       [:div.section
        (render page)]]])))


(defn post-page [{:keys [title render tags date next previous all-tags] :as post}]
  (let [all-tags (->> all-tags
                      (map #(hash-map (:tag %) (:posts %)))
                      (into {}))]
    (layout-page
     (assoc
      post
      :render
      (fn [_]
        [:section
         [:div.container
          [:div.columns.is-8.is-variable.is-desktop
           [:article.column.is-three-quarters-desktop
            [:h1.title title]
            [:p.subtitle.is-6
             [:span.tags
              [:span.tag.is-primary date]
              (->> tags sort (map (fn [t] [:a.tag {:href (str "/" t)} t])))]]
            [:div.has-text-justified
             (render post)]]
           [:nav.column {:role "navigation" :aria-label "pagination"}
            [:div.notification
             [:ul.is-size-6
              (when-let [{:keys [title slug]} next]
                [:li
                 [:strong "Next "]
                 [:a.has-text-primary {:href slug} title]])
              (when-let [{:keys [title slug]} previous]
                [:li
                 [:strong "Prev "]
                 [:a.has-text-primary {:href slug} title]])
              [:li
               [:strong "Related"]]
              (->> tags
                   (map
                    (fn [tag]
                      [:ul
                       [:li [:strong tag]]
                       (->> (get all-tags tag)
                            (remove #(= title (:title %)))
                            (map (fn [{:keys [title slug]}]
                                   [:li
                                    [:a.has-text-primary
                                     {:href slug}
                                     title]])))])))]]]]]])))))



(defn tags-page [{:keys [tag posts]}]
  (layout-page
   {:title tag
    :render
    (fn [_]
      [:section
       [:div.container
        [:h1.title.is-capitalized tag]
        [:ul
         (->> posts
              (map (fn [{:keys[ date title slug]}]
                     [:li
                      [:a {:href slug}
                       date " " title]])))]]])}))
