package JDBCUtil;

import Tips.Tips_UI;

import java.io.*;
import java.sql.*;
import java.util.Properties;

/**
 * @author:张红双
 * @date:2019-11-01 22:03
 */
public class JDBCUtils {
    private static String url;
    private static String user;
    private static String password;
    private static String driver;
    /**
     * 配置文件只读取一次,使用静态代码块(随着方法运行只执行一次)
     */
    static {
        //创建Properties集合类,用于读取java的配置文件
        Properties pro = new Properties();
        //加载输入流
        InputStream in = JDBCUtils.class.getResourceAsStream("jdbc.properties");
        //加载文件
        try {
            pro.load(in);
            url = pro.getProperty("url");
            user = pro.getProperty("user");
            password = pro.getProperty("password");
            driver = pro.getProperty("driver");
            Class.forName(driver);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取连接
     * @return 连接对象
     */
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url,user,password);
        } catch (SQLException e) {
            Tips_UI tips_ui = new Tips_UI();
            tips_ui.Tips("数据库连接错误");
        }
        return null;
    }

    /**
     * 释放资源
     * @param sta
     * @param con
     */
    public static void close(PreparedStatement sta,Connection con){
        if (sta!=null)
        //防止空的对象调用close方法而造成空指针异常
        {
            try {
                sta.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (con!=null)
        {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 重载释放资源方法
     * @param set
     * @param sta
     * @param con
     */
    public static void close(ResultSet set,PreparedStatement sta, Connection con){
        if (set!=null)
        {
            try {
                set.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (sta!=null)
        {
            try {
                sta.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (con!=null)
        {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
