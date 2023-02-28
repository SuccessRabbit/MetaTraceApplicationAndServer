package xyz.handsomelee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import xyz.handsomelee.service.SocketService;

import javax.annotation.Resource;

@Component
public class NettyStartListener implements ApplicationRunner {
    @Resource
    private SocketService socketService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.socketService.start();
    }
}
