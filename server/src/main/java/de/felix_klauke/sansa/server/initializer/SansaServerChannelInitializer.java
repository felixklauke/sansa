package de.felix_klauke.sansa.server.initializer;

import de.felix_klauke.sansa.commons.ftp.FTPRequestDecoder;
import de.felix_klauke.sansa.commons.ftp.FTPResponseEncoder;
import de.felix_klauke.sansa.server.connection.SansaConnection;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public class SansaServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

        pipeline.addLast(new LoggingHandler(LogLevel.TRACE));
        pipeline.addLast(new FTPRequestDecoder());
        pipeline.addLast(new FTPResponseEncoder());
        pipeline.addLast(new SansaConnection(socketChannel));

        /*pipeline.addLast(new SimpleChannelInboundHandler<String>() {
            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                FTPResponse response = new FTPResponse(FTPStatus.READY, "Sansa will take over from here");
                ctx.channel().writeAndFlush(response);
            }

            @Override
            protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
                System.out.println("Request: '" + s + "'");

                if (s.contains("AUTH TLS")) {
                    channelHandlerContext.channel().writeAndFlush("234 ok\n");
                } else if (s.contains("USER felix")) {
                    channelHandlerContext.channel().writeAndFlush("331 password needed\n");
                } else if (s.contains("PASS test")) {
                    channelHandlerContext.channel().writeAndFlush("230 logged in\n");
                }
            }
        });*/
    }
}
