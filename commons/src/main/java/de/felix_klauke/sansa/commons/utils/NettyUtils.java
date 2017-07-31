package de.felix_klauke.sansa.commons.utils;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.internal.PlatformDependent;

import java.util.concurrent.ThreadFactory;

/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public class NettyUtils {

    private static final boolean EPOLL = !PlatformDependent.isWindows() && Epoll.isAvailable();

    public static EventLoopGroup createEventLoopGroup(int threadAmount) {
        return EPOLL ? new EpollEventLoopGroup(threadAmount) : new NioEventLoopGroup(threadAmount);
    }

    public static EventLoopGroup createEventLoopGroup(ThreadFactory threadFactory, int threadAmount) {
        return EPOLL ? new EpollEventLoopGroup(threadAmount, threadFactory) : new NioEventLoopGroup(threadAmount, threadFactory);
    }

    public static Class<? extends ServerChannel> getServerChannelClass() {
        return EPOLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

    public static Class<? extends Channel> getChannel() {
        return EPOLL ? EpollSocketChannel.class : NioSocketChannel.class;
    }

    public static boolean isEpoll() {
        return EPOLL;
    }

    public static ChannelHandler createLengthFieldBasedFrameDecoder(int maxFrameLength, int offset,
                                                                    int lengthFieldLength) {
        return new LengthFieldBasedFrameDecoder(maxFrameLength, offset, lengthFieldLength);
    }

    public static ChannelHandler createLengthFieldPrepender(int lengthFieldLength) {
        return new LengthFieldPrepender(lengthFieldLength);
    }

    public static void closeWhenFlushed(Channel channel) {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
}
