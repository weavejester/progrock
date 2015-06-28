(ns progrock.core
  (:require [clojure.string :as str]))

(defn progress-bar [total state]
  {:progress 0, :total total, :done? false, :state state})

(defn tick
  ([bar]
   (tick bar 1))
  ([bar amount]
   (update-in bar [:progress] + amount))
  ([bar amount state]
   (-> bar (tick amount) (assoc :state state))))

(defn done
  ([bar]
   (assoc bar :done? true))
  ([bar state]
   (-> (done bar) (assoc :state state))))

(defn- keyword-replace [string keywords]
  (reduce-kv #(str/replace %1 (str %2) (str %3)) string keywords))

(defn- bar-text [{:keys [progress total]} {:keys [length complete incomplete]}]
  (let [completed-length (int (* (/ progress total) length))]
    (str (apply str (repeat completed-length complete))
         (apply str (repeat (- length completed-length) incomplete)))))

(defn- align-right [text size]
  (str (apply str (repeat (- size (count text)) \space)) text))

(def default-options
  {:length 50
   :format "[:bar] :progress/:total"
   :complete \=
   :incomplete \space})

(defn as-string
  ([bar]
   (as-string bar {}))
  ([bar profiles]
   (let [options (merge default-options ((:state bar) profiles))]
     (keyword-replace
      (:format options)
      {:bar      (bar-text bar options)
       :progress (align-right (str (:progress bar)) (count (str (:total bar))))
       :total    (str (:total bar))}))))

(defn print-progress
  ([bar]
   (print-progress bar {}))
  ([bar options]
   (print (str "\r" (as-string bar options)))
   (flush)
   (when (:done? bar) (println))))
