-- KEYS[1] = stockKey
-- KEYS[2] = userKey
local stock = tonumber(redis.call("GET", KEYS[1]))
if not stock or stock <= 0 then
    return -1
end

if redis.call("EXISTS", KEYS[2]) == 1 then
    return -2
end

redis.call("DECR", KEYS[1])
redis.call("SET", KEYS[2], "1", "EX", 3600)
return 1