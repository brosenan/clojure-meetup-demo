(ns my-tweets.core
  (:require [permacode.core :as perm]
            [perm.QmNYKXgUt64cvXau5aNFqvTrjyU8hEKdnhkvtcUphacJaf :as clg]))

(perm/pure
 (clg/defrule followee-tweets [user author text ts]
   [:my-tweets/follow user author] (clg/by user)
   [:my-tweets/tweet author text ts] (clg/by author))
 
 (clg/defclause tl-1
   [:my-tweets/timeline user -> author text ts]
   [followee-tweets user author text ts])

 (clg/defrule tweet-by-mentioned-user [user author text ts]
   [:my-tweets/tweet author text ts] (clg/by author)
   (for [user (re-seq #"@[a-zA-Z0-9]+" text)])
   (let [user (subs user 1)]))

 (clg/defclause tl-2
   [:my-tweets/timeline user -> author text ts]
   [tweet-by-mentioned-user user author text ts]
   (let [text (str "Mentioned you: " text)])))

