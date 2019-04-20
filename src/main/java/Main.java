import com.statichttp.HttpServer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        try {
            server.StartAndRunServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
