package Server;

import CheckConnect.isConnect;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sun.misc.BASE64Decoder;
import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

/**
 * @Auther: 双Ge
 * @Date: 2020/5/15 21:42
 * @Description:服务端代码
 */
public class Server implements Runnable {
    //新建客户端接收连接
    public static Socket RecClient;
    public static List<String> FriendUsername;
    public static Map<Socket, String> Friend;
    @Override
    public void run() {
        //调用Server_UI处理页面交互
        Server_UI server_ui = new Server_UI();
        //创建Map集合以键值对的方式储存客户端连接
        Friend = new HashMap<>();
        FriendUsername = new ArrayList<>();
        try {
            //开启服务端
            ServerSocket serverSocket = new ServerSocket(20700);
            SendAndRec sendAndRec = new SendAndRec();
            //循环接收客户端连接
            synchronized (this) {
                while (true) {
                    Thread.sleep(1);
                    //使accept()方法阻塞，接收客户端连接
                    RecClient = serverSocket.accept();
                    //接收用户名
                    String name = sendAndRec.Rec(RecClient);
                    //添加进Map集合
                    Friend.put(RecClient, name);
                    FriendUsername.add(name);
                    //添加进list集合
                    /*解决java.lang.IllegalStateException: Not on FX application thread
                     *运行javaFX程序，系统会自动创建一个FX application thread线程，用于更新界面的组件信息，
                     *例如ListView的items、Label的text。
                     *当我们想运用多线程实现业务，而自己创建的线程又直接导致了界面组件信息的更新时，
                     *控制台就会抛出java.lang.IllegalStateException异常。
                     *之所以抛出异常，是因为javaFX程序不允许用户在FX application thread线程外更新界面的组件信息，
                     *换句话说，所有的更新界面组件的信息的代码，都应该在在FX application thread线程中执行。
                     *解决办法还是有的，用Platform类的runLater方法可以解决该问题。
                     */
                    Platform.runLater(() -> {
                        //将用户加入集合
                        server_ui.list.add(name + "\r\n");
                    });
                    //在页面显示朋友列表
                    server_ui.ShowFriends.setItems(server_ui.list);
                    //输出提示信息提醒那位朋友上线
                    String OnlineMessage = name + "上线了！" + "\r\n";
                    server_ui.ShowMessage.appendText(OnlineMessage);
                    //启动接收信息和发送信息的线程
                    new Thread(new SendFriendList(Friend, FriendUsername, RecClient)).start();
                    new Thread(new RecClientMessage(RecClient, FriendUsername, Friend, server_ui.ShowFriends, server_ui.list, server_ui.ShowMessage)).start();
                    new Thread(new SendMessage(Friend, server_ui.Sendbt, server_ui.InputMessage, server_ui.ShowMessage)).start();
                }
            }
        } catch (BindException e) {
            //处理端口重复绑定异常
            server_ui.ShowMessage.appendText("端口已经绑定或端口被其他应用占用！" + "\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

//发送好友列表
class SendFriendList implements Runnable {
    Map<Socket, String> Fr;
    List<String> Username;
    Socket Client;

    public SendFriendList(Map<Socket, String> fr, List<String> username, Socket client) {
        Fr = fr;
        Username = username;
        Client = client;
    }

    @Override
    public void run() {
        /*
        线程安全问题发生本质：多个线程他们在操作共享的数据（资源）。
        而CPU在执行线程的过程中操作共享资源的代码还没有彻底执行完，CPU就切换到其他线程上，导致数据不一致。
        解决安全问题：线程的同步技术。
        同步的目的就是保证有一个线程在执行任务代码的时候，其他线程要在外面等待。【排队访问资源】
         */
        synchronized (this) {
            SendAndRec sendAndRec = new SendAndRec();
            for (Socket client :
                    Fr.keySet()) {
                if (client == Client) {
                    for (String s : Username
                    ) {
                        try {
                            sendAndRec.Send(client, (1 + s));
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {

                    sendAndRec.Send(client, (1 + Fr.get(Client)));
                }
            }
        }
    }
}

//设置多线程接受客户端返回的消息并转发
class RecClientMessage implements Runnable {
    //客户端连接
    private Socket client;
    //Map集合
    List<String> FriendUsername;
    Map<Socket, String> Friend;
    ListView<String> ShowFriends;
    ObservableList<String> list = FXCollections.observableArrayList();
    TextArea ShowMessage;

    //构造方法
    public RecClientMessage(Socket client, List<String> friendUsername, Map<Socket, String> friend, ListView<String> showFriends, ObservableList<String> list, TextArea showMessage) {
        this.client = client;
        FriendUsername = friendUsername;
        Friend = friend;
        ShowFriends = showFriends;
        this.list = list;
        ShowMessage = showMessage;
    }

    @Override
    public void run() {
        SendAndRec sendAndRec = new SendAndRec();
        synchronized (this) {
            try {
                while (true) {
                    Thread.sleep(1);
                    String recMessage = sendAndRec.Rec(client);
                    int pd = Integer.parseInt(recMessage.substring(0, 1));
                    String ClientMessage = recMessage.substring(1);
                    if (pd==1){
                        int len = ClientMessage.indexOf("@");
                        int len1 = ClientMessage.indexOf(":");
                        if (len!=-1 && len1!=-1)
                        {
                            //在服务端页面显示客户端信息
                            ShowMessage.appendText(Friend.get(client) + ":" + ClientMessage + "\r\n");
                            String Username = ClientMessage.substring(len + 1, len1);
                            String Message = ClientMessage.substring(len1 + 1);
                            int i = 0;
                            for (Socket client :
                                    Friend.keySet()) {
                                i++;
                                if (Friend.get(client).equals(Username)) {
                                    sendAndRec.Send(client, (2 + (Friend.get(client)) + ":" + Message));
                                    break;
                                }
                                //将消息转发至所有客户端
                                if (i == Friend.size()) {
                                    for (Socket newClient : Friend.keySet()
                                    ) {
                                        //使用if清除本身
                                        if (this.client != newClient) {
                                            //创建输出流将消息转发至客户端
                                            sendAndRec.Send(newClient,(2 + (Friend.get(this.client) + ":" + ClientMessage)));
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            //在服务端页面显示客户端信息
                            ShowMessage.appendText(Friend.get(client) + ":" + ClientMessage + "\r\n");
                            for (Socket newClient : Friend.keySet()
                            ) {
                                //使用if清除本身
                                if (this.client != newClient) {
                                    //创建输出流将消息转发至客户端
                                    sendAndRec.Send(newClient,(2 + (Friend.get(this.client) + ":" + ClientMessage)));
                                }
                            }
                        }
                    }
                    else if (pd==2){
                        String FileName = ClientMessage.substring(0,ClientMessage.indexOf("#"));
                        String FileString = ClientMessage.substring(ClientMessage.indexOf("#")+1);
                        //Base64解密
                        BASE64Decoder decoder = new BASE64Decoder();
                        byte[] b = decoder.decodeBuffer(FileString);
                        //Base64加密
                        String out = new sun.misc.BASE64Encoder().encodeBuffer(b);
                        for (Socket newClient : Friend.keySet()
                        ) {
                            //使用if清除本身
                            if (this.client != newClient) {
                                //创建输出流将消息转发至客户端
                                sendAndRec.Send(newClient,(4+Friend.get(this.client)+"*"+FileName+"#"+out));
                            }
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

//服务端发送信息
class SendMessage implements Runnable {
    Map<Socket, String> Friend;
    Button Sendbt;
    TextField InputMessage;
    TextArea ShowMessage;

    //构造方法
    public SendMessage(Map<Socket, String> friend, Button sendbt, TextField inputMessage, TextArea showMessage) {
        Friend = friend;
        Sendbt = sendbt;
        InputMessage = inputMessage;
        ShowMessage = showMessage;
    }

    @Override
    public void run() {
        synchronized (this) {
            while (true) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //从输入框接收信息
                String message = InputMessage.getText();
                SendAndRec sendAndRec = new SendAndRec();
                //创建发送按钮的活动
                Sendbt.setOnAction(event -> {
                    //显示提示信息
                    ShowMessage.appendText("服务端:" + message + "\r\n");
                    //for循环将消息发送至所以列表
                    for (Socket socket :
                            Friend.keySet()) {
                        sendAndRec.Send(socket, (2 + "服务端:" + message));
                        //输入框置空
                        InputMessage.setText("");
                    }
                });
            }
        }
    }
}

//发送和接收方法
class SendAndRec {
    public void Send(Socket socket, String Message) {
        try {
            BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
            DataOutputStream out = new DataOutputStream(outputStream);
            byte[] bytes = Message.getBytes();         //消息内容
            int totalLen = 4 + bytes.length;         //消息长度
            out.writeInt(totalLen);                //写入消息长度
            out.write(bytes);                      //写入消息内容
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String Rec(Socket socket) {
        SendAndRec sendAndRec = new SendAndRec();
        Server server = new Server();
        Server_UI server_ui = new Server_UI();
        try {
            BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
            //循环读取输入内容
            //创建一块1k的缓冲区
                /*
                    通过使用平台的默认字符集解码指定的字节子阵列来构造新的String 。 新的String的长度是字符集的函数，因此可能不等于子数组的长度。
                    参数
                     bytes - 要解码为字符的字节
                     offset - 要解码的第一个字节的索引
                     length - 要解码的字节数
                */
            DataInputStream in = new DataInputStream(inputStream);
            int totalLen = in.readInt();             //读取消息长度
            byte[] data = new byte[totalLen - 4];    //定义存放消息内容的字节数组
            in.readFully(data);                      //读取消息内容
            String Message = new String(data);       //消息内容
            return Message;
        }catch (SocketException e) {
            //若客户端关闭连接，将抛出异常
            //调用方法判断客户端是否还连接
            isConnect Connect = new isConnect();
            for (Socket socket1 : server.Friend.keySet()
            ) {
                if (Connect.isConnect(socket1)) {
                    server.FriendUsername.remove(server.Friend.get(socket));
                    //输出提示信息
                    server_ui.ShowMessage.appendText(server.Friend.get(socket) + "下线了！" + "\r\n");
                    for (Socket client : server.Friend.keySet()
                    ) {
                        if (client != socket1) {
                            sendAndRec.Send(client, (3 + server.Friend.get(socket1)));
                        }
                    }
                    Platform.runLater(() -> {
                        //更新用户列表
                        if (server_ui.list.remove(server.Friend.get(socket) + "\r\n")) {
                            //删除集合中已经离线的用户
                            server.Friend.keySet().removeIf(key -> key == socket1);
                        }
                    });
                    server_ui.ShowFriends.setItems(server_ui.list);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}