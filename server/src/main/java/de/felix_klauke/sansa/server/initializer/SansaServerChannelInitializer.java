package de.felix_klauke.sansa.server.initializer;

import de.felix_klauke.sansa.commons.ftp.FTPRequestDecoder;
import de.felix_klauke.sansa.commons.ftp.FTPResponseEncoder;
import de.felix_klauke.sansa.server.SansaServer;
import de.felix_klauke.sansa.server.connection.SansaServerConnection;
import de.felix_klauke.sansa.server.user.IUserManager;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import javax.inject.Inject;

/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public class SansaServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final SansaServer sansaServer;
    private final IUserManager userManager;

    @Inject
    public SansaServerChannelInitializer(SansaServer sansaServer, IUserManager userManager) {
        this.sansaServer = sansaServer;
        this.userManager = userManager;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();

        pipeline.addLast(new LoggingHandler(LogLevel.TRACE));
        pipeline.addLast(new FTPRequestDecoder());
        pipeline.addLast(new FTPResponseEncoder());
        pipeline.addLast(new SansaServerConnection(sansaServer, userManager));
    }
}
