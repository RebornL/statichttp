package com.niostatichttpmt;

import com.niostatichttpmt.pool.NioSelectorRunnablePool;
import com.niostatichttpmt.selector.Boss;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.time.LocalDateTime;

/**
 * @author Reborn
 */
public class ServerBootStrap {
    private NioSelectorRunnablePool selectorRunnablePool;

    public ServerBootStrap(NioSelectorRunnablePool selectorRunnablePool) {
        this.selectorRunnablePool = selectorRunnablePool;
    }

    // 通过绑定不同端口，可以启动多个BossSelector
    public void bindAndStart(final SocketAddress localAddress) {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(localAddress);
            Boss nextBoss = selectorRunnablePool.nextBoss();
            nextBoss.registerAcceptChannelTask(serverSocketChannel);
            System.out.println(LocalDateTime.now() + " " + Thread.currentThread().getName()+": 静态资源服务器正在运行，"+localAddress);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
