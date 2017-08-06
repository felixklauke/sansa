package de.felix_klauke.sansa.server.handler;

import de.felix_klauke.sansa.commons.ftp.FTPRequest;
import de.felix_klauke.sansa.commons.ftp.FTPResponse;
import de.felix_klauke.sansa.commons.ftp.ICommand;
import de.felix_klauke.sansa.server.connection.SansaConnection;

public class CommandHandlerPass extends BaseCommandHandler {
    public CommandHandlerPass(SansaConnection connection, ICommand command) {
        super(connection, command);
    }

    @Override
    public FTPResponse handleRequest(FTPRequest request) {
        return null;
    }
}
