package xyz.handsomelee.netty;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.handsomelee.Mapper.ChatMessageMapper;

@Component
public class SocketServerInitializer extends ChannelInitializer<SocketChannel> {

    public static ChatMessageMapper chatMessageMapper;
    @Autowired
    private ChatMessageMapper _chatMessageMapper;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        // 通过socket管道获得对应的管道
        ChannelPipeline pipeline = socketChannel.pipeline();
        // 通过管道添加handler
        /* HttpServerCodec 是由netty自己提供的助手类，可以理解为拦截器
            当请求到服务器，需要解码的时候，响应到客户端需要编码的时候
         */
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpObjectAggregator(1024*64));
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        pipeline.addLast(new SocketHandler());

        chatMessageMapper = _chatMessageMapper;
    }
}
