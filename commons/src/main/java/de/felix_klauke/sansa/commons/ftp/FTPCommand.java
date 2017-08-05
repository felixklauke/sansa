package de.felix_klauke.sansa.commons.ftp;

import de.felix_klauke.sansa.commons.exception.NoSuchCommandException;

/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public enum FTPCommand {

    OPTS("OPTS"),
    USER("USER"),
    PASS("PASS"),
    BYE("BYE"),
    QUIT("QUIT");

    private final String command;

    FTPCommand(String command) {
        this.command = command;
    }

    public static FTPCommand getCommandViaContent(String content) {
        for (FTPCommand command : FTPCommand.values()) {
            if (content.startsWith(command.getCommand())) {
                return command;
            }
        }

        throw new NoSuchCommandException("Could not find a command in content " + content);
    }

    public String getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return "FTPCommand{" +
                "command='" + command + '\'' +
                '}';
    }
}
