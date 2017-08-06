package de.felix_klauke.sansa.server.handler;

import de.felix_klauke.sansa.commons.ftp.ICommand;
import de.felix_klauke.sansa.server.connection.SansaConnection;

public abstract class BaseCommandHandler implements ICommandHandler {

    private final SansaConnection connection;
    private final ICommand command;

    public BaseCommandHandler(SansaConnection connection, ICommand command) {
        this.connection = connection;
        this.command = command;
    }

    public ICommand getCommand() {
        return command;
    }

    public SansaConnection getConnection() {
        return connection;
    }
}
