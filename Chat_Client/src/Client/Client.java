package Client;

import CheckConnect.*;
import FileToStringUtils.FileToStringUtils;
import Login_Client.Login_UI;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;

/**
 * @Auther: 双Ge
 * @Date: 2020/5/15 09:36
 * @Description:客户端代码
 */
public class Client implements Runnable {
    public static Socket socket;
    @Override
    public void run() {
        Client_UI client_ui = new Client_UI();
        isConnect isConnect = new isConnect();
        Login_UI login_ui = new Login_UI();
        SendAndRec sendAndRec = new SendAndRec();
        try {
            //获取用户输入的IP
            socket = new Socket(login_ui.IP.getText(),20700);
            if (isConnect.isConnect(socket) == false) {
                //发送用户名
                sendAndRec.Send(socket, client_ui.UserName);
                Thread.sleep(1);
                client_ui.ShowMessage.appendText("连接成功" + "\r\n");
                //启动线程
                new Thread(new SendServerMessage(socket, client_ui.Sendbt, client_ui.InputMessage, client_ui.ShowMessage, client_ui.UserName)).start();
                new Thread(new RecServerMessage(socket, client_ui.ShowMessage, client_ui.list, client_ui.ShowFriends)).start();
                new Thread(new Private_Chat(client_ui.ShowFriends, client_ui.InputMessage)).start();
            } else {
                client_ui.ShowMessage.appendText("与服务端连接失败" + "\r\n");
            }
        } catch (ConnectException e) {
            client_ui.ShowMessage.appendText("连接失败,请检查IP是否正确" + "\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

//创建线程向服务端发送信息
class SendServerMessage implements Runnable {
    Socket socket;
    Button SendBt;
    TextField InputMessage;
    TextArea ShowMessage;
    String UserName;

    public SendServerMessage(Socket socket, Button sendBt, TextField inputMessage, TextArea showMessage, String userName) {
        this.socket = socket;
        SendBt = sendBt;
        InputMessage = inputMessage;
        ShowMessage = showMessage;
        UserName = userName;
    }

    @Override
    public void run() {
        SendAndRec sendAndRec = new SendAndRec();
        synchronized (this) {
            while (true) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //从输入框获取信息
                String message = InputMessage.getText();
                //响应按钮事件
                SendBt.setOnAction(event -> {
                    //在界面显示信息
                    ShowMessage.appendText(UserName + ":" + message + "\r\n");
                    //发送信息
                    sendAndRec.Send(socket, (1+message));
                    //输入框制空
                    InputMessage.setText("");
                });
            }
        }
    }
}

//创建进程接受服务端消息
class RecServerMessage implements Runnable {
    //创建对象
    Socket socket;
    TextArea ShowMessage;
    ObservableList<String> list;
    ListView<String> ShowFriends;
    //构造方法


    public RecServerMessage(Socket socket, TextArea showMessage, ObservableList<String> list, ListView<String> showFriends) {
        this.socket = socket;
        ShowMessage = showMessage;
        this.list = list;
        ShowFriends = showFriends;
    }

    @Override
    public void run() {
        SendAndRec sendAndRec = new SendAndRec();
        RecFileTip recFileTip = new RecFileTip();
        synchronized (this) {
            while (true) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    //接收消息
                    String mes = sendAndRec.Rec(socket);
                    //字符串切割获取消息类型 1:接收好友列表 2:接收文本信息 3:更新好友列表 4.接收文件
                    int pd = Integer.parseInt(mes.substring(0, 1));
                    String mes1 = mes.substring(1);
                    if (pd == 1) {
                        Platform.runLater(() -> {
                            list.add(mes1 + "\r\n");
                        });
                        ShowFriends.setItems(list);
                    } else if (pd == 2) {
                        ShowMessage.appendText(mes1 + "\r\n");
                    } else if (pd == 3) {
                        Platform.runLater(() -> {
                            list.remove(mes1 + "\r\n");
                        });
                        ShowFriends.setItems(list);
                    }
                    else if (pd == 4){
                        String ClientName = mes1.substring(0,mes1.indexOf("*"));
                        String FileName = mes1.substring(mes1.indexOf("*")+1,mes1.indexOf("#"));
                        String FileString = mes1.substring(mes1.indexOf("#")+1);
                        Platform.runLater(()->{
                            recFileTip.Tips("用户"+ClientName+"发来"+FileName+"是否接收?");
                            recFileTip.rec.setOnAction(event -> {
                                //创建文件夹选择按钮
                                DirectoryChooser directoryChooser=new DirectoryChooser();
                                File file = directoryChooser.showDialog(recFileTip.Tip_Stage);
                                String path = file.getPath();
                                FileToStringUtils.stringSaveAsFile(FileString,path+FileName);
                                //关闭面板
                                recFileTip.Tip_Stage.close();
                                ShowMessage.appendText("文件接收完成,请到文件夹查看!"+"\r\n");
                            });
                            recFileTip.cancel.setOnAction(event -> {
                                try {
                                    //拒绝后关闭socket输入流
                                    socket.shutdownInput();
                                    recFileTip.Tip_Stage.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        });
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

//开启私聊线程
class Private_Chat implements Runnable {
    ListView<String> ShowFriends;
    TextField InputMessage;

    public Private_Chat(ListView<String> showFriends, TextField inputMessage) {
        ShowFriends = showFriends;
        InputMessage = inputMessage;
    }

    @Override
    public void run() {
        synchronized (this) {
            ShowFriends.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    //将私聊关键字添加至文本框
                    InputMessage.setText("@" + newValue + ":");
                }
            });
        }
    }
}
//发送和接收方法
class SendAndRec {
    public void Send(Socket socket, String Message) {
        try {
            BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
            DataOutputStream out = new DataOutputStream(outputStream);
            byte[] bytes = Message.getBytes();     //消息内容
            int totalLen = 4 + bytes.length;       //消息长度
            out.writeInt(totalLen);                //写入消息长度
            out.write(bytes);                      //写入消息内容
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String Rec(Socket socket){
        Client_UI client_ui = new Client_UI();
        try {
            BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
            DataInputStream in = new DataInputStream(inputStream);
            int totalLen = in.readInt();             //读取消息长度
            byte[] data = new byte[totalLen - 4];    //定义存放消息内容的字节数组
            in.readFully(data);                      //读取消息内容
            String Message = new String(data);            //消息内容
            return Message;
        } catch (SocketException e) {
            client_ui.ShowMessage.appendText("服务端已经关闭,请关闭会话" + "\r\n");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
