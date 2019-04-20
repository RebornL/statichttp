package com.statichttp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;

public class Request {

    private InputStream inputStream;
    // 请求资源
    private String uri;
    // 资源后缀
    private String suffix;
    private HashMap<String, String> req = new HashMap<>();

    public Request(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void parse() {

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String requestHeader = null;
        try {
            // 读取所有浏览器发送过来的请求参数头部的所有信息
            requestHeader = reader.readLine();
            String header = null;
            while ((header = reader.readLine()).length() != 0) {
                String[] headerKV = header.split(":");
                if (headerKV.length > 0) {
                    req.put(headerKV[0], headerKV[1].strip());
                    if (headerKV[0].equals("If-None-Match")) System.out.println(headerKV[1].strip());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(requestHeader);
        uri = ParseUri(requestHeader);
        suffix = GetSuffix(uri);
    }



    public String getUri() {
        return uri;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getHeader(String param) {
        return req.get(param);
    }

    private static String ParseUri(String request) {
        if (request ==null) return null;
        int index1, index2;
        index1 = request.indexOf(' ');
        if (index1 != -1) {
            index2 = request.indexOf(' ', index1+1);
            if (index2>index1) {
                return request.substring(index1+1, index2);
            }
        }
        return null;
    }

    private static String GetSuffix(String resource) {
        String suffix = null;
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
}
