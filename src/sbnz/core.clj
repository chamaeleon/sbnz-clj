(ns sbnz.core
  (:require [sbnz.vm :refer [mem-save mem-set mem-read run]])
  (:require [sbnz.asm :refer [load-program]])
  (:gen-class))

(defn -main
  [& args]
  (let [[program outputs] (load-program (first args))]
    (when program
      (mem-set program)
      (run)
      (doseq [output outputs]
        (println (mem-read output)))
      (when (second args)
        (mem-save (second args) (count program))))))
