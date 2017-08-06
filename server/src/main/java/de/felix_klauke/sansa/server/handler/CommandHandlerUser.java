package de.felix_klauke.sansa.server.handler;

import de.felix_klauke.sansa.commons.ftp.FTPCommand;
import de.felix_klauke.sansa.commons.ftp.FTPRequest;
import de.felix_klauke.sansa.commons.ftp.FTPResponse;
import de.felix_klauke.sansa.commons.ftp.FTPStatus;
import de.felix_klauke.sansa.server.connection.SansaConnection;
import de.felix_klauke.sansa.server.user.IUser;
import de.felix_klauke.sansa.server.user.IUserManager;

/**
 * Handler for all Requests holding a command of {@link FTPCommand#USER}
 * <p>
 * On an incoming request this handler will check if the the user the client
 * requested needs authentication or is a free user. If the user needs authentication
 * the handler will return a response saying that a password is needed.
 * <p>
 * Otherwise the handler will try to authenticate the user directly. If he can
 * find the requested free user he will set him as the current user and tell
 * the client that he successfully logged in.
 * <p>
 * Remember that this handler will never try to authenticate a password as
 * in ftp the user and the password are sent in different requests and with
 * different commands. For the password validation for users that need
 * authentication take a look at {@link CommandHandlerPass}.
 *
 * @author Felix Klauke
 */
public class CommandHandlerUser extends BaseCommandHandler {

    /**
     * Underlying user manager to authenticate users fast if they don't need
     * authentication.
     */
    private final IUserManager userManager;

    /**
     * Create a new handler.
     *
     * @param connection  The connection the handler will handle.
     * @param userManager The usermanager needed for all authentication actions.
     */
    public CommandHandlerUser(SansaConnection connection, IUserManager userManager) {
        super(connection, FTPCommand.USER);
        this.userManager = userManager;
    }

    @Override
    public FTPResponse handleRequest(FTPRequest request) {
        String userName = request.getArgument(0);

        if (this.userManager.userExists(userName)) {
            getConnection().setLastAttemptedUserName(userName);

            if (this.userManager.userNeedsAuthentication(userName)) {
                return new FTPResponse(FTPStatus.PASSWORD_NEEDED, "My dear " + userName + " will need a password.");
            }

            IUser user = this.userManager.authenticateUser(userName);
            if (user != null) {
                getConnection().setCurrentUser(user);

                return new FTPResponse(FTPStatus.LOGGED_IN, "Welcome to my world.");
            }
        }

        return null;
    }
}
