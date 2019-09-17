package com.niostatichttpmt.pool;


import com.niostatichttpmt.selector.Boss;
import com.niostatichttpmt.selector.NioServerBoss;
import com.niostatichttpmt.selector.NioServerWorker;
import com.niostatichttpmt.selector.Worker;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 線程调度类
 *
 * @author Reborn
 */
public class NioSelectorRunnablePool {
    // Boss和Worker数组
    private final AtomicInteger bossIndex = new AtomicInteger();
    private Boss[] bosses;
    private final AtomicInteger workerIndex = new AtomicInteger();
    private Worker[] workers;

    /*
    * @param boss Boss的线程池管理
    * @param worker Worker的线程池管理
    * */
    public NioSelectorRunnablePool(Executor boss, Executor worker) {
        initBoss(boss, 1);
        initWorker(worker, Runtime.getRuntime().availableProcessors()*2);
    }

    private void initBoss(Executor boss, int count) {
        this.bosses = new NioServerBoss[count];
        for (int i = 0; i < count; i++) {
            bosses[i] = new NioServerBoss(boss, "boss-thread-"+(i+1), this);
        }
    }

    private void initWorker(Executor worker, int count) {
        this.workers = new NioServerWorker[count];
        for (int i = 0; i < count; i++) {
            workers[i] = new NioServerWorker(worker, "worker-thread-"+(i+1), this);
        }
    }

    public Worker nextWorker() {
        return workers[Math.abs(workerIndex.getAndIncrement()%workers.length)];
    }

    public Boss nextBoss() {
        return bosses[Math.abs(bossIndex.getAndIncrement()%bosses.length)];
    }

}
