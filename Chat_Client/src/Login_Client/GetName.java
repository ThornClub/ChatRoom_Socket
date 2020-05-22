package Login_Client;

import JDBCUtil.JDBCUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Auther: 双Ge
 * @Date: 2020/5/15 13:40
 * @Description:
 */
public class GetName {
    public String getName(String username) {
        Connection con = null;
        PreparedStatement pre = null;
        ResultSet set = null;
        con = JDBCUtils.getConnection();
        String sql = "select name from chat_user where username = ?";
        try {
            pre = con.prepareStatement(sql);
            pre.setString(1, username);
            set = pre.executeQuery();
            while (set.next()) {
                return set.getString("name");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return "";
    }

}
