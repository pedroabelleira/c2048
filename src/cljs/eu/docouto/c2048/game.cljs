(ns eu.docouto.c2048.game
  (:require [better-cond.core :as b]))

;; Utilities
(defn- exp [x n] (reduce * (repeat n x)))

;; Definitions
(def dirs #{:up :down :left :right})

(defprotocol I2048Game
  "Represents a 2048 game engine"
  (move [_ dir]
    "Moves the board in the given direction (one of the values of 'dirs')")
  (finished? [_]
    "Returns true if the game is finished, false otherwise")
  (tiles [_]
    "Returns a vector of 4 vectors representing the values of the cells,
     from top to bottom and left to right")
  (reset [_]
    "Resets the value of the game to an initial state (one random cell
     with non nil value)"))

(defn- -random-tile-value [board] 2) ;; FIXME

(defn- -add-random-tile [tiles]
  (let [ind-tiles (map-indexed (fn [index t] [index t]) tiles)
        nil-tiles (filter #(nil? (second %)) ind-tiles)
        num-nil   (count nil-tiles)
        rnd-el    (if (= 0 num-nil) nil (rand-nth nil-tiles))
        rnd-ind   (if (nil? rnd-el) nil (first rnd-el))]
    (if (nil? rnd-el)
      tiles
      (assoc-in tiles [rnd-ind] (-random-tile-value tiles)))))

(defn- -tiles->vectors [dir tiles]
  (cond
    (= dir :left) (partition 4 tiles)
    (= dir :right) (map reverse (-tiles->vectors :left tiles))
    (= dir :up) (for [i (range 4)] (take-nth 4 (drop i tiles)))
    (= dir :down) (map reverse (-tiles->vectors :up tiles))
    :else nil))

(defn- -vectors->tiles [dir vectors]
  (cond
    (= dir :left) (vec (reduce concat vectors))
    (= dir :right) (-vectors->tiles :left (map reverse vectors))
    (= dir :up) (vec (reduce concat (apply mapv list vectors)))
    (= dir :down) (-vectors->tiles :up (map reverse vectors))
    :else nil))

(defn- -sum-row
  "Merges adjacent tiles with the same values. Assumes there are no
  nils interleaved with values (i.e. row was already compacted)"
  [[a b & r]]
  (->> (cond
         (or (nil? a) (nil? b)) (concat [a b] r)
         (= a b) (-> (+ a b)
                     (list)
                     (concat (-sum-row (vec r)))
                     (concat (list nil))
                     (vec))
         :else (-> (list a)
                   (concat (-sum-row (concat (list b) r)))))
       (take 4)
       (vec)))

(defn- -compact-row
  "Moves the tiles of the row to the left (towards the start)"
  [row]
  (let [row-len   (count row)
        compacted (->> row (filter #(not (nil? %))))
        pad-len   (- row-len (count compacted))
        pad       (repeat pad-len nil)]
    (vec (concat compacted pad))))

;; Moves elements in the row towards the beggining (the "left")
(defn- -move-row [row] (-> row -compact-row -sum-row))
;; Moves the whole board to the left (by moving each row)
(defn- -move-tiles [tiles] (map -move-row tiles))

(defn- -move-board
  "Moves the tiles in the board in the given direction dir
  (:left :right :up or :down)"
  [dir tiles]
  (->> (-tiles->vectors dir tiles)
       (-move-tiles)
       (-vectors->tiles dir)
       (-add-random-tile)))

(defn- -finished? [tiles]
  (and (= 0 (->> (filter nil? tiles) (count)))
       (= tiles (->> tiles (-move-board :left) (-move-board :down)))))

(defrecord G2048Game [tiles]
  I2048Game
  (finished? [this] (-finished? tiles))
  (move [this dir] (assoc this :tiles (-move-board dir tiles)))
  (tiles [this] (:tiles this))
  (reset [this] (assoc this :tiles (-add-random-tile (vec (repeat 16 nil))))))

(defn G2048Game->new []
  (reset (->G2048Game nil)))
