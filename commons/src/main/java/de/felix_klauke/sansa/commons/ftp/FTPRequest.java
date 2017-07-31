package de.felix_klauke.sansa.commons.ftp;

/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public class FTPRequest {

    private final FTPCommand command;

    public FTPRequest(FTPCommand command) {
        this.command = command;
    }

    public FTPCommand getCommand() {
        return command;
    }
}
