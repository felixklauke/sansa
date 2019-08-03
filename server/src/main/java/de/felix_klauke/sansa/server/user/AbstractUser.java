package de.felix_klauke.sansa.server.user;

public abstract class AbstractUser implements IUser {

  private final String userName;
  private final boolean needsAuthentication;

  private AbstractUser(String userName, boolean needsAuthentication) {
    this.userName = userName;
    this.needsAuthentication = needsAuthentication;
  }

  AbstractUser(String userName) {
    this(userName, true);
  }

  @Override
  public String getUserName() {
    return userName;
  }

  @Override
  public boolean needsAuthentication() {
    return needsAuthentication;
  }
}
