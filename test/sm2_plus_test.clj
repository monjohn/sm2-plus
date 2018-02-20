(ns sm2-plus-test
  (:require [sm2-plus :as sm2])
  (:use clojure.test))

(deftest percent-overdue-test
  (testing "percent-overdue"
    (is (= (sm2/percent-overdue 1 90 100)
           2.0))))

(deftest calculate-test
  (testing "valid input"
    (let [today "2018-02-14"
          learning {:interval 1
                    :updated "2018-02-01"
                    :difficulty 0.3}]
      (is (= 0.24117647058823527
             (:difficulty (sm2/calculate learning 1 today))))
      (is (= {:difficulty 0.24117647058823527
              :interval 3
              :updated 1000000
              :due-date 1000001}
             (sm2/calculate learning 1 today))))))

; (days-since-epoch (System/currentTimeMillis))
; (days-since-epoch (java.util.Date.))
; (days-since-epoch (java.time.Instant/now))
; (days-since-epoch "2018-02-18T10:15:30.00Z")
; (str (java.time.Instant/ofEpochMilli (days-since-epoch "2018-02-18T10:15:30.00Z")))
; (str (.plusDays (java.time.LocalDate/now)  90))
