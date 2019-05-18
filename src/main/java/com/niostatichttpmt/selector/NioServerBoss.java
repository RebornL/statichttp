package com.niostatichttpmt.selector;

import com.niostatichttpmt.pool.NioSelectorRunnablePool;

import java.io.IOException;
import java.nio.channels.*;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * @author Reborn
 * Boss线程，只负责接收客户端，分配和注册到各个Worker的任务队列中
 */
public class NioServerBoss extends AbstractNioSelector implements Boss {

    public NioServerBoss(Executor executor, String threadName, NioSelectorRunnablePool selectorRunnablePool) {
        super(executor, threadName, selectorRunnablePool);
    }

    // 注册接受任务
    @Override
    public void registerAcceptChannelTask(ServerSocketChannel serverChannel) {
        // 需要将其用到内部函数中，新版Java会自动加上这个
        final Selector selector = this.selector;

        registerTask(new Runnable() {
            @Override
            public void run() {
                try {
                    serverChannel.register(selector, SelectionKey.OP_ACCEPT);
                } catch (ClosedChannelException e) {
                    System.out.println("BossServerChannel注册失败");
                    e.printStackTrace();
                }
            }
        });
    }

    // 这个方法会阻塞，直至有链接进来
    @Override
    protected int select(Selector selector) throws IOException {
        return selector.select();
    }

    @Override
    protected void process(Selector selector) throws IOException {
        Set<SelectionKey> selectionKeys = selector.selectedKeys();
        if (selectionKeys.isEmpty()) {
            return;
        }

        for (Iterator<SelectionKey> iterator = selectionKeys.iterator(); iterator.hasNext();) {
            SelectionKey key = iterator.next();
            iterator.remove();
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel channel = server.accept();
            channel.configureBlocking(false);
//            HttpHandler handler = new HttpHandler(channel, key);
//            key.attach(handler);
            Worker nextWorker = getSelectorRunnablePool().nextWorker();
            // 将其注册到任务队列中
            nextWorker.registerNewChannelTask(channel);
            System.out.println(
                    "<===================================================================>");
            System.out.println(LocalDateTime.now()+": 新客户端连进来："+channel.getRemoteAddress());
        }
    }
}
