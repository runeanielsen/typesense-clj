(ns typesense.util-test
  (:require [typesense.util :as sut]
            [clojure.test :refer [deftest is]]))

(deftest build-query-test
  (let [expected "?q=Stark+is+a+company&query_by=test_name&filter_by=num_employees%3A%3E100&sort_by=num_employees%3Adesc"
        query (sut/build-query {:q "Stark is a company"
                                :query_by "test_name"
                                :filter_by "num_employees:>100"
                                :sort_by "num_employees:desc"})]
    (is (= expected query))))

(deftest maps->json-lines-test
  (let [expected "{\"username\":\"progamer42\",\"rank\":42}\n{\"username\":\"progamer69\",\"rank\":69}\n"
        ms [{:username "progamer42" :rank 42}
            {:username "progamer69" :rank 69}]
        json-lines (sut/maps->json-lines ms)]
    (is (= expected json-lines))))

(deftest json-lines->maps-test
  (let [expected [{:username "progamer42" :rank 42}
                  {:username "progamer69" :rank 69}]
        json-lines "{\"username\":\"progamer42\",\"rank\":42}\n{\"username\":\"progamer69\",\"rank\":69}\n"
        maps (sut/json-lines->maps json-lines)]
    (is (= expected maps))))
