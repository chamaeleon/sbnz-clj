(ns sbnz.asm
  (:require [instaparse.core :as insta])
  (:require [clojure.java.io :refer [resource]])
  (:require [clojure.edn :as edn])
  (:gen-class))

(defonce data-offset (atom 0))

(defn map-cell [mapping literal]
  (if (mapping literal) mapping
      (assoc mapping literal (swap! data-offset inc))))

(defn map-instr-cells [mapping entity-type instr]
  (reduce map-cell mapping (filter #(= (first %) entity-type) instr)))

(defn map-cells [mapping program entity-type]
  (reduce (fn [mapping instr] (map-instr-cells mapping entity-type instr))
          mapping
          (map rest (filter #(= (first %) :sbnz) program))))

(defn add-cell [memory mapping cell]
  (if (= (first cell) :mem)
    (conj memory (edn/read-string (second cell)))
    (conj memory (get mapping cell))))

(defn gen-code-instr [memory mapping instr]
  (let [[_ a b c d] instr]
    (-> memory
        (add-cell mapping a)
        (add-cell mapping b)
        (add-cell mapping c)
        (add-cell mapping d))))

(defn add-num [data cell-content]
  (assoc data
         (second cell-content)
         (edn/read-string (second (first cell-content)))))

(defn add-nums-and-vars [memory mapping]
  (let [zeroes (repeat (count mapping) 0)
        nums (filter #(= (first (first %)) :num) mapping)]
    (reduce add-num (vec (concat memory zeroes)) nums)))

(defn gen-code [program]
  (reset! data-offset (dec (* 4 (count program))))
  (let [mapping (map-cells {} program :num)
        mapping (map-cells mapping program :var)
        memory (reduce (fn [mem instr] (gen-code-instr mem mapping instr)) [] program)]
    (add-nums-and-vars memory mapping)))

(defn parse-sbnz [src]
  (let [parser (insta/parser (resource "sbnz.ip"))
        program (parser src)]
    program))

(defn load-program [filename]
  (gen-code (parse-sbnz (slurp filename))))