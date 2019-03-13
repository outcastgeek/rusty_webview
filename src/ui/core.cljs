(ns ui.core
  (:require [clojure.string :as str]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [ui.db]
            [ui.events]
            ;; Material UI
            ; [react]
            ; [react-dom]
            ; ["@material-ui/core/Button" :refer [Button]] ;; Not Quite Working
            ["@material-ui/core" :as mui]
            ["@material-ui/core/styles" :refer [createMuiTheme withStyles]]
            ["@material-ui/core/colors" :as mui-colors]
            ["@material-ui/icons" :as mui-icons]))

(enable-console-print!)

(println "Hello world!")

(defn average [a b]
  (let [average (/ (+ a b) 2.0)]
    (println average)
    average))


(comment
  (require '[hello-world.core :as hello] :reload))


;; -- Domino 1 - Event Dispatch -----------------------------------------------

(defn dispatch-timer-event
  []
  (let [now (js/Date.)]
    (rf/dispatch [:timer now])))  ;; <-- dispatch used

;; Call the dispatching function every second.
;; `defonce` is like `def` but it ensures only one instance is ever
;; created in the face of figwheel hot-reloading of this file.
(defonce do-timer (js/setInterval dispatch-timer-event 1000))

;; -- Domino 5 - View Functions ----------------------------------------------

(defn clock
  []
  [:div.example-clock
   {:style {:color @(rf/subscribe [:time-color])}}
   (-> @(rf/subscribe [:time])
       .toTimeString
       (str/split " ")
       first)])

(defn color-input
  []
  [:div.color-input
   "Time color: "
   [:input {:type "text"
            :value @(rf/subscribe [:time-color])
            :on-change #(rf/dispatch [:time-color-change (-> % .-target .-value)])}]])  ;; <---

;; -- Counter UI -------------------------------------------------------

(defn counter-view
  []
  [:div
   [:> mui/Card
    [:> mui/CardHeader {:title "Counter"
                        :subheader "This is the current Count:"}]
    [:> mui/CardContent
     ; [:span @(rf/subscribe [:counter])]
     [:> mui/Typography {:component "p"}
      [:b {:style {:font-size "40px"
                   :margin "5px"}}
       @(rf/subscribe [:counter])]]]
    [:> mui/CardActions
     ;;[:button {:on-click #(rf/dispatch [:decrement])} "-"]
     [:> mui/Button {:variant "contained"
                     :color "default"
                     :on-click #(rf/dispatch [:decrement])} [:b "-"]]
     ;;[:button {:on-click #(rf/dispatch [:increment])} "+"]
     [:> mui/Button {:variant "contained"
                     :color "primary"
                     :on-click #(rf/dispatch [:increment])} [:b "+"]]
     ]]
    ])

(defn ui
  []
  [:div
   [:h1 "Hello world, it is now"]
   [clock]
   [color-input]
   [:hr]
   [counter-view]])

;; -- Entry Point -------------------------------------------------------------

(defn ^:export run
  []
  (rf/dispatch-sync [:initialize])     ;; puts a value into application state
  (r/render [ui]              ;; mount the application's ui into '<div id="app" />'
    (js/document.getElementById "app")))

;; -- External Calls ----------------------------------------------------------

(defn- clj->json
  [ds]
  (. js/JSON stringify (clj->js ds)))

(defn- invoke
  [arg]
  (. js/window.external invoke (clj->json arg)))

(defn ^:export init
  []
  (invoke {:op "init" :n 0}))

