(ns tomate.goal-input)

(defn goal-input [the-goal is-timer]
  [:div.main
   [:h1 "Welcome to tomate"]
   [:input {:type "text"
            :placeholder "What are you going to do?"
            :autoFocus true
            :value @the-goal
            :on-change #(reset! the-goal (-> % .-target .-value))}]
   [:button {:type "button" :on-click #(reset! is-timer true)} "Start!"]])
