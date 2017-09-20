(ns my-tweets.core
  (:refer-clojure :exclude [uuid?])
  (:require [reagent.core :as r]
            [axiom-cljs.core :as ax])
  (:require-macros [axiom-cljs.macros :refer [defview defquery user]]))

;; Remove this before going to production...
(enable-console-print!)

;; A view allows the client to access (create, read, update and delete) facts of a certain form.
;; See http://axiom-clj.org/axiom-cljs.macros.html#defview for more information.
;; This view represents the :my-tweets/task fact.
(defview task-view [user]
  [:my-tweets/task user task ts]
  ;; We want to store the view's state in a reagent atom so that changes to the view will be reflected in the DOM.
  :store-in (r/atom nil)
  ;; We want the tasks to be sorted by timestamp, ascending.
  :order-by ts)

;; The following component edits a single task.
;; It takes a single view entry and displays an edit box for the text, and a delete button to remove it.
(defn edit-task [task-entry]
  (let [{:keys [task ts swap! del!]} task-entry]
    [:div
     [:input {:value task
              ;; When the field's value changes we use the provided swap! function to modify the view data.
              :on-change #(swap! assoc :task (.-target.value %))}]
     ;; To delete we use the provided del! function.
     [:button {:on-click del!} "X"]]))

;; The following component displays a list of all tasks, allowing them to be edited.
;; It takes as parameter the host object, which represents the connection to the host.
;; It provides a list of edit-task components, and a button for creating new tasks.
(defn task-list [host]
  ;; To get the list of items, we call the view function
  (let [tasks (task-view host (user host))
        {:keys [add]} (meta tasks)]
    [:div
     [:ul
      (for [task-entry tasks]
        [:li {:key (:ts task-entry)} ;; The timestamp is used as unique ID for this task
         (edit-task task-entry)])]
     ;; Create an empty task with the current time
     [:button {:on-click #(add {:ts ((:time host))
                                :task ""})} "New Task"]]))

;; A query allows the client to access results calculated by clauses.
;; This query accesses :my-tweets/my-tasks.
(defquery my-tasks-query [user]
  [:my-tweets/my-tasks user -> author task ts]
  :store-in (r/atom nil)
  :order-by ts)

;; my-task-list displays all my tasks: those created by me, and those in which I am mentioned.
(defn my-task-list [host]
  (let [tasks (my-tasks-query host (user host))]
    [:ul
     (for [{:keys [author task ts]} tasks]
       [:li {:key ts}
        author ": " task])]))

;; 'host' is the object connecting this app to the host.
(def host (ax/default-connection r/atom))

(defn app []
  [:div
   (task-list host)
   (my-task-list host)])

(defn render []
  (let [elem (js/document.getElementById "app")]
    (when elem
      (r/render [app] elem))))

(render)
