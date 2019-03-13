(ns ui.db
  (:require [reagent.core :as r]
            [re-frame.core :as rf]))

;; (def app-db
;;   (r/atom {:time (js/Date.)
;;            :time-color "#f88"})) ;; a Reagent atom, containing a map

;; For comparison, here's how we would have written this if we'd cared about the existing value of db:

;; (rf/reg-event-db
;;  :initialize
;;  (fn [db _]                 ;; we use db this time, so name it
;;    (-> db
;;        (assoc :time (js/Date.))
;;        (assoc :time-color "#f88")))

(rf/reg-event-db          ;; sets up initial application state
 :initialize
 (fn [_ _]                 ;; the two parameters are not important here, so use _
   {:time (js/Date.)      ;; what it returns becomes the new application state
    :time-color "#f88"})) ;; so the application state will initially be a map of two keys

