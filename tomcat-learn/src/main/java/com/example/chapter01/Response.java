package com.example.chapter01;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Response {

    private static final int BUFFER_SIZE = 1024;
    Request request;
    OutputStream output;

    public Response(OutputStream output) {
        this.output = output;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public void sendStaticResource() throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        File file = new File(HttpServer.WEB_ROOT, request.getUri());
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            int ch = fis.read(bytes, 0, BUFFER_SIZE);
            while (ch != -1) {
                output.write(bytes, 0, ch);
                ch = fis.read(bytes, 0, BUFFER_SIZE);
            }
            fis.close();
        } else {
            String errorMessage = "404 not found";
            output.write(errorMessage.getBytes(StandardCharsets.UTF_8));
        }
        output.flush();
    }
}


