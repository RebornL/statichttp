package com.niostatichttpmt.selector;

import java.nio.channels.ServerSocketChannel;

/**
 * @author Reborn
 */
public interface Boss {
    void registerAcceptChannelTask(ServerSocketChannel serverChannel);
}
