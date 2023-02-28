package xyz.handsomelee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import xyz.handsomelee.service.UserClientHandler;

@SpringBootApplication
public class MetatraceApplication {

    public static void main(String[] args) {
        
        SpringApplication.run(MetatraceApplication.class, args);

        // 启动用户组管理线程
        Thread t = new Thread(new UserClientHandler());
        t.start();
    }

}
