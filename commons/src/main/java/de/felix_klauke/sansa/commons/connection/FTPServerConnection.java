package de.felix_klauke.sansa.commons.connection;

import java.io.File;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface FTPServerConnection {

    void setupSSL();

    void setUserName(String userName);

    void setPassword(String password);

    boolean isAuthenticated();

    File getUserWorkingPath();
}
