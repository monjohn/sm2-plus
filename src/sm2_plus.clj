(ns sm2-plus)

(def default-difficulty 0.3)

;; Date Helpers




(defn days-since-epoch []
  (let [milliseconds-per-day 86400000];
    (Math/round (float (/ (System/currentTimeMillis) milliseconds-per-day)))));

(defn percent-overdue [interval update today]
  (let [calculated (/ (- today update) interval)];
    (if (> calculated  2.0)
      2.0
      calculated)))

(defn within-bounds [number]
  (cond
    (< number 0) 0
    (> number 1) 1
    :else number))

(defn new-interval [rating difficulty-weight percent-overdue]
  (if (= difficulty-weight  0)
    1
    (if (= rating 1.0)
      (Math/round  (/ (/ 1.0 difficulty-weight) difficulty-weight))
      (+ 1 (Math/round (* (- difficulty-weight 1.0) percent-overdue))))))

(defn calculate[learning rating today]
    (let [p-overdue  (percent-overdue (:interval learning) (:updated learning) today);
          difficulty  (within-bounds (+ (:difficulty learning)
                                        (* (- 8.0 (* 9.0 rating)) (/ p-overdue 17.0))));
          difficulty-weight   (- 3.0  (* 1.7  difficulty));
          new-interval  (new-interval rating difficulty-weight p-overdue)];
      {:interval new-interval
       :updated today
       :difficulty difficulty
       :due-date (+ 1 today)}))
