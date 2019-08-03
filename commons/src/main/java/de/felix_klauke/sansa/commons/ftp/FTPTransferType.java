package de.felix_klauke.sansa.commons.ftp;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public enum FTPTransferType {

  BINARY("I"),
  UNKNOWN("ERROR");

  private final String represent;

  FTPTransferType(String represent) {
    this.represent = represent;
  }

  public static FTPTransferType forSymbol(String typeSymbol) {
    for (FTPTransferType ftpTransferType : FTPTransferType.values()) {
      if (ftpTransferType.getSymbol().equals(typeSymbol)) {
        return ftpTransferType;
      }
    }

    return UNKNOWN;
  }

  public String getSymbol() {
    return represent;
  }
}
