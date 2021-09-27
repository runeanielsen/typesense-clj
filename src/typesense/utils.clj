(ns typesense.utils
  (:require [cheshire.core :as json]
            [clojure.string :as str]
            [clojure.walk :as walk]))

(defn handle-json-response
  [response]
  (-> response
      :body
      (json/parse-string true)))

(defn handle-jsonline-response
  [response]
  (->> response
       :body
       (str/split-lines)
       (map #(json/parse-string % true))
       (into [])))

(defn json-lines
  "Take a vector of maps and returns json-line format."
  [maps]
  (reduce (fn [acc x] (str acc (json/generate-string x) "\n"))
          ""
          maps))

(defn collection-uri
  "Returns the collection uri resource path."
  ([settings]
   (str (:uri settings) "/collections"))
  ([settings collection-name]
   (str (collection-uri settings) "/" collection-name)))

(defn document-uri
  "Returns the document uri resource path."
  [settings collection-name]
  (str (:uri settings) "/collections/" collection-name "/documents"))

(defn build-query
  "Convert param pairs into a valid query string."
  [query-params]
  (if (= (count query-params) 0)
    ""
    (->> query-params
         walk/stringify-keys
         (map #(str (key %) "=" (val %)))
         (str/join \&)
         (str "?"))))
