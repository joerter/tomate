(ns tomate.goal-timer
  (:require
   [reagent.core :as reagent :refer [atom]]))

(defn get-minutes [milliseconds]
  (.padStart (str (js/Math.floor (/ milliseconds 60000))) 2 "0"))

(defn get-seconds [milliseconds]
  (.padStart (str (mod milliseconds 60000)) 2 "0"))

(defn milliseconds->time [total-milliseconds]
  (str (get-minutes total-milliseconds) ":" (get-seconds total-milliseconds)))

(defn get-end-time [] (+ 1500000 (.getTime (js/Date.))))

(defn milliseconds-from-end-time [end-time]
  (let [current-time (.getTime (js/Date.))]
    (- end-time current-time)))

(defn goal-timer [the-goal]
  (let [total-milliseconds (atom 1500000) end-time (get-end-time)]
    (fn []
      ;; (js/console.log "seconds from end time:" (seconds-from-end-time end-time))
      (js/setTimeout #(reset! total-milliseconds (milliseconds-from-end-time end-time)) 1000)
      [:div.timer
       [:h1 @the-goal]
       [:h1 (milliseconds->time @total-milliseconds)]])))
