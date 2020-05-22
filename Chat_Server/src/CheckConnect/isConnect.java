package CheckConnect;

import java.net.Socket;

/**
 * @Auther: 双Ge
 * @Date: 2020/5/15 16:37
 * @Description:
 */
public class isConnect {
    public boolean isConnect(Socket socket){
        try {
            /*
            * 往输出流发送一个字节的数据，只要对方Socket的SO_OOBINLINE属性没有打开，就会自动舍弃这个字节，
            * 而SO_OOBINLINE属性默认情况下就是关闭的
            * 判断连接的有效性
            * 如果'0XFF'发送成功就表示连接有效
            */
            socket.sendUrgentData(0xFF);
            return false;
        }
        catch (Exception e) {
            return true;
        }
    }
}
