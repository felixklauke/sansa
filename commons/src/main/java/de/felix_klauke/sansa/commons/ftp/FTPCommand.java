package de.felix_klauke.sansa.commons.ftp;

/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public enum FTPCommand implements ICommand {

  AUTH_TLS("AUTH TLS"),
  OPTS("OPTS"),
  USER("USER"),
  PASS("PASS"),
  BYE("BYE"),
  SYST("SYST"),
  QUIT("QUIT"),
  UNKNOWN_COMMAND("UnknownCommand"),
  PWD("PWD"),
  TYPE("TYPE"),
  CHANGE_WORKING_DIR("CWD"),
  EPSV("EPSV"),
  EPRT("EPRT"),
  LIST("LIST"),
  RETRIEVE("RETR");

  private final String command;

  FTPCommand(String command) {
    this.command = command;
  }

  public static FTPCommand forCommand(String command) {
    for (FTPCommand ftpCommand : values()) {
      if (command.startsWith(ftpCommand.command)) {
        return ftpCommand;
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
