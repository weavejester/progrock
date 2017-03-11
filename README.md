# Progrock

[![Build Status](https://travis-ci.org/weavejester/progrock.svg)](https://travis-ci.org/weavejester/progrock)

A functional Clojure progress bar for the command line.


## Installation

Add the following dependency to your `project.clj`:

    [progrock "0.1.2"]


## Usage

Require the Progrock namespace:

```clojure
(require '[progrock.core :as pr])
```

Then create a new progress bar:

```clojure
(def bar (pr/progress-bar 100))
```

The progress bar is just a map of data:

```clojure
{:progress 0, :total 100, :done? false, :creation-time 1439141590081}
```

You can alter the progress bar like any data structure, but Progrock
supplies two functions to make it a little easier. `tick` increments
the progress by a certain amount, and `done` marks the progress bar as
done.

```clojure
(pr/tick bar 10)
;;=> {:progress 10, :total 100, :done? false, :creation-time 1439141590081}
(pr/done bar)
;;=> {:progress 0 :total 100, :done? true, :creation-time 1439141590081}
```

You can render the progress bar as a string:

```clojure
(pr/render (pr/tick bar 25))
;;=> " 25/100    25% [============                                      ]  ETA: 00:00"
```

Or print the progress bar directly:

```clojure
(pr/print (pr/tick bar 25))
;; 25/100    25% [============                                      ]  ETA: 00:00
```

Printing the progress bar multiple times will overwrite the previous
one, allowing it to be animated. For example:

```clojure
(loop [bar (pr/progress-bar 100)]
  (if (= (:progress bar) (:total bar))
    (pr/print (pr/done bar))
    (do (Thread/sleep 100)
        (pr/print bar)
        (recur (pr/tick bar)))))
```

You can also extensively customize the progress bar:

```clojure
(pr/print (pr/tick bar 25)
          {:length 20, :format "|:bar| :progress/:total", :complete \#})
;; |#####               |  25/100
```


## Documentation

* [API Docs](https://weavejester.github.io/progrock/progrock.core.html)


## License

Copyright © 2017 James Reeves

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
