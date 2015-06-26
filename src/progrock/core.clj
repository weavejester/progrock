(ns progrock.core)

(defn progress-bar [total]
  {:progress 0, :total total, :done? false})

(defn tick
  ([bar]
   (tick bar 1))
  ([bar amount]
   (update-in bar [:progress] + amount)))

(defn done [bar]
  (assoc bar :done? true))

(defn as-string
  ([bar]
   (as-string bar {}))
  ([bar options]
   (let [completed       (/ (:progress bar) (:total bar))
         bar-length      (:length options 50)
         complete-length (int (* completed bar-length))]
     (str "["
          (apply str (repeat complete-length "="))
          (apply str (repeat (- bar-length complete-length) " "))
          "]"))))

(defn print-progress [bar]
  (print (str "\r" (as-string bar)))
  (flush)
  (when (:done? bar) (println)))
