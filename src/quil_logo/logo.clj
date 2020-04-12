(ns quil-logo.logo
  (:require [quil.core :as q])
  )

(def dir-offset 270)
(def dir (volatile! 0))
(def x (volatile! 600))
(def y (volatile! 600))
(def scale (volatile! 10))

(def hue (volatile! 0))
(def sat (volatile! 0))
(def bri (volatile! 0))
(def alpha (volatile! 0))

(def pen-down (volatile! false))
(def shape-x (volatile! 0))
(def shape-y (volatile! 0))
(def pen-size (volatile! 1))

(defn setpc [c]
  (vreset! hue c))

(defn setscale [s]
  (vreset! scale s))

(defn setps [s]
  (vreset! pen-size s))

(defn setalpha [a]
  (vreset! alpha a))

(defn setsat [s]
  (vreset! sat s))

(defn setbri [b]
  (vreset! bri b))

(defn setdir [d]
  (vreset! dir d))

(defn reset []
  (q/background 100)
  (q/color-mode :hsb 100)
  (vreset! x (/ (q/width) 2))
  (vreset! y (/ (q/height) 2))
  (vreset! shape-x @x)
  (vreset! shape-y @y)
  (vreset! dir 0)
  (vreset! scale 10)
  (vreset! pen-size 1)
  (vreset! hue 0)
  (vreset! sat 70)
  (vreset! bri 80)
  (vreset! alpha 10))

(defn fd [l]
  (vswap! x + (* @scale l (q/cos (q/radians (+ @dir dir-offset)))))
  (vswap! y + (* @scale l (q/sin (q/radians (+ @dir dir-offset)))))
  (q/vertex @x @y))

(defn fc [l cdir cl cdir2 cl2]
  (let [new-x (+ @x (* @scale l (q/cos (q/radians (+ @dir dir-offset)))))
        new-y (+ @y (* @scale l (q/sin (q/radians (+ @dir dir-offset)))))
        
        cx (+ @x (* @scale cl (q/cos (q/radians (+ cdir dir-offset)))))
        cy (+ @y (* @scale cl (q/sin (q/radians (+ cdir dir-offset)))))
        
        cx2 (+ new-x (* @scale cl2 (q/cos (q/radians (+ cdir2 dir-offset)))))
        cy2 (+ new-y (* @scale cl2 (q/sin (q/radians (+ cdir2 dir-offset)))))
        ]
    (q/bezier-vertex cx cy cx2 cy2 new-x new-y)
    (vreset! x new-x)
    (vreset! y new-y)))

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
    (q/fill c @sat @bri @alpha)
    (q/end-shape)
    (vreset! pen-down false)))

(defn pd [& [pen-col]]
  (when-not @pen-down
    (q/begin-shape)
    (if (pos? @pen-size)
      (q/stroke (if pen-col
                  pen-col 
                  @hue) 100 80)
      (q/no-stroke))
    (vreset! shape-x @x)
    (vreset! shape-y @y)
    (q/vertex @shape-x @shape-y)
    (vreset! pen-down true)))

(defn t []
  (/ (q/millis) 1000.0))

(defn chop-up-cmds [cmds local-vars]
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
                                   (chop-up-cmds v local-vars)
                                   ['i iterations]
                                   'dotimes))
                 (if next-cmd [next-cmd] [])
                 rest-cmds))
        (let [next-cmd (first cmds)]
          (if (or (fn? next-cmd)
                  (and (symbol? next-cmd)
                       (not (contains? (into #{} local-vars) next-cmd))))
            (recur (conj split-cmds cmd)
                   [next-cmd]
                   (rest cmds))
            (recur split-cmds
                   (conj cmd next-cmd)
                   (rest cmds))))))))

(defmacro run [& cmds]
  `(do ~@(chop-up-cmds cmds [])))


(defmacro defproc [name args & cmds]
  `(defn ~name ~args
     (do ~@(chop-up-cmds cmds args))))

(defn hit-table [radius]
  (let [r (range (* -1 radius)
                 (inc radius))]
    (->> (for [x r
               y r
               :let [l (q/sqrt (+ (q/sq y) (q/sq x)))]]
           (when (<= l 5)
             {:x x
              :y y
              :l l
              :angle (if (zero? x)
                       (if (zero? y)
                         nil
                         (if (pos? y)
                           q/HALF-PI
                           (* -1 q/HALF-PI)))
                       (q/atan (/ y x)))}))
         (remove nil?))))