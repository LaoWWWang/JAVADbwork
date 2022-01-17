package west2dbwork;

import java.sql.*;

public class Db {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/west2?" +//记得改数据库名字
            "useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String USER = "root";
    static final String PASS = "";//记得写密码

    public static Connection getConnection() {//链接数据库
        Connection conn = null;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void addCity(String cityName,int id,float lat,float lon) {             //添加城市信息
        String sql = "replace into city_information(name, id, lat, lon) values(?,?,?,?)";//replace应该就是更新吧，
        Connection conn = null;                                                          //Update也不知道该改些啥
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, cityName);
            pstmt.setInt(2,id);
            pstmt.setFloat(3,lat);
            pstmt.setFloat(4,lon);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(pstmt);
            close(conn);
        }
    }

    public static void addCityTemp(int id,String fxDate,int tempMax,int tempMin,String textDay){//添加天气信息
        String sql = "replace into city_weather(id, fxDate, tempMax, tempMin, textDay) values(?,?,?,?,?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,id);
            pstmt.setString(2,fxDate);
            pstmt.setInt(3,tempMax);
            pstmt.setInt(4,tempMin);
            pstmt.setString(5,textDay);
            pstmt.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }finally {
            close(pstmt);
            close(conn);
        }
    }

    public static void search(int page){        //说实话，我感觉我写错了，这个分页查询
        String sql = "SELECT * FROM city_information ORDER BY id DESC LIMIT 3 OFFSET ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,3*(page - 1));
            rs = pstmt.executeQuery();
            while(rs.next()){
                System.out.println(rs.getString("name")+','+rs.getInt("id")
                +','+rs.getFloat("lat")+','+rs.getFloat("lon"));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }finally {
            close(rs);
            close(pstmt);
            close(conn);
        }
    }

    public static void search(String name){//根据名字查询城市全部信息
        String sql = "SELECT * FROM city_information i INNER JOIN city_weather w ON i.id = w.id where i.name = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,name);
            rs = pstmt.executeQuery();
            while(rs.next()){
                System.out.println(rs.getString("name")+','+rs.getInt("id")
                        +','+rs.getFloat("lat")+','+rs.getFloat("lon")+','
                +rs.getString("fxDate")+','+rs.getInt("tempMax")+','+rs.getInt("tempMin")
                        +','+rs.getString("textDay"));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }finally {
            close(rs);
            close(pstmt);
            close(conn);
        }
    }

    public static void close(PreparedStatement pstmt) {
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
