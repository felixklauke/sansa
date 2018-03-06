package de.felix_klauke.sansa.commons.ftp;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author Felix Klauke <info@felix-klauke.de>
 */
public class FTPRequestContext {

    private final ChannelHandlerContext channelHandlerContext;

    public FTPRequestContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    public void resume(FTPResponse response) {
        channelHandlerContext.channel().writeAndFlush(response);
    }

    public void setupSSL(String rawCommand) {
    }
}
