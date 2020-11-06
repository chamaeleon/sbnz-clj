(ns sbnz.vm
  (:require [clojure.string :as s])
  (:require [clojure.edn :as edn])
  (:gen-class))

(defonce memory (atom (make-array Integer/TYPE (* 1024 1024))))
(defonce pc (atom 0))

(defn mem-read [addr]
  (aget @memory addr))

(defn mem-write [addr value]
  (aset @memory addr value))

(defn mem-load [filename]
  (->> (map edn/read-string (s/split (slurp filename) #" "))
       (map-indexed (fn [idx v] (mem-write idx v)))))

(defn mem-save [filename cells]
  (spit filename (s/join " " (take cells @memory))))

(defn branch [addr]
  (swap! pc addr))

(defn next-instruction []
  (swap! pc + 4))

(defn run []
  (let [[a b c d] (for [i (range 4)] (mem-read (+ @pc i)))
        result (- (read a) (mem-read b))]
    (mem-write c result)
    (if (zero? result) (branch d) (next-instruction))
    (when (>= @pc 0) (recur))))