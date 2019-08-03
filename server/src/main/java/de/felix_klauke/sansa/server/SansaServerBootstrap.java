package de.felix_klauke.sansa.server;


import com.google.inject.Guice;
import com.google.inject.Injector;
import de.felix_klauke.sansa.server.module.SansaModule;

/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public class SansaServerBootstrap {

  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new SansaModule());
    SansaServer sansaServer = injector.getInstance(SansaServer.class);
    sansaServer.start();
    sansaServer.registerUsers();
  }
}
