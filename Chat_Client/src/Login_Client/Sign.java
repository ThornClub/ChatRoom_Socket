package Login_Client;

import JDBCUtil.JDBCUtils;
import Tips.Tips_UI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Auther: 双Ge
 * @Date: 2020/5/15 09:29
 * @Description:
 */
public class Sign {
    public void Method_Sign(String username,String name,String password){
        Tips_UI tips_ui = new Tips_UI();
        Connection con;
        PreparedStatement pre = null;
        ResultSet set = null;
        con = JDBCUtils.getConnection();
        String sql = "insert into chat_user (name ,username ,password) values (?,?,?)";
        try {
            pre = con.prepareStatement(sql);
            pre.setString(1,name);
            pre.setString(2,username);
            pre.setString(3,password);
            int count = pre.executeUpdate();
            if (count!=0)
            {
                tips_ui.Tips("注册成功请返回登录");
            }
        } catch (SQLException throwables) {
            tips_ui.Tips("注册失败,请返回重试");
        }
        JDBCUtils.close(set,pre,con);
    }
}
