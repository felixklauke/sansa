package de.felix_klauke.sansa.server.connection;

import de.felix_klauke.sansa.commons.exception.InvalidArgumentCountException;
import de.felix_klauke.sansa.commons.ftp.FTPCommand;
import de.felix_klauke.sansa.commons.ftp.FTPRequest;
import de.felix_klauke.sansa.commons.ftp.FTPResponse;
import de.felix_klauke.sansa.commons.ftp.FTPStatus;
import de.felix_klauke.sansa.server.handler.CommandHandlerUser;
import de.felix_klauke.sansa.server.handler.ICommandHandler;
import de.felix_klauke.sansa.server.user.IUser;
import de.felix_klauke.sansa.server.user.IUserManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * An inbound handler always representing one command connection (Not the
 * data connection!) to one FTP client. It will handler all incoming data
 * that could be decoded as an {@link FTPRequest} by an
 * {@link de.felix_klauke.sansa.commons.ftp.FTPRequestDecoder}.
 *
 * Remember that the data connection to a client isn't known yet until
 * the client sends us his information in passive mode. The data connection
 * will be built by a {@link de.felix_klauke.sansa.server.handler.CommandHandlerPorts}.
 *
 * TODO: Build a layer of authentication checks.
 *
 * @author Felix 'SasukeKawaii' Klauke
 */
public class SansaConnection extends SimpleChannelInboundHandler<FTPRequest> {

    /**
     * The user manager used to authenticate user with or without authentication. It
     * holds all users registered by
     * {@link de.felix_klauke.sansa.server.SansaServer#registerUser(IUser)}
     */
    private final IUserManager userManager;

    /**
     * The netty channel for the command connection to the client.
     */
    private final Channel channel;

    /**
     * The socket to the client also known as "FTP data connection". All information
     * about files etc. will be sent through this socket to client in passive mode.
     * <p>
     * TODO: Insert a list of handlers that destroy the data connection.
     * <p>
     * It will be created in a {@link de.felix_klauke.sansa.server.handler.CommandHandlerPorts}
     * and destroyed in ...
     */
    private Socket socket;

    /**
     * The last user name a client wanted to authenticate with.
     */
    private String lastAttemptedUserName;

    /**
     * The current login or client is authenticated with. Will be null
     * when the user isn't authenticated at the moment.
     */
    private IUser currentUser;

    /**
     * Currently unused but it should indicate if the clients wants to talk
     * via ASCII or binary mode.
     */
    private boolean usesBinary = true;

    /**
     * Currently unused but it should indicate the mode the data is sent with.
     */
    private boolean passiveMode = false;

    /**
     * The current directory the user is working in. Will be changed by
     * all requests with the command {@link FTPCommand#CHANGE_WORKING_DIR}
     * like in {@link de.felix_klauke.sansa.server.handler.CommandHandlerChangeWorkingDir}
     */
    private File currentLocation;

    private final Map<FTPCommand, ICommandHandler> commandHandlers;

    /**
     * Create a new connection.
     *
     * @param userManager The usermanager.
     * @param channel The command control channel to the client.
     */
    public SansaConnection(IUserManager userManager, Channel channel) {
        this.userManager = userManager;
        this.channel = channel;
        this.currentLocation = new File("");

        commandHandlers = new HashMap<>();
        commandHandlers.put(FTPCommand.USER, new CommandHandlerUser(this, userManager));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        FTPResponse response = new FTPResponse(FTPStatus.READY, "Sansa will take over from here");
        sendResponse(response);
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FTPRequest ftpRequest) throws Exception {
        FTPCommand command = ftpRequest.getCommand();

        ICommandHandler commandHandler = commandHandlers.get(command);
        if (commandHandler != null) {
            commandHandler.handleRequest(ftpRequest);
            return;
        }

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

    /**
     * Send an FTP Response to the client.
     *
     * @param response The response to send.
     */
    public void sendResponse(FTPResponse response) {
        channel.writeAndFlush(response);
    }

    public Channel getChannel() {
        return channel;
    }

    public File getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(File currentLocation) {
        this.currentLocation = currentLocation;
    }

    public IUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(IUser currentUser) {
        this.currentUser = currentUser;
    }

    public IUserManager getUserManager() {
        return userManager;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getLastAttemptedUserName() {
        return lastAttemptedUserName;
    }

    public void setLastAttemptedUserName(String lastAttemptedUserName) {
        this.lastAttemptedUserName = lastAttemptedUserName;
    }

    public void setPassiveMode(boolean passiveMode) {
        this.passiveMode = passiveMode;
    }

    public void setUsesBinary(boolean usesBinary) {
        this.usesBinary = usesBinary;
    }
}
