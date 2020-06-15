(ns eu.docouto.c2048.gameview
  (:require [eu.docouto.c2048.game :as game]
            [re-frame.core :as rf]
            [re-com.core :as rc]
            [re-pressed.core :as rp]
            [eu.docouto.c2048.events :as events]
            [eu.docouto.c2048.subs :as subs]))

(rf/reg-sub ::game (fn [db] (:game db)))

(rf/reg-event-db
 ::move
 (fn [db [_ value]]
   (assoc db :game (game/move (:game db) value))))

(rf/dispatch
 [::rp/set-keydown-rules
  {
   :event-keys
   [
    [[::move :left]
     [{:keyCode 37}]]
    [[::move :up]
     [{:keyCode 38}]]
    [[::move :right]
     [{:keyCode 39}]]
    [[::move :down]
     [{:keyCode 40}]]]}])

(def colors {:background  :#ffcc99
             :blank :#ffe6cc
             2 :lightsalmon
             4 :lightblue
             8  :#e6ffb3
             16 :coral
             32 :blue
             64 :green
             128 :red
             256 :orange
             512 :lightblue
             1024 :ligthgreen
             2048 :lightred
             4096 :blue})

(defn cell-color [val]
  (get colors val (:blank colors)))

(defn- cell [val]
  (let [t (if (nil? val) " " val)]
    [:td
     [:div {:style
            {:width :4em
             :height :4em
             :line-height :4em
             :text-align :center
             :background-color (cell-color t)
             :border-color :red
             :border-width :2px
             :padding :4px
             :margin :4px
             :color :black
             :font-weight :bold
             :font-size :1.5em
             :border-radius :0.5em}}
      t]]))

(defn- row [row] [:tr (map (fn [c] (cell c)) row)])

(defn- board [tiles]
  (let [rows (partition 4 tiles)]
    [:table {:style {:background-color (:background colors)
                     :margin :2em}}
     (map row rows)]))

(defn buttons []
  [:table
   [:tr
    [:td]
    [:td [rc/button :label "up" :on-click #(rf/dispatch [::move :up])]]
    [:td]]
   [:tr
    [:td [rc/button :label "left" :on-click #(rf/dispatch [::move :left])]]
    [:td]
    [:td [rc/button :label "right" :on-click #(rf/dispatch [::move :right])]]]
   [:tr
    [:td]
    [:td [rc/button :label "down" :on-click #(rf/dispatch [::move :down])]]
    [:td]]])

(defn game []
  [:div
   [:div
    [board (game/tiles @(rf/subscribe [::game]))]]
   [buttons]])
