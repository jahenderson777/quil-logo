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

(defproc draw []
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


(q/defsketch logo
  :title "Logo"
  :setup #(q/frame-rate 50)  
  :draw draw
  :features [:resizable]
  :size [800 800])

