package com.atguigu.daijia.order.testLock;

import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author lonelykkk
 * @email 2765314967@qq.com
 * @date 2024/9/25 17:51
 * @Version V1.0
 */
@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    public synchronized void testLock() {

        RLock lock = redissonClient.getLock("lock1");
        lock.lock();

        //获取锁成功，执行业务代码
        //1.先从redis中通过key num获取值  key提前手动设置 num 初始值：0
        String value = redisTemplate.opsForValue().get("num");
        //2.如果值为空则非法直接返回即可
        if (StringUtils.isBlank(value)) {
            return;
        }
        //3.对num值进行自增加一
        int num = Integer.parseInt(value);
        redisTemplate.opsForValue().set("num", String.valueOf(++num));

        lock.unlock();

    }

//    public synchronized void testLock() {
//        String uuid = UUID.randomUUID().toString();
//        //final Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent("lock", "lock");
//        Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent("lock", uuid,3, TimeUnit.SECONDS);
//        if (ifAbsent) {
//            //获取锁成功，执行业务代码
//            //1.先从redis中通过key num获取值  key提前手动设置 num 初始值：0
//            String value = redisTemplate.opsForValue().get("num");
//            //2.如果值为空则非法直接返回即可
//            if (StringUtils.isBlank(value)) {
//                return;
//            }
//            //3.对num值进行自增加一
//            int num = Integer.parseInt(value);
//            redisTemplate.opsForValue().set("num", String.valueOf(++num));
//
//            //3 释放锁
//            final DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
//            //lua脚本
//            String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
//                    "then\n" +
//                    "    return redis.call(\"del\",KEYS[1])\n" +
//                    "else\n" +
//                    "    return 0\n" +
//                    "end";
//            redisScript.setScriptText(script);
//            redisScript.setResultType(Long.class);
//            redisTemplate.execute(redisScript, Arrays.asList("lock"), uuid);
//
//        }else {
//            try {
//                Thread.sleep(100);
//                this.testLock();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public synchronized void testLock2() {
        String uuid = UUID.randomUUID().toString();
        //final Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent("lock", "lock");
        Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 3, TimeUnit.SECONDS);
        if (ifAbsent) {
            //获取锁成功，执行业务代码
            //1.先从redis中通过key num获取值  key提前手动设置 num 初始值：0
            String value = redisTemplate.opsForValue().get("num");
            //2.如果值为空则非法直接返回即可
            if (StringUtils.isBlank(value)) {
                return;
            }
            //3.对num值进行自增加一
            int num = Integer.parseInt(value);
            redisTemplate.opsForValue().set("num", String.valueOf(++num));

            //3 释放锁
            final String redisUuid = redisTemplate.opsForValue().get("lock");
            if (uuid.equals(redisUuid)) {
                redisTemplate.delete("lock");
            }

        } else {
            try {
                Thread.sleep(100);
                this.testLock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void testLock1() {
        String value = redisTemplate.opsForValue().get("num");
        if (StringUtils.isBlank(value)) {
            return;
        }
        int num = Integer.parseInt(value);

        redisTemplate.opsForValue().set("num", String.valueOf(num + 1));
    }
}
