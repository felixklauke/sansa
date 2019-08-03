package de.felix_klauke.sansa.commons.ftp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author Felix 'SasukeKawaii' Klauke
 */
public class FTPResponseEncoder extends MessageToByteEncoder<FTPResponse> {

  protected void encode(ChannelHandlerContext channelHandlerContext, FTPResponse ftpResponse,
      ByteBuf byteBuf) throws Exception {
    ByteBufUtil.writeUtf8(byteBuf,
        ftpResponse.getStatus().getStatusId() + " " + ftpResponse.getMessage() + "\n");
  }
}
