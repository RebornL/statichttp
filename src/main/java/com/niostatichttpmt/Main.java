package com.niostatichttpmt;

import com.niostatichttpmt.pool.NioSelectorRunnablePool;

import java.net.InetSocketAddress;
import java.util.concurrent.*;

/**
 * @author Reborn
 */
public class Main {

    public static void main(String[] args) {
        // 只启动一个BossSelector，所以Boss线程池只有一个
        ThreadPoolExecutor bossExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1));
        int runProcessNum = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor workerExecutor = new ThreadPoolExecutor(runProcessNum, runProcessNum*2, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(5), new ThreadPoolExecutor.DiscardOldestPolicy());
        NioSelectorRunnablePool nioSelectorRunnablePool = new NioSelectorRunnablePool(bossExecutor, workerExecutor);
//                new NioSelectorRunnablePool(Executors.newSingleThreadExecutor(),
//                        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));

        ServerBootStrap bootStrap = new ServerBootStrap(nioSelectorRunnablePool);
        bootStrap.bindAndStart(new InetSocketAddress(22222));
    }
}
