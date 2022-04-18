package com.example.chapter01;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 该代码只能在 ie 中访问
 */
public class HttpServer {

    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";

    public static final String SHUTDOWN_COMMAND = "/shutdown";

    private boolean shutdown = false;

    public static void main(String[] args) throws IOException {
        new HttpServer().await();
    }

    public void await() throws IOException {
        int port = 8080;
        ServerSocket serverSocket = new ServerSocket(port, 1, InetAddress.getByName("localhost"));
        while (!shutdown) {

            Socket socket = serverSocket.accept();
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            Request request = new Request(input);
            request.parse();

            Response response = new Response(output);
            response.setRequest(request);
            response.sendStaticResource();

            socket.close();

            shutdown = request.getUri().equalsIgnoreCase(SHUTDOWN_COMMAND);

        }
    }
}
