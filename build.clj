(ns build
  (:require [clojure.tools.build.api :as b]
            [org.corfield.build :as bb]))

(def lib 'io.github.runeanielsen/clojure-clj)
(def version (format "0.1.%s" (b/git-count-revs nil)))

(defn ci "Run the CI pipeline of tests (and build the JAR)." [opts]
  (-> opts
      (assoc :lib lib :version version)
      (bb/clean)
      (bb/jar)))

(defn deploy "Deploy the JAR to Clojars." [opts]
  (-> opts
      (assoc :lib lib :version version)
      (bb/deploy)))
