package xyz.handsomelee.service;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xyz.handsomelee.netty.SocketServerInitializer;
import javax.annotation.Resource;


@Service
public class SocketService {
    @Resource
    private SocketServerInitializer socketServerInitializer;
    @Getter
    private ServerBootstrap serverBootstrap;
    @Value("${my.nettyPort}")
    private int port;

    public void start(){
        this.init();
    }

    private void init(){

        // 主线程池
        NioEventLoopGroup group_master = new NioEventLoopGroup();

        // 从线程池
        NioEventLoopGroup group_slave = new NioEventLoopGroup();
        this.serverBootstrap = new ServerBootstrap();
        this.serverBootstrap.group(group_master, group_slave)
                .channel(NioServerSocketChannel.class)
                .childHandler(this.socketServerInitializer); // 加入自定义初始化器

        try{
            ChannelFuture channelFuture = this.serverBootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            group_master.shutdownGracefully();
            group_slave.shutdownGracefully();
        }
    }

}
