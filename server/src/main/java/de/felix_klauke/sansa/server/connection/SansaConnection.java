package de.felix_klauke.sansa.server.connection;

import de.felix_klauke.sansa.commons.exception.InvalidArgumentCountException;
import de.felix_klauke.sansa.commons.ftp.FTPCommand;
import de.felix_klauke.sansa.commons.ftp.FTPRequest;
import de.felix_klauke.sansa.commons.ftp.FTPResponse;
import de.felix_klauke.sansa.commons.ftp.FTPStatus;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public class SansaConnection extends SimpleChannelInboundHandler<FTPRequest> {

    private final Channel channel;
    private String lastAttemptedUserName;

    public SansaConnection(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        FTPResponse response = new FTPResponse(FTPStatus.READY, "Sansa will take over from here");
        sendResponse(response);
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FTPRequest ftpRequest) throws Exception {
        FTPCommand command = ftpRequest.getCommand();

        switch (command) {
            case USER: {
                validateArgsLength(ftpRequest, 1);
                String userName = ftpRequest.getArgs()[0];

                if ("felix".equalsIgnoreCase(userName)) {
                    FTPResponse response = new FTPResponse(FTPStatus.PASSWORD_NEEDED, "My dear " + userName + " will need a password.");
                    sendResponse(response);
                }

                break;
            }
            case PASS: {
                validateArgsLength(ftpRequest, 1);
                String password = ftpRequest.getArgs()[0];

                if ("test".equalsIgnoreCase(password)) {
                    FTPResponse response = new FTPResponse(FTPStatus.LOGGED_IN, "Welcome to my world.");
                    sendResponse(response);
                }

                break;
            }
        }
    }

    private void validateArgsLength(FTPRequest ftpRequest, int size) {
        if (ftpRequest.getArgs().length != size) {
            throw new InvalidArgumentCountException(ftpRequest + " has a size of " + ftpRequest.getArgs().length + " but should have a size of " + size);
        }
    }

    public void sendResponse(FTPResponse response) {
        channel.writeAndFlush(response);
    }
}
