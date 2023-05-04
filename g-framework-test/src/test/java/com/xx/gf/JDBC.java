package com.xx.gf;

/**
 * @author hanyangyang
 * @since 2023/5/4
 */
import java.sql.*;

public class JDBC {
    public static  void main(String[] args){
        Connection con = null;
        PreparedStatement pStatement = null;
        ResultSet res = null;
        try{
            // 1.注册驱动
            Driver driver = new com.mysql.cj.jdbc.Driver();
            DriverManager.registerDriver(driver);

            //2.获取连接
            String url = "jdbc:mysql://localhost:3306/hyy_test?serverTimezone=UTC";
            String user = "root";
            String password = "root";
            con = DriverManager.getConnection(url, user, password);

            //3.获取数据库操作对象
            String sql = "insert into user_info (id,name,age,email) values ( 200,0,0,'0@qq.com'),( 201,1,1,'1@qq.com' ) on duplicate key update id=VALUES(id),name=VALUES(name),age=VALUES(age),email=VALUES(email)";
            pStatement = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);

            //4.执行sql语句
            int i = pStatement.executeUpdate();
            System.out.println(i);
            ResultSet generatedKeys = pStatement.getGeneratedKeys();
            while (generatedKeys.next()){
                System.out.println(generatedKeys.getLong(1));
            }

        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            //6.释放资源，try中的变量无法在finally中使用，关闭资源需从小到大依次关闭
            try {
                if (res != null) {
                    res.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (pStatement != null) {
                    pStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
}

