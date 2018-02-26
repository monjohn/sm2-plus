(ns sm2-plus-test
  (:require [sm2-plus :as sm2])
  (:use clojure.test))

(deftest percent-overdue-test
  (testing "percent-overdue"
    (is (= (sm2/percent-overdue 1 90 100)
           2.0))))

(deftest calculate-test
  (let [learn-map  {:interval 1
                    :updated "2018-02-13"
                    :difficulty 0.3}
        output {:difficulty 0.24117647058823527
                :interval 3
                :updated  "2018-02-14"
                :due-date  "2018-02-17"}]

    (testing "calculates the right difficulty"
      (is (= (:difficulty (sm2/calculate 1 learn-map "2018-02-14"))
             0.24117647058823527)))


    (testing "returns correct data with only rating passed"
      (is (= (:difficulty (sm2/calculate  1))
             0.24117647058823527)))

    (testing "returns correct data with only rating and learn-map as args "
      (is (= (:difficulty (sm2/calculate  1))
             0.24117647058823527)))

    (testing "date given as string"
      (is (= (sm2/calculate 1 learn-map "2018-02-14")
             output)))

    (testing "date given as DateTime string"
      (is (= (sm2/calculate 1 learn-map "2018-02-14T12:30:00Z")
             output)))

    (testing "date given as number of milliseconds"
      (is (= (sm2/calculate 1 learn-map 1518584400000)
             output)))

    (testing "date given as java.util.Date"
      (let [today (.parse
                     (java.text.SimpleDateFormat. "yyyy-MM-dd")
                    "2018-02-14")]
        (is (= (sm2/calculate 1 learn-map today)
               output))))

    (testing "date given as java.time.LocalDate"
      (is (= (sm2/calculate 1 learn-map (java.time.LocalDate/parse "2018-02-14"))
             output)))))
