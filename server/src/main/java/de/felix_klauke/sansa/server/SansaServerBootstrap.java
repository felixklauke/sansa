package de.felix_klauke.sansa.server;


/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public class SansaServerBootstrap {

    public static void main(String[] args) {
        SansaServer sansaServer = new SimpleSansaServer();
        sansaServer.start();
        sansaServer.registerUsers();
    }
}
