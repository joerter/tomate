(ns tomate.core
  (:require
   [tomate.goal-timer :refer [goal-timer]]
   [tomate.goal-input :refer [goal-input]]
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]
   [reagent.session :as session]
   [reitit.frontend :as reitit]
   [clerk.core :as clerk]
   [accountant.core :as accountant]))

(def router
  (reitit/router
   [["/" :index]
    ]))

(defn home-page []
  (let [is-timer (atom false)
        the-goal (atom "Fix the bugs!!")]
    (fn [] (if @is-timer (goal-timer the-goal) (goal-input the-goal is-timer)))))

(defn page-for [route]
  (case route
    :index #'home-page
    ))

(defn current-page []
  (fn []
    (let [page (:current-page (session/get :route))]
      [:div
       [page]
       ])))

(defn mount-root []
  (rdom/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (clerk/initialize!)
  (accountant/configure-navigation!
   {:nav-handler
    (fn [path]
      (let [match (reitit/match-by-path router path)
            current-page (:name (:data  match))
            route-params (:path-params match)]
        (reagent/after-render clerk/after-render!)
        (session/put! :route {:current-page (page-for current-page)
                              :route-params route-params})
        (clerk/navigate-page! path)))
    :path-exists?
    (fn [path]
      (boolean (reitit/match-by-path router path)))})
  (accountant/dispatch-current!)
  (mount-root))
