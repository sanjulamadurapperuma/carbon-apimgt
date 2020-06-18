package org.wso2.carbon.apimgt.gateway.mediators.oauth;

import org.wso2.carbon.apimgt.gateway.mediators.oauth.client.TokenResponse;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;

public class RedisCache {
    private static JedisPool jedisPool;
    private static Jedis jedis;

    // To be replaced with values from deployment.toml config
    private static String host = "localhost";
    private static Integer port = 6379;
    private static String password = "";

    private void initialize() {
        // TODO
        if (password.length() > 0) {
            jedisPool = new JedisPool(new JedisPoolConfig(), host, port);
        } else {
            jedisPool = new JedisPool(host, port);
        }

        jedis = new Jedis(host, port);

        if (password.length() > 0) {
            jedis.auth(password);
        }
    }

    private void addTokenResponse(TokenResponse tokenResponse) {
        // TODO
        try (Jedis jedis = jedisPool.getResource()) {
            // TODO - Check if the UUID is not unique
            // TODO - If not then append another string to the uuid to make it unique
            jedis.hmset(tokenResponse.getUuid(), tokenResponse.toMap());
        }
    }

    private void getTokenResponseById(String uuid) {
        // TODO
        try (Jedis redis = jedisPool.getResource()) {
            Map<String, String> fields = jedis.hgetAll(uuid);
        }
    }

    private void stopRedisCacheSession() {
        // TODO
        jedisPool.destroy();
        jedis.close();
    }
}
