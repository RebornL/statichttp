package com.niostatichttpmt;

import com.niostatichttpmt.pool.NioSelectorRunnablePool;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * @author Reborn
 */
public class Main {

    public static void main(String[] args) {
        // 只启动一个BossSelector，所以Boss线程池只有一个
        NioSelectorRunnablePool nioSelectorRunnablePool =
                new NioSelectorRunnablePool(Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));

        ServerBootStrap bootStrap =
                new ServerBootStrap(nioSelectorRunnablePool);
        bootStrap.bindAndStart(new InetSocketAddress(22222));
    }
}
