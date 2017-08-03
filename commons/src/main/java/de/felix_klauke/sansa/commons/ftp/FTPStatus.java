package de.felix_klauke.sansa.commons.ftp;

/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public enum FTPStatus {

    READY(220),
    LOGGED_IN(230),
    PASSWORD_NEEDED(331);

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
