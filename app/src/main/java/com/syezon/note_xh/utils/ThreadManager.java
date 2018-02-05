package com.syezon.note_xh.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by admin on 2017/7/24.
 */

public class ThreadManager {
    //通过ThreadPoolExecutor的代理类来对线程池的管理
    private static CustomThreadPoll sCustomThreadPoll;
    private static ExecutorService sCacheThreadPool;
    private static ExecutorService sSingleThreadPool;

    public static CustomThreadPoll getCustomFixThreadPoll(){
        synchronized (CustomThreadPoll.class) {
            if(sCustomThreadPoll ==null){
                sCustomThreadPoll =new CustomThreadPoll(9,15,1000);
            }
        }
        return sCustomThreadPoll;
    }

    public static ExecutorService getCacheThreadPoll(){
        synchronized (ThreadManager.class) {
            if(sCacheThreadPool ==null||sCacheThreadPool.isShutdown()){
                sCacheThreadPool =Executors.newCachedThreadPool();
            }
        }
        return sCacheThreadPool;
    }


    public static ExecutorService getSingleThreadPoll(){
        synchronized (ThreadManager.class) {
            if(sSingleThreadPool ==null||sSingleThreadPool.isShutdown()){
                sSingleThreadPool =Executors.newSingleThreadExecutor();
            }
        }
        return sSingleThreadPool;
    }



    //通过ThreadPoolExecutor的代理类来对线程池的管理
    public static class CustomThreadPoll {
        private ThreadPoolExecutor poolExecutor;//线程池执行者 ，java内部通过该api实现对线程池管理
        private int corePoolSize;
        private int maximumPoolSize;
        private long keepAliveTime;

        public CustomThreadPoll(int corePoolSize, int maximumPoolSize, long keepAliveTime){
            this.corePoolSize=corePoolSize;
            this.maximumPoolSize=maximumPoolSize;
            this.keepAliveTime=keepAliveTime;
        }
        //对外提供一个执行任务的方法
        public void execute(Runnable r){
            if(poolExecutor==null||poolExecutor.isShutdown()){
                poolExecutor=new ThreadPoolExecutor(
                        //核心线程数量
                        corePoolSize,
                        //最大线程数量
                        maximumPoolSize,
                        //当线程空闲时，保持活跃的时间
                        keepAliveTime,
                        //时间单元 ，毫秒级
                        TimeUnit.MILLISECONDS,
                        //线程任务队列
                        new LinkedBlockingQueue<Runnable>(),
                        //创建线程的工厂
                        Executors.defaultThreadFactory());
            }
            poolExecutor.execute(r);
        }
    }
}
