(ns build
  (:require [clojure.tools.build.api :as b]
            [org.corfield.build :as bb]))

(def lib 'io.github.runeanielsen/typesense-clj)
(def version (format "0.1.%s" (b/git-count-revs nil)))

(defn build "Build the JAR." [opts]
  (-> opts
      (assoc :lib lib :version version)
      (bb/clean)
      (bb/jar)))

(defn tests "Run the tests." [opts]
  (-> opts
      (bb/clean)
      (bb/run-tests)))

(defn deploy "Deploy the JAR to Clojars." [opts]
  (-> opts
      (assoc :lib lib :version version)
      (bb/clean)
      (bb/jar)
      (bb/deploy)))
