(ns sm2-plus)

(def default-difficulty 0.3)
(def default-interval-in-days 1)

;; Date helpers

(def milliseconds-per-day 86400000);

(defn days-to-millis [days]
  (days * milliseconds-per-day))

(defn millis-to-days [millis]
  (Math/round (float (/ millis milliseconds-per-day))));

(defn date-from-days [days-in-seconds]
  (java.time.LocalDate/ofEpochDay days-in-seconds))

(defn string-to-local-date [date]
  (let [date (if (= 10 (count date))
              date
              (apply str (take 10 date)))]
      (java.time.LocalDate/parse date)))

(defn days-since-epoch [date]
  (condp = (type date)
    java.time.LocalDate (.toEpochDay date)
    java.util.Date (millis-to-days (.getTime date))
    java.lang.Long (millis-to-days date)
    java.lang.String (.toEpochDay (string-to-local-date date))
    java.time.Instant (millis-to-days (.toEpochMilli date))
    :unknown
      (throw (Exception. (str "Unable to recognize format of date: " date)))))

;;;; Sm2-plus algorithm

(defn percent-overdue [interval updated today]
  (let [interval (if (nil? interval)
                     default-interval-in-days
                     interval)
        percentage (/ (- today updated) interval)]
    (if (> percentage  2.0)
      2.0
      percentage)))

(defn within-bounds [number]
  (cond
    (< number 0) 0
    (> number 1) 1
    :else number))

(defn new-interval [rating difficulty-weight percent-overdue]
  (if (= difficulty-weight 0)
    1
    (if (= rating 1.0)
      (Math/round  (/ (/ 1.0 difficulty-weight) difficulty-weight))
      (+ 1 (Math/round (* (- difficulty-weight 1.0) percent-overdue))))))

(defn new-difficulty [difficulty rating percentage-overdue]
  (let [prev-difficulty (if (nil? difficulty)
                            default-difficulty
                            difficulty)]
    (within-bounds (+ prev-difficulty
                      (* (- 8.0 (* 9.0 rating))
                         (/ percentage-overdue 17.0))))))

(defn calculate
  ([rating]
   (let [today (java.time.LocalDate/now)]
     (calculate rating {:updated (.minusDays today 1 )} today)))
  ([rating learn-map]
   (calculate rating learn-map (java.time.LocalDate/now)))
  ([rating learn-map now]
   (let [today-in-days (days-since-epoch now)
         updated       (days-since-epoch (if (:updated learn-map)
                                           (:updated learn-map)
                                           (- today-in-days 86400)))
         p-overdue     (percent-overdue (:interval learn-map) updated today-in-days);
         difficulty    (new-difficulty (:difficulty learn-map) rating p-overdue)
         difficulty-weight (- 3.0 (* 1.7  difficulty));
         interval      (new-interval rating difficulty-weight p-overdue)]
      {:interval interval
       :updated (date-from-days (days-since-epoch now))
       :difficulty difficulty
       :due-date (date-from-days (+ interval today-in-days))})))
