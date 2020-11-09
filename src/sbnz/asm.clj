(ns sbnz.asm
  (:require [instaparse.core :as insta])
  (:require [instaparse.failure :as fail])
  (:require [clojure.java.io :refer [resource]])
  (:require [clojure.edn :as edn])
  (:require [clojure.string :as s])
  (:gen-class))

(defonce data-offset (atom 0))
(defonce data-mapping (atom {}))

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
    (reset! data-mapping mapping)
    (add-nums-and-vars memory mapping)))

(defn gen-output [outputs]
  (for [[_ output] outputs] 
    (if (= (first output) :var)
      (get @data-mapping output)
      (edn/read-string (second output)))))

(defn parse-sbnz [src]
  (try
    (let [parser (insta/parser (resource "sbnz.ip"))
          program (parser src)]
      program)
    (catch Exception _)))

(defn get-code-subset [program type]
  (filter #(= (first %) type) program))

(defn load-program [filename]
  (if-let [parsed (parse-sbnz (slurp filename))]
    (if (not (insta/failure? parsed))
      [(gen-code (get-code-subset parsed :sbnz))
       (gen-output (get-code-subset parsed :output))]
      (fail/pprint-failure parsed))
    nil))