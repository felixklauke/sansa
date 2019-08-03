package de.felix_klauke.sansa.server.module;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import de.felix_klauke.sansa.server.SansaServer;
import de.felix_klauke.sansa.server.SimpleSansaServer;
import de.felix_klauke.sansa.server.initializer.SansaServerChannelInitializer;
import de.felix_klauke.sansa.server.user.IUserManager;
import de.felix_klauke.sansa.server.user.SimpleUserManager;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class SansaModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(IUserManager.class).to(SimpleUserManager.class).asEagerSingleton();
    bind(SansaServer.class).to(SimpleSansaServer.class).asEagerSingleton();
    bind(new TypeLiteral<ChannelInitializer<SocketChannel>>() {
    }).to(SansaServerChannelInitializer.class).asEagerSingleton();
  }
}
