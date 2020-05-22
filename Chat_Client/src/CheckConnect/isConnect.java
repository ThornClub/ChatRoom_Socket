package CheckConnect;

import java.net.Socket;

/**
 * @Auther: 双Ge
 * @Date: 2020/5/15 16:37
 * @Description:判断Socket连接
 */
public class isConnect {
    public boolean isConnect(Socket socket){
        try {
            socket.sendUrgentData(0xFF);
            return false;
        }
        catch (Exception e) {
            return true;
        }
    }
}
