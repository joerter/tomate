(ns tomate.goal-timer
  (:require
   [reagent.core :as reagent :refer [atom]]))

(defn get-minutes [seconds]
  (.padStart (str (js/Math.floor (/ seconds 60))) 2 "0"))

(defn get-seconds [total-seconds]
  (.padStart (str (mod total-seconds 60)) 2 "0"))

(defn seconds->time [total-seconds]
  (str (get-minutes total-seconds) ":" (get-seconds total-seconds)))

(defn goal-timer [the-goal]
  (let [total-seconds (atom 1500)]
   (fn []
     (js/setTimeout #(swap! total-seconds dec) 1000)
     [:div.timer
      [:h1 @the-goal]
      [:h1 (seconds->time @total-seconds)]])))
