(ns progrock.core
  (:require [clojure.string :as str]))

(defn progress-bar [total]
  {:progress 0, :total total, :done? false})

(defn tick
  ([bar]
   (tick bar 1))
  ([bar amount]
   (update-in bar [:progress] + amount)))

(defn done [bar]
  (assoc bar :done? true))

(defn- keyword-replace [string keywords]
  (reduce-kv #(str/replace %1 (str %2) (str %3)) string keywords))

(defn- bar-text [{:keys [progress total]} {:keys [length]}]
  (let [completed (int (* (/ progress total) length))]
    (str (apply str (repeat completed "="))
         (apply str (repeat (- length completed) " ")))))

(def default-options
  {:length 50
   :format "[:bar] :progress/:total"})

(defn as-string
  ([bar]
   (as-string bar {}))
  ([bar options]
   (let [options (merge default-options options)]
     (keyword-replace
      (:format options)
      {:bar      (bar-text bar options)
       :progress (str (:progress bar))
       :total    (str (:total bar))}))))

(defn print-progress
  ([bar]
   (print-progress bar {}))
  ([bar options]
   (print (str "\r" (as-string bar options)))
   (flush)
   (when (:done? bar) (println))))
