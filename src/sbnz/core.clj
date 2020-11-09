(ns sbnz.core
  (:require [sbnz.vm :refer [mem-load mem-save mem-set run]])
  (:require [sbnz.asm :refer [load-program]])
  (:gen-class))

(defn -main
  [& args]
  (let [program (load-program (first args))]
    (mem-set program)
    (run)
    (mem-save (second args) (count program))))
