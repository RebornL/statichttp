package com.biostatichttp.http;

import java.io.*;
import java.util.Date;
import java.util.zip.GZIPOutputStream;

public class Response {

    private String encoding = "UTF-8";
    OutputStream outputStream;

    public Response(OutputStream outputStream) {
        this.outputStream = outputStream;
    }


    public void sendData(File file, String contentType, String md5) throws IOException {
        PrintStream out = new PrintStream(this.outputStream, true);
        FileInputStream fis = new FileInputStream(file);
        byte data[] = new byte[fis.available()];
        fis.read(data);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        GZIPOutputStream gout = new GZIPOutputStream(bout);
        gout.write(data);
        gout.close();
        byte[] gzipData = bout.toByteArray();

        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: " + contentType + ";charset=" + encoding);
        out.println("Content-Length: " + gzipData.length);
        out.println("Content-Encoding: gzip");
        out.println("Cache-Control: max-age=1234567, private, must-revalidate");
//            out.println("Cache-Control: no-cache");
        out.println("Last-Modified: "+new Date(file.lastModified()));
        out.println("ETag: "+md5);
        out.println();// 根据 HTTP 协议, 空行将结束头信息
        System.out.println("压缩器："+data.length);
        System.out.println("压缩后："+gzipData.length);
        out.write(gzipData);
        fis.close();

    }

    public void response304() {
        PrintStream out = new PrintStream(this.outputStream, true);
        out.println("HTTP/1.1 304 Not Modified");
        out.println();
        out.close();
        close();
    }

    public void Response404() throws IOException {
        PrintWriter out = new PrintWriter(this.outputStream, true);
        // 返回应答消息,并结束应答
        out.println("HTTP/1.0 404 NOTFOUND");
        out.println("Content-Type:text/html;charset=UTF-8");
        // 根据 HTTP 协议, 空行将结束头信息
        out.println();
        out.println("该资源在服务器中不存在");
        out.close();
        close();
    }

    public void Response500() throws IOException {
        PrintWriter out = new PrintWriter(this.outputStream, true);
        out.println("HTTP/1.0 500");// 返回应答消息,并结束应答
        out.println("");
        out.flush();
        out.close();
        close();
    }

    public void close() {
        try {
            this.outputStream.close();
        } catch (IOException e) {
            System.out.println("关闭输出流失败");
            e.printStackTrace();
        }
    }

}
