(ns foo
  (:require [potemkin]))

(potemkin/def-map-type FooMap [m mta]
  (get [_ k v]
    (second (get m k [nil v])))
  (assoc [_ k v]
    (FooMap. (assoc m k [k v])
             mta))
  (dissoc [_ k]
    (FooMap. (dissoc m k) mta))
  (keys [_]
    (map first (vals m)))
  (meta [_]
    mta)
  (with-meta [_ mta]
    (FooMap. m mta))

  clojure.lang.Associative
  (containsKey [_ k]
    (contains? m k))
  (entryAt [_ k]
    (if (contains? m k)
      (clojure.lang.MapEntry. k (get _ k))))

  (empty [_]
    (FooMap. {} nil)))

(defn bar [_]
  (prn (assoc (FooMap. {} nil) :ok true)))
