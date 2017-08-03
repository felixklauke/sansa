package de.felix_klauke.sansa.server.user;

public abstract class AbstractUser implements IUser {

    private final String userName;

    protected AbstractUser(String userName) {
        this.userName = userName;
    }

    @Override
    public String getUserName() {
        return userName;
    }
}
