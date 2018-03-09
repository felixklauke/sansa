package de.felix_klauke.sansa.server.connection;

import de.felix_klauke.sansa.commons.connection.FTPServerConnection;
import de.felix_klauke.sansa.commons.ftp.*;
import de.felix_klauke.sansa.server.SansaServer;
import de.felix_klauke.sansa.server.user.IUser;
import de.felix_klauke.sansa.server.user.IUserManager;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.inject.Inject;
import javax.net.ssl.SSLException;
import java.io.File;
import java.security.cert.CertificateException;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class SansaServerConnection extends SimpleChannelInboundHandler<FTPRequest> implements FTPServerConnection {

    private final SansaServer sansaServer;
    private final IUserManager userManager;
    private String userName;
    private String password;
    private IUser user;
    private ChannelHandlerContext lastChannelHandlerContext;
    private FTPTransferType transferType;
    private File workingPath;
    private boolean activeMode;

    @Inject
    public SansaServerConnection(SansaServer sansaServer, IUserManager userManager) {
        this.sansaServer = sansaServer;
        this.userManager = userManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        lastChannelHandlerContext = ctx;

        FTPResponse response = new FTPResponse(FTPStatus.READY, "This is the sansa take over.");
        ctx.writeAndFlush(response);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FTPRequest request) {
        lastChannelHandlerContext = channelHandlerContext;

        FTPRequestContext requestContext = new FTPRequestContext(this, channelHandlerContext);
        sansaServer.handleRequest(requestContext, request);
    }

    @Override
    public void setupSSL() {
        try {
            SelfSignedCertificate selfSignedCertificate = new SelfSignedCertificate("localhost");

            SslContext sslContext = SslContextBuilder.forServer(selfSignedCertificate.certificate(), selfSignedCertificate.privateKey()).build();
            ByteBufAllocator byteBufAllocator = lastChannelHandlerContext.alloc();
            SslHandler sslHandler = sslContext.newHandler(byteBufAllocator);

            ChannelPipeline pipeline = lastChannelHandlerContext.channel().pipeline();
            pipeline.addFirst(sslHandler);

        } catch (CertificateException | SSLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean isInActiveMode() {
        return activeMode;
    }

    @Override
    public void setActiveMode(boolean activeMode) {
        this.activeMode = activeMode;
    }

    @Override
    public boolean isAuthenticated() {
        user = userManager.authenticateUser(userName, password);
        return user != null;
    }

    @Override
    public void setTransferType(FTPTransferType transferType) {
        this.transferType = transferType;
    }
  
    @Override
    public File getUserWorkingPath() {
        return workingPath;
    }
}
