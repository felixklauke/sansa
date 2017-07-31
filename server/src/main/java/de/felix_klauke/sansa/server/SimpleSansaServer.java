package de.felix_klauke.sansa.server;

import de.felix_klauke.sansa.commons.utils.NettyUtils;
import de.felix_klauke.sansa.server.initializer.SansaServerChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public class SimpleSansaServer implements SansaServer {

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    @Override
    public void start() {
        this.bossGroup = NettyUtils.createEventLoopGroup(1);
        this.workerGroup = NettyUtils.createEventLoopGroup(4);

        Class<? extends ServerChannel> serverChannelClazz = NettyUtils.getServerChannelClass();
        ChannelHandler channelInitializer = new SansaServerChannelInitializer();

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        try {
            Channel channel = serverBootstrap
                    .group(this.bossGroup, this.workerGroup)
                    .channel(serverChannelClazz)
                    .childHandler(channelInitializer)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .bind(21).sync().channel();

            System.out.println("Started.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
