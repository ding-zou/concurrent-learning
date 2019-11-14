package top.dzou.concurrent.db_datasource;

/**
 * @author dingxiang
 * @date 19-8-10 下午7:54
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * 使用Semaphore实现数据库连接池
 */
public class SemaphoreDataSource {

    private List<Connection> pool = new LinkedList<>();
    private static final int INIT_CONNECTION = 5;
    private static String URL = "jdbc:mysql://localhost:3306/springboot";
    private static String USER = "dzou";
    private static String PASSWORD = "1234";
    private Semaphore semaphore;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public SemaphoreDataSource(){
        semaphore = new Semaphore(INIT_CONNECTION);
        for(int i=0;i<INIT_CONNECTION;i++) {
            try {
                pool.add(DriverManager.getConnection(URL,USER,PASSWORD));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection(){
        try {
            System.out.println(Thread.currentThread().getName()+":来获取连接");
            if(semaphore.availablePermits()<=0){
                System.out.println(Thread.currentThread().getName()+":无可用连接，等待获取");
            }
            semaphore.acquire();
            synchronized (this) {
                Connection con = pool.remove(0);
                System.out.println(Thread.currentThread().getName() + ":获取到连接," + con);
                return con;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void release(Connection con) {
        synchronized (this) {
            if (con != null) {
                semaphore.release();
                System.out.println(Thread.currentThread().getName() + ":释放连接," + con);
                pool.add(con);
            }
        }
    }

    public static void main(String[] args) {
        SemaphoreDataSource dataSource = new SemaphoreDataSource();
        for(int i=0;i<15;i++){
            new Thread(()->{
                try {
                    Connection con = dataSource.getConnection();
                    Thread.sleep(5000);
                    dataSource.release(con);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
