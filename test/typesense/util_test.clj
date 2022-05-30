(ns ^:unit typesense.util-test
  (:require [typesense.util :as sut]
            [clojure.test :refer [deftest is]]))

(deftest map->url-parameter-string-test
  (let [expected "?q=Stark+is+a+company&query_by=test_name&filter_by=num_employees%3A%3E100&sort_by=num_employees%3Adesc"
        query (sut/map->url-parameter-string {:q "Stark is a company"
                                              :query_by "test_name"
                                              :filter_by "num_employees:>100"
                                              :sort_by "num_employees:desc"})]
    (is (= expected query))))

(deftest map->url-parameter-string-test-empty
  (let [expected ""
        query (sut/map->url-parameter-string {})]
    (is (= expected query))))

(deftest maps->json-lines-test
  (let [expected "{\"username\":\"progamer42\",\"rank\":42}\n{\"username\":\"progamer69\",\"rank\":69}\n"
        ms [{:username "progamer42" :rank 42}
            {:username "progamer69" :rank 69}]
        json-lines (sut/maps->json-lines ms)]
    (is (= expected json-lines))))

(deftest maps->json-lines-test-empty
  (let [expected ""
        ms []
        json-lines (sut/maps->json-lines ms)]
    (is (= expected json-lines))))

(deftest json-lines->maps-test
  (let [expected [{:username "progamer42" :rank 42}
                  {:username "progamer69" :rank 69}]
        json-lines "{\"username\":\"progamer42\",\"rank\":42}\n{\"username\":\"progamer69\",\"rank\":69}\n"
        maps (sut/json-lines->maps json-lines)]
    (is (= expected maps))))

(deftest json-lines->maps-test-empty
  (let [expected []
        json-lines ""
        maps (sut/json-lines->maps json-lines)]
    (is (= expected maps))))

(deftest http-response-json->map-test
  (let [expected {:username "progamer42" :rank 42}
        http-response {:body "{\"username\":\"progamer42\",\"rank\":42}"}
        result (sut/http-response-json->map http-response)]
    (is (= expected result))))

(deftest http-response-json->map-test-empty
  (let [expected nil
        http-response {:body ""}
        result (sut/http-response-json->map http-response)]
    (is (= expected result))))

(deftest http-response-jsonline->maps-test
  (let [expected [{:username "progamer42" :rank 42}
                  {:username "progamer69" :rank 69}]
        http-response {:body "{\"username\":\"progamer42\",\"rank\":42}\n{\"username\":\"progamer69\",\"rank\":69}\n"}
        result (sut/http-response-jsonline->maps http-response)]
    (is (= expected result))))

(deftest http-response-jsonline->maps-empty
  (let [expected []
        http-response {:body ""}
        result (sut/http-response-jsonline->maps http-response)]
    (is (= expected result))))
