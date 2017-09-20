(ns my-tweets.core
  (:refer-clojure :exclude [uuid?])
  (:require [reagent.core :as r]
            [axiom-cljs.core :as ax])
  (:require-macros [axiom-cljs.macros :refer [defview defquery user]]))

;; Remove this before going to production...
(enable-console-print!)

(defview tweets-view [me]
  [:my-tweets/tweet me text ts]
  :store-in (r/atom nil)
  :order-by (- ts))

(defn tweets-pane [host]
  (let [tweets (tweets-view host (user host))
        {:keys [add]} (meta tweets)]
    [:div
     [:button {:on-click #(add {:ts ((:time host))
                                :text ""})} "New Tweet"]
     [:ul
      (for [{:keys [text ts swap! del!]} tweets]
        [:li {:key ts}
         [:input {:value text
                  :on-change #(swap! assoc :text (.-target.value %))}]
         [:button {:on-click del!} "X"]])]]))

(defview following-view [me]
  [:my-tweets/follow me other]
  :store-in (r/atom nil))

(defonce current-followee (r/atom ""))

(defn following-pane [host]
  (let [followees (following-view host (user host))
        {:keys [add]} (meta followees)]
    [:div
     [:input {:value @current-followee
              :on-change #(reset! current-followee (.-target.value %))}]
     [:button {:on-click #(do
                            (add {:other @current-followee})
                            (reset! current-followee ""))} "Follow"]
     [:ul
      (for [{:keys [other del!]} followees]
        [:li {:key other}
         [:span other]
         [:button {:on-click del!} "unfollow"]])]]))

(defquery timeline-view [me]
  [:my-tweets/timeline me -> author text ts]
  :store-in (r/atom nil)
  :order-by (- ts))

(defn timeline-pane [host]
  (let [timeline (timeline-view host (user host))]
    [:ul
     (for [{:keys [author text ts]} timeline]
       [:li {:key ts}
        author ": " text])]))

(def host (ax/default-connection r/atom))

(defn app []
  [:div
   [:h1 "Hello, " (user host)]
   [:table
    [:thead
     [:tr
      [:th "Timeline"]
      [:th "Tweets"]
      [:th "Following"]]]
    [:tbody
     [:tr
      [:td
       (timeline-pane host)]
      [:td
       (tweets-pane host)]
      [:td
       (following-pane host)]]]]])

(let [elem (js/document.getElementById "app")]
  (when elem
    (r/render [app] elem)))

