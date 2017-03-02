(ns progrock.core
  "A namespace for generating textual progress bars in a functional way."
  (:refer-clojure :exclude [print])
  (:require [clojure.core :as core]
            [clojure.string :as str]))

(defn progress-bar
  "Create an immutable data structure representing a progress bar for the
  specified total."
  [total]
  {:progress 0
   :total total
   :done? false
   :creation-time (System/currentTimeMillis)})

(defn tick
  "Return a new progress bar that has been incremented by the supplied amount,
  or by 1, if no amount was supplied."
  ([bar]
   (tick bar 1))
  ([bar amount]
   (update-in bar [:progress] + amount)))

(defn done
  "Return a new progress bar that has been marked as 'done'."
  [bar]
  (assoc bar :done? true))

(defn- keyword-replace [string keywords]
  (reduce-kv #(str/replace %1 (str %2) (str %3)) string keywords))

(defn- bar-text [{:keys [progress total]} {:keys [length complete incomplete]}]
  (let [completed-ratio  (if (pos? total) (/ progress total) 0)
        completed-length (int (* completed-ratio length))]
    (str (apply str (repeat completed-length complete))
         (apply str (repeat (- length completed-length) incomplete)))))

(defn- align-right [text size]
  (str (apply str (repeat (- size (count text)) \space)) text))

(defn- percent [x total]
  (if (pos? total) (int (* 100 (/ x total))) 0))

(defn- interval-str [milliseconds]
  (if (nil? milliseconds)
    "--:--"
    (let [seconds (mod (int (/ milliseconds 1000)) 60)
          minutes (int (/ milliseconds 60000))]
      (format "%02d:%02d" minutes seconds))))

(defn- elapsed-time [{:keys [creation-time]}]
  (- (System/currentTimeMillis) creation-time))

(defn- remaining-time [{:keys [progress total] :as bar}]
  (let [elapsed (elapsed-time bar)]
    (if (and (pos? progress) (pos? total))
      (- (/ elapsed (/ progress total)) elapsed))))

(def default-render-options
  "A map of default options for the render function."
  {:length 50
   :format ":progress/:total   :percent% [:bar]  ETA: :remaining"
   :complete \=
   :incomplete \space})

(defn render
  "Render a progress bar as a string. Takes an optional map of profiles, which
  connects state keywords to maps of display options. The following display
  options are allowed:

    :format     - a format string for the progress bar
    :length     - the length of the bar
    :complete   - the character to use for a completed chunk
    :incomplete - the character to use for an incomplete chunk

  The format string determines how the bar is displayed, with the following
  subsitutions:

    :bar       - the progress bar itself
    :progress  - the number of complete items
    :total     - the total number of items
    :percent   - the percentage done
    :elapsed   - the elapsed time in minutes and seconds
    :remaining - the estimated remaining time in minutes and seconds"
  ([bar]
   (render bar {}))
  ([{:keys [state progress total] :as bar} options]
   (let [options (merge default-render-options options)]
     (keyword-replace
      (:format options)
      {:bar       (bar-text bar options)
       :progress  (align-right (str progress) (count (str total)))
       :total     (str total)
       :percent   (align-right (str (percent progress total)) 3)
       :elapsed   (interval-str (elapsed-time bar))
       :remaining (interval-str (remaining-time bar))}))))

(defn print
  "Prints a progress bar, overwriting any existing progress bar on the same
  line. If the progress bar is done, a new line is printed."
  ([bar]
   (print bar {}))
  ([bar options]
   (core/print (str "\r" (render bar options)))
   (when (:done? bar) (newline))
   (flush)))
