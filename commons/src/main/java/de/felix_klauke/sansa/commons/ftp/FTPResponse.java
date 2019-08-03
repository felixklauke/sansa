package de.felix_klauke.sansa.commons.ftp;

/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public class FTPResponse {

  private final FTPStatus status;
  private final String message;

  public FTPResponse(FTPStatus status, String message) {
    this.status = status;
    this.message = message;
  }

  public FTPStatus getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }
}
