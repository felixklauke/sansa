package de.felix_klauke.sansa.commons.ftp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public class FTPRequestDecoder extends ByteToMessageDecoder {

  private Logger logger = LoggerFactory.getLogger(FTPRequestDecoder.class);

  protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf,
      List<Object> list) {
    int readableBytes = byteBuf.readableBytes();
    byte[] bytes = new byte[readableBytes];
    byteBuf.readBytes(bytes);

    String command = new String(bytes);

    // Cut off suffix
    if (command.endsWith("\r\n")) {
      command = command.substring(0, command.length() - 2);
    }

    FTPCommand ftpCommand = FTPCommand.forCommand(command);
    FTPRequest ftpRequest = new FTPRequest(command, ftpCommand);

    logger.info("Handling plain command: " + command);

    list.add(ftpRequest);
  }
}
