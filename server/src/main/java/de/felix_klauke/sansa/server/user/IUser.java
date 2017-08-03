package de.felix_klauke.sansa.server.user;

public interface IUser {

    /**
     * Get the name of the user to authenticate with.
     *
     * @return The user name.
     */
    String getUserName();

    /**
     * Check if the user can be authenticated with the given secret.
     *
     * @param password The secret.
     * @return If the user could be authenticated.
     */
    boolean checkPassword(String password);
}
