package top.dzou.concurrent.web_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author dingxiang
 * @date 19-8-23 下午12:49
 */

/**
 * 简易web服务器
 */
public class HttpWebServer {
    public static void main(String[] args) throws IOException {
        //开启服务器 监听8090端口
        ServerSocket serverSocket = new ServerSocket(8090);
        //使用线程池接收多请求
        ExecutorService pool = Executors.newCachedThreadPool();
        while (!Thread.interrupted()) {
            //接收请求并执行线程任务
            Socket socket = serverSocket.accept();
            pool.execute(new Thread(new ServerThread(socket)));
        }
        serverSocket.close();
    }
}
