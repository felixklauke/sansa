package de.felix_klauke.sansa.server.connection;

import de.felix_klauke.sansa.commons.ftp.FTPRequest;
import de.felix_klauke.sansa.commons.ftp.FTPRequestContext;
import de.felix_klauke.sansa.server.SansaServer;
import de.felix_klauke.sansa.server.user.IUserManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import javax.inject.Inject;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class SansaServerConnection extends SimpleChannelInboundHandler<FTPRequest> {

    private final SansaServer sansaServer;
    private final IUserManager userManager;

    @Inject
    public SansaServerConnection(SansaServer sansaServer, IUserManager userManager) {
        this.sansaServer = sansaServer;
        this.userManager = userManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FTPRequest request) {
        FTPRequestContext requestContext = new FTPRequestContext(channelHandlerContext);
        sansaServer.handleRequest(requestContext, request);
    }
}
