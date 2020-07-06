(ns tomate.goal-input)

(defn goal-input [the-goal is-timer]
  [:div.goal-input
   [:input {:type "text"
            :class "form-control mb-3"
            :placeholder "What are you going to do?"
            :autoFocus true
            :value @the-goal
            :on-change #(reset! the-goal (-> % .-target .-value))
            :on-key-press (fn [e] (if (= 13 (.-charCode e)) (reset! is-timer true)))}]
   [:button {:type "button" :class "btn btn-primary" :on-click #(reset! is-timer true)} "Start!"]])
