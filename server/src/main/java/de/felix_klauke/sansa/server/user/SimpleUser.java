package de.felix_klauke.sansa.server.user;

public class SimpleUser extends AbstractUser {

  private final String password;

  public SimpleUser(String userName, String password) {
    super(userName);
    this.password = password;
  }

  @Override
  public boolean checkPassword(String password) {
    return password.equals(this.password);
  }
}
