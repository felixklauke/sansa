package de.felix_klauke.sansa.commons.ftp;

/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public class FTPRequest {

  private final String rawCommand;
  private final FTPCommand command;

  public FTPRequest(String rawCommand, FTPCommand command) {
    this.rawCommand = rawCommand;
    this.command = command;
  }

  public FTPCommand getCommand() {
    return command;
  }

  public String getRawCommand() {
    return rawCommand;
  }

  public String getCommandArgument(int index) {
    return rawCommand.split(" ")[index + 1];
  }
}
