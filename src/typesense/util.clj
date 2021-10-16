(ns typesense.util
  (:require [cheshire.core :as json]
            [clojure.string :as str]
            [clojure.walk :as walk])
  (:import [java.net URLEncoder]))

(defn build-query
  "Convert param pairs into a valid query string."
  [query-params]
  (if (= (count query-params) 0)
    ""
    (->> query-params
         walk/stringify-keys
         (map #(str (key %) "=" (-> (val %)
                                    str
                                    URLEncoder/encode)))
         (str/join \&)
         (str "?"))))

(defn maps->json-lines
  "Take a vector of maps and returns json-line format."
  [ms]
  (reduce (fn [acc x] (str acc (json/generate-string x) "\n"))
          ""
          ms))

(defn json-lines->maps
  "Transforms json-lines to vector of maps."
  [json-lines]
  (->> json-lines
       str/split-lines
       (map #(json/parse-string % true))
       (into [])))

(defn handle-json-response
  "Handles JSON response from Typesense."
  [response]
  (-> response
      :body
      (json/parse-string true)))

(defn handle-jsonline-response
  "Handles JSON-line response from Typesense."
  [response]
  (-> response
      :body
      json-lines->maps))
