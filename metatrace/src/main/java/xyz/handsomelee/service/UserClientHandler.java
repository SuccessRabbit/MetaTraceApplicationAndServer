package xyz.handsomelee.service;


import xyz.handsomelee.netty.SocketHandler;

/**
 * 用于管理客户端的类
 */
public class UserClientHandler implements Runnable{
    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(1000 * 10);
            }catch (InterruptedException e){
                e.printStackTrace();
            }

            // 检测是否长时间没有收到来自客户端的消息
            for (String userid : SocketHandler.usersKeySet) {
                if(System.currentTimeMillis() - SocketHandler.USER_CHANNELS.get(userid).getLastMessageTimeStamp()
                    > 1000 * 120){
                    // TODO 也许需要进行处理
                    SocketHandler.USER_CHANNELS.get(userid).getClient().close();
                    SocketHandler.USER_CHANNELS.remove(userid);
                    SocketHandler.usersKeySet = SocketHandler.USER_CHANNELS.keySet();
                }
            }
        }
    }
}
