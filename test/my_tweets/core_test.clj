(ns my-tweets.core-test
  (:require [midje.sweet :refer :all]
            [my-tweets.core :refer :all]
            [cloudlog-events.testing :refer [scenario as emit query apply-rules]]))

(fact
 (scenario
  (as "bob"
      (emit [:my-tweets/tweet "bob" "hello" 1000]))
  (as "alice"
      (emit [:my-tweets/follow "alice" "bob"])
      (query [:my-tweets/timeline "alice"])
      => #{["bob" "hello" 1000]})))
