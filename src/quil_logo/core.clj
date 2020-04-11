(ns quil-logo.core
  (:require [quil.core :as q]
            [quil-logo.logo :as logo :refer [rpt run reset t fd lt rt pd pu pc]]))

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

(defn draw []
  (reset)
  (heart)
  (starni)
  )

(q/defsketch example
  :title "Logo"
  ;:settings #(q/smooth 2)
  :setup setup
  ;:renderer :p2d
  :draw draw
  :features [:resizable]
  :size [800 800])