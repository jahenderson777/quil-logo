(ns quil-logo.core
  (:require [quil.core :as q]
            [quil-logo.logo :as logo :refer 
             [setdir setscale setsat setps setbri setalpha defproc rpt run reset 
              t bk fd lt rt pd pu pc fc]]))

(defproc starni []
  pd 14
  lt (* 100 (q/sin (* 1.4 (t))))
  rpt 14 [fd 5 rt 150]
  pu)

(defproc heart []
  pd
  lt (* -10 (q/sin (* 1.4 (t))))

  rpt 4 [fd 1 rt 10]
  rpt 10 [fd 0.52 rt 20]
  lt 160
  rpt 10 [fd 0.52 rt 20]
  lt 10
  rpt 4 [fd 1 rt 10]
  rt 50
  pc 0)

(defproc flower [c size]
  rpt 14 [pd rt 11.4
          rpt 5 [fd (* size 2) rt 4]
          rpt 6 [fd (* size 1) rt 30]
          rpt 5 [fd (* size 2) rt 4]
          pc c])

(defproc spinning-flowers []
  lt (* 20 (q/sin (* 0.3 (t))))
  rpt 6 [lt 60
         fd 32
         flower (* i 15) 0.7
         bk 32])

(defproc leaf-cut-big [c]
  rt 30
  pd
  fd 2 rt 10 fd 2
  rpt 14 [fd 1.6 lt 120 fd 3.5 rt 168 fd 3.9 lt 56]
  fd 1.9 lt 50
  rpt 14 [lt 56 fd 3.9 rt 167 fd 3.5 lt 120 fd 1.6]
  lt 120
  pc c)

(defproc leaf [c]
  rt 30
  pd fd 2 rt 10 fd 2
  rpt 13 [fd 1.99 lt (- 10 (/ i 4))]
  lt 63
  rpt 13 [fd 1.9 lt (+ 6 (/ i 2))]
  pc c
  lt 114)

(defproc leaf-cut-small [c]
  rt 30
  pd
  fd 2 rt 10 fd 2
  rpt 8 [fd 1.6 lt 120 fd 3.5 rt 168 fd 3.9 lt 59]
  fd 1.9 lt 56
  rpt 9 [lt 61 fd 3.9 rt 167 fd 3.5 lt 120 fd 1.6]
  lt 110
  pc c)

(defproc leaf-full []
  leaf-cut-big 29
  fd 2
  leaf 20
  setsat 0
  setbri 0
  leaf-cut-small 3
  setsat 80
  setbri 100
  fd 1.6
  setscale 6
  lt 20
  leaf 30
  setscale 5
  lt 0
  leaf 20
  setscale 10)

(defproc draw2 []
  reset
  setps 0
  setalpha 40
  setbri 100
  setscale 13
  spinning-flowers
  setscale 30
  
  lt 40
  heart
  setscale 20
  heart
  setalpha 80
  lt 70
  setscale 10
  lt 90
  leaf-full
  bk 1 rt 150 fd 2
  leaf-full
  ;(setps 0)
  
  ;(heart)
  ;(starni)
  #_(run))


(defproc slope []
  lt -70 bk 8 pd fd 16 rt 120 fd 16 pc 40)

(def state (atom {:bg nil
                  :player-x 95
                  :player-y 405
                  :dir 0
                  :hit-table (logo/hit-table 20)
                  :speed 6}))

(defn draw-player []
  (let [{:keys [player-x player-y]} @state]
    (q/ellipse-mode :center)
    (q/ellipse player-x player-y 20 20)))

(defn update-state []
  (swap! state (fn [{:keys [speed dir] :as state}]
                 (-> state
                     (update :player-x + (* speed (q/cos dir)))
                     (update :player-y + (* speed (q/sin dir)))))))

(defn get-pixel [px x y]
  (let [idx (+ (int x) (* (int y) 800))]
    (aget px idx)))

(defn draw []
  (reset)
  (slope)
  (q/rect-mode :center)
  (q/rect 400 400 770 770)
  ;(spinning-flowers)
  (update-state)
  (let [{:keys [player-x player-y bg hit-table dir]} @state
        px (q/pixels)]
    (when-not bg 
      (swap! state assoc :bg 
             (get-pixel px player-x player-y))) 
    (let [bg (:bg @state)
          {:keys [sum-cx sum-cy cnt]} 
          (reduce (fn [m {:keys [dx dy cx cy]}]
                    (let [test-pixel (get-pixel px
                                                (+ player-x dx)
                                                (+ player-y dy))]
                      (if (not= test-pixel bg)
                        (do ;(println dx dy cx cy)
                          (-> m
                              (update :sum-cx + cx)
                              (update :sum-cy + cy)
                              (update :cnt inc)))
                        m)))
                  {:sum-cx 0
                   :sum-cy 0
                   :cnt 0}
                  hit-table)
          angle (q/atan2 sum-cy sum-cx)
          new-dir (- (* 2 (+ angle q/HALF-PI)) 
                     dir
                     )]
      (draw-player)
      (when (pos? cnt)
        (println (q/degrees angle) (q/degrees new-dir))
        (swap! state assoc :dir new-dir)
        (update-state)
        )
      ))
  )

(q/defsketch logo
  :title "Logo"
  :setup #(q/frame-rate 50)  
  :draw draw
  :features [:resizable]
  :size [800 800])

