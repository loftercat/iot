package com.fujica.util;

import cn.hutool.core.lang.Snowflake;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;


@Slf4j
public class SnowFlakeIDGenerator {

    private static Snowflake snowFlake;


    private final static long WORK_ID = new Random().nextInt(32);


    private final static long DATACENTER_ID = 19;
    static{
        snowFlake = new Snowflake(WORK_ID, DATACENTER_ID);
    }

    public SnowFlakeIDGenerator() {
        snowFlake = new Snowflake(WORK_ID, DATACENTER_ID);
    }
    /**
     * 雪花算法ID生成，全局唯一
     * @return
     *      long id
     */
    public static long nextId(){
        return snowFlake.nextId();
    }

    public static long generateSnowFlakeId(){
        return snowFlake.nextId();
    }

    /**
     * 雪花算法ID生成，全局唯一
     * @return
     *      String id
     */
    public static String nextString() {
        return ((Long)snowFlake.nextId()).toString();
    }

}
