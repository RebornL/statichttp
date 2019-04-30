package com.statichttp;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Handler implements Runnable {
    // 用来缓存已经被请求过的文件的md5值
    private static ConcurrentHashMap<String, String> File2Md5 = new ConcurrentHashMap<>();

    private Socket client;
    private Request request;
    private Response response;

    public Handler(Socket client, Request request, Response response) {
        this.client = client;
        this.request = request;
        this.response = response;
    }



    @Override
    public void run() {
        if (this.client != null) {
            try {
                // 获得请求的资源的地址
                String resource = this.request.getUri();
                System.out.println("用户请求的资源resource是:" + resource);
                // 根据后缀名判断
                String suffix = this.request.getSuffix();
                String contentType = MIME.getContentType(suffix);
                Path path = Paths.get(HttpServer.STATIC_ROOT, resource);
                File file = path.toFile();
                System.out.println(path);
                if (file.exists()) {
                    String md5 = null;
                    if (File2Md5.containsKey(resource)) {
                        md5 = File2Md5.get(resource);
                    } else {
                        md5 = MD5Util.md5HashCode32(file.getAbsolutePath());
                    }
                    if (this.request.getHeader("If-None-Match") != null && this.request.getHeader("If-None-Match").equals(md5)) {
                        this.response.response304();
                    } else {
                        this.response.sendData(file, contentType, md5);
                    }
                } else {
                    this.response.Response404();
                }
            } catch (Exception e) {
                System.out.println("HTTP服务器错误:" + e.getLocalizedMessage());
            } finally {
                closeSocket(this.client);
            }
        }
    }



    public static void closeSocket(Socket socket) {
        try {
            socket.close();
            System.out.println(socket + "离开了HTTP服务器");
            System.out.println();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
