(ns eu.docouto.c2048.views
  (:require
   [re-frame.core :as re-frame]
   [re-com.core :as re-com]
   [re-pressed.core :as rp]
   [eu.docouto.c2048.events :as events]
   [eu.docouto.c2048.subs :as subs]
   [eu.docouto.c2048.gameview :as gv]
   ))

(defn dispatch-keydown-rules []
  (re-frame/dispatch
   [::rp/set-keydown-rules
    {:event-keys [[[::events/set-re-pressed-example "Hello, world!"]
                   [{:keyCode 72} ;; h
                    {:keyCode 69} ;; e
                    {:keyCode 76} ;; l
                    {:keyCode 76} ;; l
                    {:keyCode 79} ;; o
                    ]]]

     :clear-keys
     [[{:keyCode 27} ;; escape
       ]]}]))

(defn display-re-pressed-example []
  (let [re-pressed-example (re-frame/subscribe [::subs/re-pressed-example])]
    [:div

     ;; [:p
     ;;  "Re-pressed is listening for keydown events. However, re-pressed
     ;;  won't trigger any events until you set some keydown rules."]

     ;; [:div
     ;;  [re-com/button
     ;;   :on-click dispatch-keydown-rules
     ;;   :label "set keydown rules"]]

     ;; [:p
     ;;  [:span
     ;;   "After clicking the button, you will have defined a rule that
     ;;   will display a message when you type "]
     ;;  [:strong [:code "hello"]]
     ;;  [:span ". So go ahead, try it out!"]]

     ;; [:p
     ;;  [:span
     ;;   "Este é o meu texto que se recarga sen saber por qué"]]

     (when-let [rpe @re-pressed-example]
       [re-com/alert-box
        :alert-type :info
        :body rpe])]))

(defn title []
  (let [name (re-frame/subscribe [::subs/name])]
    [re-com/title
     :label (str "2048")
     :level :level1]))

(defn main-panel []
  [re-com/v-box
   :height "100%"
   :children [[title]
              [display-re-pressed-example]
              [gv/game]
              ]])
