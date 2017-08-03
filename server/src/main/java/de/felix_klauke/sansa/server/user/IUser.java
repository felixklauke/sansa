package de.felix_klauke.sansa.server.user;

public interface IUser {

    String getUserName();

    boolean checkPassword(String password);
}
