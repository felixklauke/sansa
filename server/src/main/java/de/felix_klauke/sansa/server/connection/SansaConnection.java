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
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;

/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public class SansaConnection extends SimpleChannelInboundHandler<FTPRequest> {

    private Socket socket;
    private final IUserManager userManager;
    private final Channel channel;
    private String lastAttemptedUserName;
    private IUser currentUser;
    private boolean usesBinary = true;
    private boolean passiveMode = false;
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
            case EPSV: {
                this.passiveMode = true;

                FTPResponse response = new FTPResponse(FTPStatus.ENTERED_PASSIVE_MODE, "Sansa is now submissive.");
                sendResponse(response);

                break;
            }
            case EPRT: {
                validateArgsLength(ftpRequest, 1);

                String[] splittedArgs = ftpRequest.getArgs()[0].split("\\|");
                int port = Integer.parseInt(splittedArgs[splittedArgs.length - 1]);
                
                socket = new Socket("localhost", port);

                FTPResponse response = new FTPResponse(FTPStatus.FILE_STATUS, "yo");
                sendResponse(response);

                break;
            }
            case LIST: {
                FTPResponse response = new FTPResponse(FTPStatus.BEGINNING_FILE_LIST_ASCII, "Here it comes.");
                sendResponse(response);

                PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                printWriter.print("pom.xml");
                printWriter.flush();

                socket.close();

                FTPResponse response1 = new FTPResponse(FTPStatus.FILE_STATUS, "yo");
                sendResponse(response1);

                break;
            }
            case RETRIEVE: {
                validateArgsLength(ftpRequest, 1);

                String fileName = ftpRequest.getArgs()[0];

                File file = new File(this.currentLocation, fileName);
                byte[] bytes = Files.readAllBytes(file.toPath());

                this.socket.getOutputStream().write(bytes);
                this.socket.getOutputStream().flush();

                FTPResponse response1 = new FTPResponse(FTPStatus.FILE_STATUS, "yo");
                sendResponse(response1);

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
