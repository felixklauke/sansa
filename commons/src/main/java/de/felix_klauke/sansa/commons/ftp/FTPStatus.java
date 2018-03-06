package de.felix_klauke.sansa.commons.ftp;

/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public enum FTPStatus {

    BEGINNING_FILE_LIST_ASCII(150),
    OK(200),
    SYST_STATUS(215),
    READY(220),
    FILE_STATUS(226),

    ENTERED_PASSIVE_MODE(227),
    LOGGED_IN(230),
    WORKING_DIR_CHANGED(250),
    PASSWORD_NEEDED(331),
    LOGIN_INCORRECT(530),
    UNKNOWN_COMMAND(500),
    PATH_CREATED(257),
    SECURE_CONNECTION_ACCEPTED(234);

    private final int statusId;

    FTPStatus(int statusId) {
        this.statusId = statusId;
    }

    public static FTPStatus getStatusViaId(int statusId) {
        for (FTPStatus ftpStatus : FTPStatus.values()) {
            if (ftpStatus.getStatusId() == statusId) {
                return ftpStatus;
            }
        }

        return null;
    }

    public int getStatusId() {
        return statusId;
    }
}
