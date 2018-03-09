package de.felix_klauke.sansa.commons.ftp;

import de.felix_klauke.sansa.commons.connection.FTPServerConnection;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class FTPRequestContext {

    private final FTPServerConnection serverConnection;
    private final ChannelHandlerContext channelHandlerContext;

    public FTPRequestContext(FTPServerConnection serverConnection, ChannelHandlerContext channelHandlerContext) {
        this.serverConnection = serverConnection;
        this.channelHandlerContext = channelHandlerContext;
    }

    public void resume(FTPResponse response) {
        channelHandlerContext.channel().writeAndFlush(response);
    }

    public void setLastAttemptedUserName(String userName) {
        serverConnection.setUserName(userName);
    }

    public void setupSSL() {
        serverConnection.setupSSL();
    }

    public boolean isUserAuthenticated() {
        return serverConnection.isAuthenticated();
    }

    public void setLastAttemptedPassword(String lastAttemptedPassword) {
        serverConnection.setPassword(lastAttemptedPassword);
    }

    public void setTransferType(FTPTransferType transferType) {
        this.serverConnection.setTransferType(transferType);
    }
  
    public File getCurrentUserWorkingPath() {
        return serverConnection.getUserWorkingPath();
    }

    public void setPassiveMode() {
        serverConnection.setActiveMode(true);
    }
}
