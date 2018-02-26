# sm2-plus

A Clojure implementation of the [SM2+ algorithm](http://www.blueraja.com/blog/477/a-better-spaced-repetition-learning-algorithm-sm2) for spaced repetition, which is an improved version of the standard [SuperMemo 2](https://www.supermemo.com/english/ol/sm2.htm) algorithm. This version is done by [BlueRaja](http://www.blueraja.com/blog/author/blueraja). Tip 'o the hat to [lo-tp](https://github.com/lo-tp/sm2-plus)

## Usage
This library is intended to provide an easy way to add spaced interval learning to any items that you would like to learn.

The only function that you should need to use is `calculate`.

At minimum it takes a rating, this is a value from 0.0 - 1.0, which represents the confidence that the learner has in recognizing the term, how familiar they are with the item. A 0.6 or higher is considered a correct answer by the algorithm. The rating is all that you would pass in the first time a word was presented.

`calculate` returns a map of calculated values:

```clojure
{:difficulty 0.7705882352941176,
 :interval 2,
 :percent-overdue 1.0,
 :updated "2018-02-23",
 :due-date "2018-02-25"}
```

This value should be serialzied/saved with the item to be learned. Each subsequent time calculate should be called with the previous value map, which will then be updated.

```clojure
(calculate rating learn-map)
```

`calculate` can also take a date, representing the today or the date on which the calculation is occurring. You don't need to pass a date as it will assume today by default. But you might want to pass in a date for testing purpose or to generate speculative values. The date can be:

- in milliseconds from the epoch, e.g. `(System/currentTimeMillis)`
- a java.util.Date, e.g. `(java.util.Date.)`
- a java.time.Instant, e.g. `(java.time.Instant/now)`
- a string "2018-02-18T10:15:30.00Z" or "2018-02-18")

## Example

```clojure
(ns quiz
  (:require [sm2-plus :refer [calculate]]))

(def new-pair {:german "Abgemacht!"
               :english "It's a deal"})

(defn update-pair [rating pair]
  (update pair :learn-map #(sm2-plus/calculate rating %)))


(def updated-once (update-pair 0 new-pair))

{:german "Abgemacht!", :english "It's a deal",
 :learn-map {:difficulty 0.7705882352941176, :interval 2, :percent-overdue 1.0,
             :updated "2018-02-23", :due-date "2018-02-25"}}

```
On the 25 the pair is presented again and this time reted with a 0.6 which counts as correct

```clojure
(def updated-twice (update-pair 0.6 updated-once))
```
Result:
```clojure
{:german "Abgemacht!", :english "It's a deal",
 :learn-map {:difficulty 0.9235294117647058, :interval 1, :percent-overdue 1,
             :updated "2018-02-25", :due-date "2018-02-26"}}
```
On the 25 the pair is presented again and this time reted with a 0.6 which counts as correct
```clojure
(def updated-thrice (update-pair 1 updated-twice))
{:german "Abgemacht!", :english "It's a deal",
  :learn-map {:difficulty 0.8058823529411764, :interval 2, :percent-overdue 2,
              :updated "2018-02-27", :due-date "2018-03-01"}}
```

```clojure
{:german "Abgemacht!", :english "It's a deal",
 :learn-map {:difficulty 0.7470588235294117, :interval 2, :percent-overdue 1,
             :updated "2018-03-01", :due-date "2018-03-03"}}
{:german "Abgemacht!", :english "It's a deal",
 :learn-map {:difficulty 0.688235294117647, :interval 3, :percent-overdue 2,
             :updated "2018-03-03", :due-date "2018-03-06"}}
```
To review a review, you can sort the items by percentage-overdue and review the first 20, as so.

```clojure
(defn most-overdue [pairs]
  (take 20
    (sort-by #(get-in % [:learn-map :percent-overdue]) > pairs)))
```
