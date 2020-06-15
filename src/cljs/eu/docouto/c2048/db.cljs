(ns eu.docouto.c2048.db
  (:require [eu.docouto.c2048.game :as game]))

(def default-db
  {:name "re-frame"
   :game (game/G2048Game->new)})
