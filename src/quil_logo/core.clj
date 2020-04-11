(ns quil-logo.core
  (:require [quil.core :as q]
            [quil-logo.logo :as logo :refer [rpt run reset t bk fd lt rt pd pu pc]]))

(defn setup []
  (q/frame-rate 50)
  (q/background 200))

(defn starni []
  (run pd 14
       lt (* 100 (q/sin (* 1.4 (t))))
       rpt 14 [fd 5 rt 150]
       pu))

(defn heart []
  (run pd
       lt (* -60 (q/sin (* 1.4 (t))))

       rpt 4 [fd 1 rt 10]
       rpt 10 [fd 0.52 rt 20]
       lt 160
       rpt 10 [fd 0.52 rt 20]
       lt 10
       rpt 4 [fd 1 rt 10]
       pc 0))

(defn flower [c size]
  (run
   rpt 14 [pd rt 11.4
           rpt 5 [fd (* size 2) rt 4]
           rpt 6 [fd (* size 1) rt 30]
           rpt 5 [fd (* size 2) rt 4]
           pc (* c)]))

(defn draw []
  (reset)
  ;(heart)
  ;(starni)
  (run 
   lt (* 360 (q/sin (* 0.3 (t))))
   rpt 6 [lt 60
          fd 22
          flower (* i 15) 0.7
          bk 22])
  )

(q/defsketch example
  :title "Logo"
  :setup setup
  :draw draw
  :features [:resizable]
  :size [800 800])