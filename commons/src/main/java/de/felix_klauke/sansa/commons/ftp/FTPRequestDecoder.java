package de.felix_klauke.sansa.commons.ftp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public class FTPRequestDecoder extends ByteToMessageDecoder {

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        byte[] readableBytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(readableBytes);
        String content = new String(readableBytes);

        FTPCommand command = FTPCommand.getCommandViaContent(content);

        int commandOffset = command.getCommand().length() + 1;
        int argsSize = content.length() - 2;

        String[] args = new String[0];

        if (argsSize > commandOffset) {
            args = content.substring(commandOffset, argsSize).split(" ");
        }

        FTPRequest request = new FTPRequest(command, args);
        list.add(request);
    }
}
