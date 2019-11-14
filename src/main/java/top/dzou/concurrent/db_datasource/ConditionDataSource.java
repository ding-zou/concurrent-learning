package top.dzou.concurrent.db_datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author dingxiang
 * @date 19-8-6 下午6:13
 */

/**
 * 数据库连接池 Condition和Lock实现
 */
public class ConditionDataSource {

    private List<Connection> pool = new LinkedList<>();
    private static final int INIT_CONNECTION = 10;
    private static String URL = "jdbc:mysql://localhost:3306/springboot";
    private static String USER = "dzou";
    private static String PASSWORD = "1234";
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public ConditionDataSource(){
        for(int i=0;i<INIT_CONNECTION;i++) {
            try {
                pool.add(DriverManager.getConnection(URL,USER,PASSWORD));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection(){
        lock.lock();
        try {
            while (pool.isEmpty()){
                condition.await();
            }
            System.out.println(Thread.currentThread().getName()+":获取到连接，目前连接池还有"+ (pool.size() - 1) +"个连接");
            return pool.remove(0);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            lock.unlock();
        }
    }

    public void release(Connection con) throws SQLException {
        lock.lock();
        try {
            if (con != null) {
                System.out.println(Thread.currentThread().getName()+":释放连接，目前连接池还有"+(pool.size() + 1)+"个连接");
                pool.add(con);
                condition.signal();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ConditionDataSource dataSource = new ConditionDataSource();
        for(int i=0;i<5;i++){
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
