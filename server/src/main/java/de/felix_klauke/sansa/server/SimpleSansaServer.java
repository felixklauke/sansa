package de.felix_klauke.sansa.server;

import de.felix_klauke.sansa.commons.utils.NettyUtils;
import de.felix_klauke.sansa.server.initializer.SansaServerChannelInitializer;
import de.felix_klauke.sansa.server.user.IUserManager;
import de.felix_klauke.sansa.server.user.SimpleUser;
import de.felix_klauke.sansa.server.user.SimpleUserManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;

/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public class SimpleSansaServer implements SansaServer {

    /**
     * Boss group for netty.
     */
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel channel;
    private IUserManager userManager;

    SimpleSansaServer() {
        this.userManager = new SimpleUserManager();
    }

    @Override
    public void start() {
        this.bossGroup = NettyUtils.createEventLoopGroup(1);
        this.workerGroup = NettyUtils.createEventLoopGroup(4);

        Class<? extends ServerChannel> serverChannelClazz = NettyUtils.getServerChannelClass();
        ChannelHandler channelInitializer = new SansaServerChannelInitializer(userManager);

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        try {
            channel = serverBootstrap
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

    @Override
    public void stop() {
        this.channel.close();

        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
    }

    @Override
    public void registerUsers() {
        this.userManager.registerUser(new SimpleUser("felix", "test"));
    }
}
