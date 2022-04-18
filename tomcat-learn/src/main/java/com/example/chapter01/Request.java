package com.example.chapter01;

import java.io.IOException;
import java.io.InputStream;

public class Request {

    private InputStream input;
    private String uri;

    public Request(InputStream input) {
        this.input = input;
    }

    public void parse() throws IOException {
        StringBuffer request = new StringBuffer(2048);
        byte[] buffer = new byte[2048];
        int i = input.read(buffer);
        for (int j = 0; j < i; j++) {
            request.append(((char) buffer[j]));
        }
        System.out.println(request);
        uri = parseUri(request.toString());
    }

    private String parseUri(String request) {
        int index1, index2;
        index1 = request.indexOf(' ');
        if (index1 != -1) {
            index2 = request.indexOf(' ', index1 + 1);
            if (index2 > index1) {
                return request.substring(index1 + 1, index2);
            }
        }
        return null;
    }

    public String getUri() {
        return uri;
    }
}
