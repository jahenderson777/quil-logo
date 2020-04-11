(ns quil-logo.logo
  (:require [quil.core :as q]))

(def dir-offset 270)
(def dir (volatile! 0))
(def x (volatile! 600))
(def y (volatile! 600))
(def col (volatile! 0))
(def pen-down (volatile! false))
(def shape-x (volatile! 0))
(def shape-y (volatile! 0))
(def pen-size (volatile! 1))

(defn setpc [c]
  (vreset! col c))

(defn reset []
  (q/background 88)
  (q/color-mode :hsb 100)
  (vreset! x (/ (q/width) 2))
  (vreset! y (/ (q/width) 2))
  (vreset! shape-x @x)
  (vreset! shape-y @y)
  (vreset! dir 0)
  (vreset! pen-size 1))

(defn fd [l]
  (vswap! x + (* 50 l (q/cos (q/radians (+ @dir dir-offset)))))
  (vswap! y + (* 50 l (q/sin (q/radians (+ @dir dir-offset)))))
  (q/vertex @x @y))

(defn bk [l]
  (fd (* -1 l)))

(def rpt identity)

(defn lt [a]
  (vswap! dir - a))

(defn rt [a]
  (vswap! dir + a))

(defn pu []
  (q/no-fill)
  (q/end-shape)
  (vreset! pen-down false))

(defn pc [c]
  (when @pen-down
    (q/vertex @shape-x @shape-y)
    (when (or (not= @x @shape-x)
              (not= @y @shape-y))
      (vreset! x @shape-x)
      (vreset! y @shape-y))
    (q/fill c 100 80 10)
    (q/end-shape)
    (vreset! pen-down false)))

(defn pd [& [pen-col]]
  (when-not @pen-down
    (q/begin-shape)
    (q/stroke (if pen-col
                pen-col 
                @col) 100 80)
    (vreset! shape-x @x)
    (vreset! shape-y @y)
    (q/vertex @shape-x @shape-y)
    (vreset! pen-down true)))

(defn t []
  (/ (q/millis) 1000.0))

(defn chop-up-cmds [cmds]
  (loop [split-cmds []
         cmd [(first cmds)]
         cmds (rest cmds)]
    (if (or (not (seq cmd))
            (not (seq cmds)))
      (map seq (if (seq cmd)
                 (conj split-cmds cmd)
                 split-cmds))
      
      (if (= (first cmd) 'rpt)
        (let [[iterations v next-cmd & rest-cmds] cmds]
          (recur (conj split-cmds (conj 
                                   (chop-up-cmds v)
                                   ['_ iterations]
                                   'dotimes))
                 (if next-cmd [next-cmd] [])
                 rest-cmds))
        (let [next-cmd (first cmds)]
          (if (or (fn? next-cmd)
                  (symbol? next-cmd))
            (recur (conj split-cmds cmd)
                   [next-cmd]
                   (rest cmds))
            (recur split-cmds
                   (conj cmd next-cmd)
                   (rest cmds))))))))

(defmacro run [& cmds]
  `(do ~@(chop-up-cmds cmds)))