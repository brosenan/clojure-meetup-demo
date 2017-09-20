(ns my-tweets.core-test
  (:require [midje.sweet :refer :all]
            [my-tweets.core :refer :all]
            [cloudlog-events.testing :refer [scenario as emit query apply-rules]]))

(fact
 (scenario
  (as "bob"
      (emit [:my-tweets/tweet "bob" "hello" 1000]))
  (as "charlie"
      (emit [:my-tweets/tweet "charlie" "Hi, @alice..." 2000]))
  (apply-rules [:my-tweets.core/tweet-by-mentioned-user "alice"])
  => #{["charlie" "Hi, @alice..." 2000]}
  (as "alice"
      (emit [:my-tweets/follow "alice" "bob"])
      (query [:my-tweets/timeline "alice"])
      => #{["bob" "hello" 1000]
           ["charlie" "Mentioned you: Hi, @alice..." 2000]})))
