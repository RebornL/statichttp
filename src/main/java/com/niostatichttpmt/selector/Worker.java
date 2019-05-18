package com.niostatichttpmt.selector;

import java.nio.channels.SocketChannel;

/**
 * @author Reborn
 */
public interface Worker {
    void registerNewChannelTask(SocketChannel channel);
}
