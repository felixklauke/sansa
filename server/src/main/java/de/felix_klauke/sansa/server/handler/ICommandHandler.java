package de.felix_klauke.sansa.server.handler;

import de.felix_klauke.sansa.commons.ftp.FTPRequest;
import de.felix_klauke.sansa.commons.ftp.FTPResponse;
import de.felix_klauke.sansa.commons.ftp.ICommand;
import de.felix_klauke.sansa.server.connection.SansaConnection;

/**
 * A handler for a common ftp request identified by a specific
 * ftp command. You can take a look at {@link de.felix_klauke.sansa.commons.ftp.FTPCommand#}
 * for a list of all available
 */
public interface ICommandHandler {

    FTPResponse handleRequest(FTPRequest request);

    ICommand getCommand();

    SansaConnection getConnection();
}
