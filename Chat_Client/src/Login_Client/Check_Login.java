package Login_Client;

import JDBCUtil.JDBCUtils;
import Tips.Tips_UI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Auther: 双Ge
 * @Date: 2020/5/15 18:34
 * @Description:
 */
public class Check_Login {
    public boolean Check(String username,String password){
        Tips_UI tips_ui = new Tips_UI();
        Connection con;
        PreparedStatement pre;
        ResultSet set;
        con = JDBCUtils.getConnection();
        String sql = "select * from chat_user where username=?";
        String sql1 = "select * from chat_user where username=? and password=?";
        try {
            pre = con.prepareStatement(sql);
            pre.setString(1,username);
            set = pre.executeQuery();
            if (set.next())
            {
                pre = con.prepareStatement(sql1);
                pre.setString(1,username);
                pre.setString(2,password);
                set = pre.executeQuery();
                if (set.next())
                {
                    return true;
                }
                else
                {
                    tips_ui.Tips("用户名或密码错误");
                }
            }
            else {
                tips_ui.Tips("系统中暂无此用户");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
