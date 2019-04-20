import com.statichttp.HttpServer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        try {
            server.StartAndRunServer();
        } catch (IOException e) {
            System.out.println("服务器启动出错！");
            e.printStackTrace();
        }
    }
}
