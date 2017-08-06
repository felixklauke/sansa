package de.felix_klauke.sansa.commons.ftp;

import java.util.Arrays;

/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public class FTPRequest {

    private final FTPCommand command;
    private final String[] args;

    FTPRequest(FTPCommand command, String[] args) {
        this.command = command;
        this.args = args;
    }

    public FTPCommand getCommand() {
        return command;
    }

    public String[] getArgs() {
        return args;
    }

    public String getArgument(int index) {
        return getArgs()[index];
    }

    @Override
    public String toString() {
        return "FTPRequest{" +
                "command=" + command +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}
