package de.felix_klauke.sansa.server;

import de.felix_klauke.sansa.commons.ftp.FTPRequest;
import de.felix_klauke.sansa.commons.ftp.FTPRequestContext;
import de.felix_klauke.sansa.server.user.IUser;

/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public interface SansaServer {

  /**
   * Start the server
   */
  void start();

  /**
   * Stop the server.
   */
  void stop();

  /**
   * Check if the server is running.
   *
   * @return The running state.
   */
  boolean isRunning();

  /**
   * Register a new user a client can auth against.
   *
   * @param user The user.
   */
  void registerUser(IUser user);

  /**
   * Register the default user
   */
  void registerUsers();

  void handleRequest(FTPRequestContext requestContext, FTPRequest ftpRequest);
}
