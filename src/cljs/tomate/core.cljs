(ns tomate.core
  (:require
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]
   [reagent.session :as session]
   [reitit.frontend :as reitit]
   [clerk.core :as clerk]
   [accountant.core :as accountant]))

;; -------------------------
;; Routes

(def router
  (reitit/router
   [["/" :index]
    ["/items"
     ["" :items]
     ["/:item-id" :item]]
    ["/about" :about]]))

(defn path-for [route & [params]]
  (if params
    (:path (reitit/match-by-name router route params))
    (:path (reitit/match-by-name router route))))

;; -------------------------
;; Page components
(defn get-minutes [seconds]
  (.padStart (str (js/Math.floor (/ seconds 60))) 2 "0"))

(defn get-seconds [total-seconds]
  (.padStart (str (mod total-seconds 60)) 2 "0"))

(defn seconds->time [total-seconds]
  (str (get-minutes total-seconds) ":" (get-seconds total-seconds)))


(defn goal-input [the-goal is-timer]
  [:div.main
   [:h1 "Welcome to tomate"]
   [:input {:type "text"
            :placeholder "What are you going to do?"
            :autoFocus true
            :value @the-goal
            :on-change #(reset! the-goal (-> % .-target .-value))}]
   [:button {:type "button" :on-click #(reset! is-timer true)} "Start!"]])

(defn goal-timer [the-goal]
  (let [total-seconds (atom 1500)]
   (fn []
     [:div.timer
      [:h1 @the-goal]
      [:h1 (seconds->time @total-seconds)]])))

(defn home-page []
  (let [is-timer (atom false)
        the-goal (atom "Fix the bugs!!")]
    (fn [] (if @is-timer (goal-timer the-goal) (goal-input the-goal is-timer)))))

(defn items-page []
  (fn []
    [:span.main
     [:h1 "The items of tomate"]
     [:ul (map (fn [item-id]
                 [:li {:name (str "item-" item-id) :key (str "item-" item-id)}
                  [:a {:href (path-for :item {:item-id item-id})} "Item: " item-id]])
               (range 1 60))]]))

(defn item-page []
  (fn []
    (let [routing-data (session/get :route)
          item (get-in routing-data [:route-params :item-id])]
      [:span.main
       [:h1 (str "Item " item " of tomate")]
       [:p [:a {:href (path-for :items)} "Back to the list of items"]]])))

(defn about-page []
  (fn [] [:span.main
          [:h1 "About tomate"]]))


;; -------------------------
;; Translate routes -> page components


(defn page-for [route]
  (case route
    :index #'home-page
    :about #'about-page
    :items #'items-page
    :item #'item-page))


;; -------------------------
;; Page mounting component


(defn current-page []
  (fn []
    (let [page (:current-page (session/get :route))]
      [:div
       [:header
        [:p [:a {:href (path-for :index)} "Home"] " | "
         [:a {:href (path-for :about)} "About tomate"]]]
       [page]
       [:footer
        [:p "tomate was generated by the "
         [:a {:href "https://github.com/reagent-project/reagent-template"} "Reagent Template"] "."]]])))

;; -------------------------
;; Initialize app

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