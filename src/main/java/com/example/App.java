package com.example;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class App {
    public static void main(String[] args) throws IOException {
        Calculator calc = new Calculator();
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);

        server.createContext("/", exchange -> {
            String msg = "Hello from Jenkins CI/CD! 2 + 3 = " + calc.add(2, 3);
            byte[] body = msg.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });

        server.start();
        System.out.println("Server running on port 8081");
    }
}
