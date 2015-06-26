(ns progrock.core)

(defn progress-bar
  [{:keys [total length]}]
  (fn [progress]
    (let [completed       (/ progress total)
          complete-length (int (* completed length))]
      (str "["
           (apply str (repeat complete-length "="))
           (apply str (repeat (- length complete-length) " "))
           "]"))))
