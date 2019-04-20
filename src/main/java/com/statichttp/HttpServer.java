package com.statichttp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HttpServer {

    public static String STATIC_ROOT = System.getProperty("user.dir") + File.separator + "static";

    private static String SHUTDOWN_COMMAND = "/SHUTDOWN";
    private static int PORT = 22222;

    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(30, 100, 30, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(30));

//    public static void main(String[] args) {
//        //初始化读取配置文件
//        ReadProperties();
//
//        try {
//            StartAndRunServer();
//        } catch (IOException e) {
//            System.out.println("服务器启动出错！");
//            e.printStackTrace();
//        }
//
//    }
    static {
        ReadProperties();
    }


    public void StartAndRunServer() throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        Socket client = null;
        System.out.println("静态文件服务器正在运行,端口:" + PORT);
        System.out.println();
        while (true) {
            client = server.accept();
            System.out.println(client + "连接到HTTP服务器");

            Request request = new Request(client.getInputStream());
            request.parse();

            if (request.getUri().equals(SHUTDOWN_COMMAND)) {
                System.out.println("网页端关闭服务器");
                break;
            }


            Response response = new Response(client.getOutputStream());
            EXECUTOR.execute(new Handler(client, request, response));
        }

        // 关闭线程池和服务器
        EXECUTOR.shutdown();
        server.close();
    }

    private static void ReadProperties() {
        Properties props = new Properties();
        InputStream fis = HttpServer.class.getResourceAsStream("../../config.properties");
        try {
            props.load(fis);
        } catch (IOException e) {
            System.out.println("配置文件读取出错");
            e.printStackTrace();
        }
        PORT = Integer.parseInt(props.getProperty("port"));
        STATIC_ROOT = System.getProperty("user.dir")+ File.separator+props.getProperty("static_path");

    }

}
