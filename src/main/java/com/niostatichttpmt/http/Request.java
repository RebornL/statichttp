package com.niostatichttpmt.http;

import com.niostatichttpmt.utils.Property;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.HashMap;

public class Request {

    private SocketChannel socketChannel;
    // 请求资源
    private String uri;
    // 资源后缀
    private String suffix;
    // 请求头部信息
    private HashMap<String, String> req = new HashMap<>();

    public Request(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public void parse() throws IOException {
        ByteBuffer readBuff = ByteBuffer.allocate(1024);
        readBuff.clear();
        String request = "";
        int readByte = 0;
        while ((readByte = socketChannel.read(readBuff)) > 0) {
            readBuff.flip();
            request += Property.CHARSET.decode(readBuff).toString();
            readBuff.clear();
        }
        if (request.length() <= 0) {
            System.out.println(LocalDateTime.now()+": 客户端连接不成功，直接断开");
            socketChannel.close();
            return;
        }
        String[] requestHeader = request.split("\r\n");
        this.uri = ParseUri(requestHeader[0]);
        assert uri != null;
        this.suffix = GetSuffix(uri);
        // 读取所有浏览器发送过来的请求参数头部的所有信息
        for (int i = 1; i < requestHeader.length; i++) {
            String[] headerKV = requestHeader[i].split(":");
            if (headerKV.length > 0) {
                //strip是jdk11的方法
                req.put(headerKV[0], headerKV[1].trim());
            }
        }

    }

    private static String ParseUri(String request) {
        if (request == null) {
            return null;
        }
        int index1, index2;
        index1 = request.indexOf(' ');
        if (index1 != -1) {
            index2 = request.indexOf(' ', index1+1);
            if (index2>index1) {
                String uri = request.substring(index1+1, index2);
                return "/".equals(uri) ? "/index.html" : uri;
            }
        }
        return null;
    }

    private static String GetSuffix(String resource) {
        // 默认以html
        String suffix = "html";
        if (resource.equals("/")) {
            resource = "/index.html";
            String[] names = resource.split("\\.");
            suffix = names[names.length - 1];
        } else {
            String[] names = resource.split("\\.");
            suffix = names[names.length - 1];
        }
        return suffix;
    }


    public String getUri() {
        return uri;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getHeader(String param) {
        return req.getOrDefault(param, null);
    }
}
