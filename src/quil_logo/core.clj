(ns quil-logo.core
  (:require [quil.core :as q]
            [quil-logo.logo :as logo :refer 
             [setsat setps setbri setalpha defproc rpt run reset 
              t bk fd lt rt pd pu pc fc]]))

(defproc starni []
  pd 14
  lt (* 100 (q/sin (* 1.4 (t))))
  rpt 14 [fd 5 rt 150]
  pu)

(defproc heart []
  pd
  lt (* -60 (q/sin (* 1.4 (t))))

  rpt 4 [fd 1 rt 10]
  rpt 10 [fd 0.52 rt 20]
  lt 160
  rpt 10 [fd 0.52 rt 20]
  lt 10
  rpt 4 [fd 1 rt 10]
  pc 0)

(defproc flower [c size]
  rpt 14 [pd rt 11.4
          rpt 5 [fd (* size 2) rt 4]
          rpt 6 [fd (* size 1) rt 30]
          rpt 5 [fd (* size 2) rt 4]
          pc c])

(defproc spinning-flowers []
  lt (* 360 (q/sin (* 0.3 (t))))
  rpt 6 [lt 60
         fd 22
         flower (* i 15) 0.7
         bk 22])

(defproc draw []
  reset
  setps 0
  setalpha 100
  setbri 100
  rt 30
  pd
  rpt 6 [fd 2 lt 120 fd 1.5 rt 160 fd 1.9 lt 60]
  fd 1.9 lt 50
  rpt 6 [lt 60 fd 1.9 rt 160 fd 1.5 lt 120 fd 2]
  pc 27
  ;(setps 0)
  
  ;(heart)
  ;(starni)
  #_(run 
     )
  )

(q/defsketch logo
  :title "Logo"
  :setup #(q/frame-rate 50)  
  :draw draw
  :features [:resizable]
  :size [800 800])