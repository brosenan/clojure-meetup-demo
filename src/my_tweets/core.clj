(ns my-tweets.core
  (:require [permacode.core :as perm]
            [perm.QmNYKXgUt64cvXau5aNFqvTrjyU8hEKdnhkvtcUphacJaf :as clg]))

(perm/pure
 (clg/defclause tl-1
   [:my-tweets/timeline user -> author text ts]
   [:my-tweets/follow user author] (clg/by user)
   [:my-tweets/tweet author text ts] (clg/by author)))

