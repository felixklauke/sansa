package de.felix_klauke.sansa.server;


/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public class SansaServerBootstrap {

    public static void main(String[] args) {
        SansaServer sansaServer = new SimpleSansaServer(userManager);
        sansaServer.start();

        sansaServer.registerUsers();
    }
}
