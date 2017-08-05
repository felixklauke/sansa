package de.felix_klauke.sansa.server.connection;

import de.felix_klauke.sansa.commons.exception.InvalidArgumentCountException;
import de.felix_klauke.sansa.commons.ftp.FTPCommand;
import de.felix_klauke.sansa.commons.ftp.FTPRequest;
import de.felix_klauke.sansa.commons.ftp.FTPResponse;
import de.felix_klauke.sansa.commons.ftp.FTPStatus;
import de.felix_klauke.sansa.server.user.IUser;
import de.felix_klauke.sansa.server.user.IUserManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;

/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public class SansaConnection extends SimpleChannelInboundHandler<FTPRequest> {

    private final IUserManager userManager;
    private final Channel channel;
    private String lastAttemptedUserName;
    private IUser currentUser;
    private boolean usesBinary = true;
    private File currentLocation;

    public SansaConnection(IUserManager userManager, Channel channel) {
        this.userManager = userManager;
        this.channel = channel;
        this.currentLocation = new File("");
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

                if (this.userManager.userExists(userName)) {
                    this.lastAttemptedUserName = userName;

                    if (this.userManager.userNeedsAuthentication(userName)) {
                        FTPResponse response = new FTPResponse(FTPStatus.PASSWORD_NEEDED, "My dear " + userName + " will need a password.");
                        sendResponse(response);
                        return;
                    }

                    this.currentUser = this.userManager.authenticateUser(userName);
                    if (currentUser != null) {
                        FTPResponse response = new FTPResponse(FTPStatus.LOGGED_IN, "Welcome to my world.");
                        sendResponse(response);
                    }
                }

                break;
            }
            case PASS: {
                validateArgsLength(ftpRequest, 1);
                String password = ftpRequest.getArgs()[0];

                this.currentUser = this.userManager.authenticateUser(this.lastAttemptedUserName, password);
                if (this.currentUser != null) {
                    FTPResponse response = new FTPResponse(FTPStatus.LOGGED_IN, "Welcome to my world.");
                    sendResponse(response);
                    return;
                }

                FTPResponse response = new FTPResponse(FTPStatus.LOGIN_INCORRECT, "Sansa cannot accept that.");
                sendResponse(response);

                break;
            }
            case PWD: {
                FTPResponse response = new FTPResponse(FTPStatus.PATH_CREATED, this.currentLocation.getAbsolutePath());
                sendResponse(response);

                break;
            }
            case CHANGE_WORKING_DIR: {
                validateArgsLength(ftpRequest, 1);
                String path = ftpRequest.getArgs()[0];

                this.currentLocation = new File(path);

                FTPResponse response = new FTPResponse(FTPStatus.WORKING_DIR_CHANGED, "Sansa is going there.");
                sendResponse(response);
                break;
            }
            case TYPE: {
                this.usesBinary = true;

                FTPResponse response = new FTPResponse(FTPStatus.OK, "Yoo!");
                sendResponse(response);

                break;
            }
            case BYE: {

            }
            case QUIT: {

                this.channel.close();

                break;
            }
            case SYST: {
                FTPResponse response = new FTPResponse(FTPStatus.SYST_STATUS, "Moarfuckn Sansa");
                sendResponse(response);

                break;
            }
            default: {
                FTPResponse response = new FTPResponse(FTPStatus.UNKNOWN_COMMAND, "Sansa never heard about thatt.");
                sendResponse(response);
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
