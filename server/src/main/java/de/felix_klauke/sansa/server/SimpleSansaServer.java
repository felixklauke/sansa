package de.felix_klauke.sansa.server;

import de.felix_klauke.sansa.commons.utils.NettyUtils;
import de.felix_klauke.sansa.server.initializer.SansaServerChannelInitializer;
import de.felix_klauke.sansa.server.user.IUser;
import de.felix_klauke.sansa.server.user.IUserManager;
import de.felix_klauke.sansa.server.user.SimpleUser;
import de.felix_klauke.sansa.server.user.SimpleUserManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public class SimpleSansaServer implements SansaServer {

    /**
     * Basic logger for general server actions.
     */
    private final Logger logger;

    /**
     * Boss group for netty.
     */
    private EventLoopGroup bossGroup;

    /**
     * User manager for ftp with anuthentication.
     */
    private final IUserManager userManager;

    /**
     * Worker Group for netty
     */
    private EventLoopGroup workerGroup;

    /**
     * Channel all clients will speak with.
     */
    private Channel channel;

    /**
     * Basic constructor to create a server.
     */
    SimpleSansaServer() {
        this.logger = LoggerFactory.getLogger(SimpleSansaServer.class);
        this.userManager = new SimpleUserManager();
    }

    @Override
    public void start() {
        this.bossGroup = NettyUtils.createEventLoopGroup(1);
        this.workerGroup = NettyUtils.createEventLoopGroup(4);

        Class<? extends ServerChannel> serverChannelClazz = NettyUtils.getServerChannelClass();
        ChannelHandler channelInitializer = new SansaServerChannelInitializer(userManager);

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        this.logger.info("Starting a sansa server.");

        try {
            channel = serverBootstrap
                    .group(this.bossGroup, this.workerGroup)
                    .channel(serverChannelClazz)
                    .childHandler(channelInitializer)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .bind(21).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.logger.info("Sansa is now rocking the shit.");
    }

    @Override
    public void stop() {
        this.channel.close();

        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
    }

    @Override
    public boolean isRunning() {
        return this.channel.isActive();
    }

    @Override
    public void registerUser(IUser user) {
        this.userManager.registerUser(user);
    }

    @Override
    public void registerUsers() {
        this.userManager.registerUser(new SimpleUser("felix", "test"));
    }
}
