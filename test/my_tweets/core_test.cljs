(ns my-tweets.core-test
  (:require [cljs.test :refer-macros [is testing]]
            [devcards.core :refer-macros [deftest]]
            [my-tweets.core :as app]))

(deftest a-test
  (is (= 1 2)))

