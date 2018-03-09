package de.felix_klauke.sansa.commons.connection;

import de.felix_klauke.sansa.commons.ftp.FTPTransferType;

import java.io.File;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public interface FTPServerConnection {

    void setupSSL();

    void setUserName(String userName);

    void setPassword(String password);

    boolean isInActiveMode();

    void setActiveMode(boolean activeMode);

    boolean isAuthenticated();

    void setTransferType(FTPTransferType transferType);

    File getUserWorkingPath();
}
