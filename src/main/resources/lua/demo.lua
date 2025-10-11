local resp_status = {
    sadd_error = -1,
    prod_not_found = 0,
    ok = 1,
    user_is_existed = 2,
    no_store = 3
}

local prod_key = 'h:product' -- 我的为人的
local is_exist = 1

local prod = redis.call('HGET', prod_key, KEYS[1])
-- local k = redis.call('HSET', prod_key, KEYS[1], ARGV[1])

-- print(KEYS, ARGV)
-- return { ARGV, KEYS }

local sKey = 'set:' .. KEYS[1]
local sValue = KEYS[2]

if prod == nil then
    return resp_status.prod_not_found
end

-- lua 判断key是否在set中
if redis.call('SISMEMBER', sKey, sValue) == is_exist then
    return resp_status.user_is_existed
end

prod = tonumber(prod)

if prod > 0 then
    prod = prod - tonumber(ARGV[1])
else
    return resp_status.no_store;
end

local r = redis.call('SADD', sKey, sValue)

if r == resp_status.ok then
    redis.call('HSET', prod_key, KEYS[1], prod)
    return resp_status.ok
else
    return resp_status.sadd_error
end
