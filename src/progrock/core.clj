(ns progrock.core
  "A namespace for generating textual progress bars in a functional way."
  (:refer-clojure :exclude [print])
  (:require [clojure.core :as core]
            [clojure.string :as str]))

(defn progress-bar
  "Create an immutable data structure representing a progress bar. The ending
  total must be supplied, along with a keyword representing the current state.
  The state can be anything, but will be used to determine how to display the
  progress bar."
  [total state]
  {:progress 0, :total total, :done? false, :state state})

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
  (let [completed-length (int (* (/ progress total) length))]
    (str (apply str (repeat completed-length complete))
         (apply str (repeat (- length completed-length) incomplete)))))

(defn- align-right [text size]
  (str (apply str (repeat (- size (count text)) \space)) text))

(def default-profile
  "A map of default options for a profile used in as-string."
  {:length 50
   :format "[:bar] :progress/:total"
   :complete \=
   :incomplete \space})

(defn render
  "Render a progress bar as a string. Takes an optional map of profiles, which
  connects state keywords to maps of display options. The following display
  options are allowed:

    :format     - a format string for the progress bar
    :length     - the length of the bar
    :complete   - the character to use for a completed chunk
    :incomplete - the character to use for an incomplete chunk"
  ([bar]
   (render bar {}))
  ([bar profiles]
   (let [options (merge default-profile ((:state bar) profiles))]
     (keyword-replace
      (:format options)
      {:bar      (bar-text bar options)
       :progress (align-right (str (:progress bar)) (count (str (:total bar))))
       :total    (str (:total bar))}))))

(defn print
  "Prints a progress bar, overwriting any existing progress bar on the same
  line. If the progress bar is done, a new line is printed."
  ([bar]
   (print bar {}))
  ([bar options]
   (core/print (str "\r" (render bar options)))
   (flush)
   (when (:done? bar) (newline))))
