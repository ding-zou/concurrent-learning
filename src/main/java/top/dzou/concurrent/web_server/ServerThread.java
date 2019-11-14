package top.dzou.concurrent.web_server;

import java.io.*;
import java.net.Socket;
import java.util.Date;

/**
 * @author dingxiang
 * @date 19-8-23 下午12:51
 */
public class ServerThread implements Runnable {
    //客户端socket
    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private static final String base = "/home/dzou/Documents/idea";
    private String path = "/home/dzou/Documents/idea/index.html";
    private static final String jpg = "image/jpeg";
    private static final String png = "image/png";
    private static final String ico = "image/x-icon";
    private static final String html = "text/html;charset=utf-8";

    public ServerThread(Socket socket){
        this.socket = socket;
    }

    /**
     * 初始化流对象
     * @throws IOException
     */
    private void init() throws IOException {
        is = socket.getInputStream();
        os = socket.getOutputStream();
    }

    @Override
    public void run() {
        try{
            init();
            handleRequest();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    /**
     * 处理请求
     * @throws IOException
     */
    private void handleRequest() throws IOException {
        //读取请求内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String len = "";
        if ((len = reader.readLine().split(" ")[1])!=null){
            System.out.println(len);
            if(len.equals("/")){
                len += "index.html";
            }
        }
        //响应
        PrintWriter out = new PrintWriter(os);
        assert len != null;
        String s = len.substring(len.lastIndexOf('/'));
        InputStream fis = new FileInputStream(base+s);
        //响应头
        out.println("HTTP/1.1 200 OK");
        out.println("Date: "+new Date());
        out.println("Server: Molly");
        if(len.endsWith(".jpg")){
            out.println("Content-Type: "+jpg);
        }else if(len.endsWith(".png")){
            out.println("Content-Type: "+png);
        }else if(len.endsWith(".ico")){
            out.println("Content-Type: "+ico);
        }else {
            out.println("Content-Type: "+html);
        }
        out.println("");
        out.flush();
        //响应体
        byte[] bytes = new byte[1024];
        int length = 0;
        while ((length = fis.read(bytes))!=-1){
            os.write(bytes,0,length);
        }
        out.flush();
    }

    /**
     * 关闭流
     */
    private void close(){
        try {
            is.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
