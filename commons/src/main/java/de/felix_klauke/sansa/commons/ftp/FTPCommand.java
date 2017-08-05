package de.felix_klauke.sansa.commons.ftp;

/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public enum FTPCommand {

    OPTS("OPTS"),
    USER("USER"),
    PASS("PASS"),
    BYE("BYE"),
    SYST("SYST"),
    QUIT("QUIT"),
    UNKNOWN_COMMAND("UnknownCommand"),
    PWD("PWD"),
    TYPE("TYPE"),
    CHANGE_WORKING_DIR("CWD");

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

        return UNKNOWN_COMMAND;
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
