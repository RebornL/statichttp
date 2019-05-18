package com.niostatichttpmt.selector;

import com.niostatichttpmt.pool.NioSelectorRunnablePool;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Reborn
 */
public abstract class AbstractNioSelector implements Runnable {

    // 线程池
    private final Executor executor;
    // selector选择器
    protected Selector selector;
    protected final AtomicBoolean wakeUp = new AtomicBoolean();
    // 线程任务队列
    private final Queue<Runnable> taskQueue = new ConcurrentLinkedQueue<>();
    private String threadName;
    // 线程的调度器
    protected NioSelectorRunnablePool selectorRunnablePool;

    public AbstractNioSelector(Executor executor, String threadName, NioSelectorRunnablePool selectorRunnablePool) {
        this.executor = executor;
        this.threadName = threadName;
        this.selectorRunnablePool = selectorRunnablePool;
        openSelector();
    }

    private void openSelector() {
        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 初始化之后，线程池分配一个线程给Boss进行
        executor.execute(this);
    }

    @Override
    public void run() {
        Thread.currentThread().setName(threadName);
        while (true) {
            // 消息选择器状态设置为未唤醒状态
            wakeUp.set(false);
            // 消息选择器选择消息的方式
            try {
                select(selector);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 读出任务
            processTaskQueue();
            // 处理任务
            try {
                process(selector);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract void process(Selector selector) throws IOException;
    protected abstract int select(Selector selector) throws IOException;

    // 欢迎线程和工作线程各自添加不同的线程，再将Selector唤醒
    protected final void registerTask(Runnable task) {
        taskQueue.add(task);
        Selector selector = this.selector;
        if (selector != null) {
            if (wakeUp.compareAndSet(false, true)) {
                selector.wakeup();
            }
        } else {
            taskQueue.remove(task);
        }
    }

    // 获取线程调度器
    public NioSelectorRunnablePool getSelectorRunnablePool() {
        return selectorRunnablePool;
    }

    private void processTaskQueue() {
        for (;;) {
            final Runnable task = taskQueue.poll();
            if (task == null) {
                break;
            }
            // 直接调用任务中要做的事即可，不需要start调用线程去处理
            task.run();
        }
    }
}
