package de.felix_klauke.sansa.server;

import com.google.common.base.Preconditions;
import de.d3adspace.constrictor.netty.NettyUtils;
import de.felix_klauke.sansa.commons.ftp.FTPRequest;
import de.felix_klauke.sansa.commons.ftp.FTPRequestContext;
import de.felix_klauke.sansa.commons.ftp.FTPResponse;
import de.felix_klauke.sansa.commons.ftp.FTPStatus;
import de.felix_klauke.sansa.commons.ftp.FTPTransferType;
import de.felix_klauke.sansa.server.initializer.SansaServerChannelInitializer;
import de.felix_klauke.sansa.server.user.IUser;
import de.felix_klauke.sansa.server.user.IUserManager;
import de.felix_klauke.sansa.server.user.SimpleUser;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;

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
        this.bossGroup = NettyUtils.createBossGroup(1);
        this.workerGroup = NettyUtils.createWorkerGroup(4);

        Class<? extends ServerChannel> serverChannelClazz = NettyUtils.getServerSocketChannel();
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
        Preconditions.checkNotNull(requestContext, "Request Context cannot be null.");
        Preconditions.checkNotNull(ftpRequest, "Request cannot be null.");

        switch (ftpRequest.getCommand()) {
            case AUTH_TLS: {
                handleCommandAuthTLS(requestContext, ftpRequest);
                break;
            }
            case USER: {
                handleCommandUser(requestContext, ftpRequest);
                break;
            }
            case PASS: {
                handleCommandPassword(requestContext, ftpRequest);
                break;
            }
            case TYPE: {
                handleCommandSetTransferType(requestContext, ftpRequest);
                break;
            }
            case SYST: {
                handleCommandSystemInformation(requestContext, ftpRequest);
                break;
            }
            case PWD: {
                handleCommandPrintWorkingDir(requestContext, ftpRequest);
                break;
            }
            case EPSV: {
                handleCommandEnterPassiveMode(requestContext, ftpRequest);
                break;
            }
            default: {

            }
            case UNKNOWN_COMMAND: {
                handleUnknownCommand(requestContext, ftpRequest);
                break;
            }
        }
    }

    /**
     * Handle that the client wants us to enter the passive mode.
     *
     * @param requestContext The request context.
     * @param ftpRequest     The request.
     */
    private void handleCommandEnterPassiveMode(FTPRequestContext requestContext, FTPRequest ftpRequest) {
        requestContext.setPassiveMode();

        FTPResponse response = new FTPResponse(FTPStatus.ENTERED_PASSIVE_MODE, "Sansa is now submissive and passive.");
        requestContext.resume(response);
    }

    /**
     * Handle that the given request wants to set the transfer type.
     *
     * @param requestContext The request context.
     * @param ftpRequest     The request.
     */
    private void handleCommandSetTransferType(FTPRequestContext requestContext, FTPRequest ftpRequest) {
        String typeSymbol = ftpRequest.getCommandArgument(0);
        FTPTransferType transferType = FTPTransferType.forSymbol(typeSymbol);

        requestContext.setTransferType(transferType);

        FTPResponse response = new FTPResponse(FTPStatus.OK, "Yo.");
        requestContext.resume(response);
    }

    /**
     * Handle that the given request wants to print the working dir.
     *
     * @param requestContext The request request.
     * @param ftpRequest     The request.
     */
    private void handleCommandPrintWorkingDir(FTPRequestContext requestContext, FTPRequest ftpRequest) {
        File file = requestContext.getCurrentUserWorkingPath();
        FTPResponse response = new FTPResponse(FTPStatus.PATH_CREATED, file == null ? "/" : file.getAbsolutePath());
        requestContext.resume(response);
    }

    /**
     * Handle that sansa never heard about that command.
     *
     * @param requestContext The request context.
     * @param ftpRequest     The request.
     */
    private void handleUnknownCommand(FTPRequestContext requestContext, FTPRequest ftpRequest) {
        FTPResponse response = new FTPResponse(FTPStatus.UNKNOWN_COMMAND, "Sansa never heard about that.");
        requestContext.resume(response);
    }

    /**
     * Handle that the given request was about system information.
     *
     * @param requestContext The request context.
     * @param ftpRequest The request.
     */
    private void handleCommandSystemInformation(FTPRequestContext requestContext, FTPRequest ftpRequest) {
        FTPResponse response = new FTPResponse(FTPStatus.SYST_STATUS, "Motherfuckr this is sansa stark from winterfell.");
        requestContext.resume(response);
    }

    /**
     * Handle that the given request wants to set a password for authentication.
     *
     * @param requestContext The request context.
     * @param ftpRequest     The request.
     */
    private void handleCommandPassword(FTPRequestContext requestContext, FTPRequest ftpRequest) {
        String password = ftpRequest.getCommandArgument(0);
        requestContext.setLastAttemptedPassword(password);

        FTPResponse response;

        if (requestContext.isUserAuthenticated()) {
            response = new FTPResponse(FTPStatus.LOGGED_IN, "Welcome to sansas world.");
        } else {
            response = new FTPResponse(FTPStatus.LOGIN_INCORRECT, "Such wow, so much deny.");
        }

        requestContext.resume(response);
    }

    /**
     * Handle that the given request in the given context wants to authenticate as a specific user.
     *
     * @param requestContext The request context.
     * @param ftpRequest     The request.
     */
    private void handleCommandUser(FTPRequestContext requestContext, FTPRequest ftpRequest) {
        String userName = ftpRequest.getCommandArgument(0);
        requestContext.setLastAttemptedUserName(userName);

        FTPResponse response = new FTPResponse(FTPStatus.PASSWORD_NEEDED, "Password is need for user '" + userName + "'.");
        requestContext.resume(response);
    }

    /**
     * Handle the given request in the given context when the client sent the AUTH TLS Command.
     *
     * @param requestContext The request context.
     * @param ftpRequest     The request.
     */
    private void handleCommandAuthTLS(FTPRequestContext requestContext, FTPRequest ftpRequest) {
        FTPResponse response = new FTPResponse(FTPStatus.SECURE_CONNECTION_ACCEPTED, "Security is important.");
        requestContext.resume(response);

        requestContext.setupSSL();
    }
}
