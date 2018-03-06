package de.felix_klauke.sansa.server;

import de.felix_klauke.sansa.commons.ftp.FTPRequest;
import de.felix_klauke.sansa.commons.ftp.FTPRequestContext;
import de.felix_klauke.sansa.commons.ftp.FTPResponse;
import de.felix_klauke.sansa.commons.ftp.FTPStatus;
import de.felix_klauke.sansa.commons.utils.NettyUtils;
import de.felix_klauke.sansa.server.initializer.SansaServerChannelInitializer;
import de.felix_klauke.sansa.server.user.IUser;
import de.felix_klauke.sansa.server.user.IUserManager;
import de.felix_klauke.sansa.server.user.SimpleUser;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public class SimpleSansaServer implements SansaServer {

    /**
     * Basic logger for general server actions.
     */
    private final Logger logger = LoggerFactory.getLogger(SimpleSansaServer.class);

    /**
     * Boss group for netty.
     */
    private EventLoopGroup bossGroup;

    /**
     * User manager for ftp with authentication.
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
    @Inject
    SimpleSansaServer(IUserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public void start() {
        this.bossGroup = NettyUtils.createEventLoopGroup(1);
        this.workerGroup = NettyUtils.createEventLoopGroup(4);

        Class<? extends ServerChannel> serverChannelClazz = NettyUtils.getServerChannelClass();
        ChannelHandler channelInitializer = new SansaServerChannelInitializer(this, userManager);

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        this.logger.info("Starting a sansa server.");

        try {
            channel = serverBootstrap
                    .group(this.bossGroup, this.workerGroup)
                    .channel(serverChannelClazz)
                    .childHandler(channelInitializer)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .bind(1100).sync().channel();
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
        return channel != null && channel.isActive();
    }

    @Override
    public void registerUser(IUser user) {
        userManager.registerUser(user);
    }

    @Override
    public void registerUsers() {
        userManager.registerUser(new SimpleUser("felix", "test"));
    }

    @Override
    public void handleRequest(FTPRequestContext requestContext, FTPRequest ftpRequest) {
        switch (ftpRequest.getCommand()) {
            case AUTH_TLS: {
                handleCommandAuthTLS(requestContext, ftpRequest);
            }
        }
    }

    /**
     * Handle the given request in the given context when the client sent the AUTH TLS Command.
     *
     * @param requestContext The request context.
     * @param ftpRequest     The request.
     */
    private void handleCommandAuthTLS(FTPRequestContext requestContext, FTPRequest ftpRequest) {
        FTPResponse response = new FTPResponse(FTPStatus.SECURE_CONNECTION_ACCEPTED, "Security is important.");

        requestContext.setupSSL(ftpRequest.getRawCommand());

        requestContext.resume(response);
    }
}
