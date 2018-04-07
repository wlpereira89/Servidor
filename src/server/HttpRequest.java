/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author root
 */
final class HttpRequest implements Runnable {

    final static String CRLF = "\r\n";
    Socket socket;

    public HttpRequest(Socket socket) throws Exception {
        this.socket = socket;
    }

    private void processRequest() throws Exception {
        // Get a reference to the socket's input and output streams.
        InputStream is = this.socket.getInputStream();
        DataOutputStream os = new DataOutputStream(this.socket.getOutputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        // Get the request line of the HTTP request message.      
        String requestLine = br.readLine();
        System.out.println();
        System.out.println(requestLine);
        String headerLine = null;

        while ((headerLine = br.readLine()).length() != 0) {
            System.out.println(headerLine);
        }

        StringTokenizer tokens = new StringTokenizer(requestLine);
        tokens.nextToken();  // skip over the method, which should be "GET"
        String fileName = tokens.nextToken();

        fileName = "." + fileName;
        // Open the requested file.
        FileInputStream fis1 = null;
        boolean fileExists = true;
        try {
            fis1 = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            if (fileName.equals("./")) {
                fileName = "./index.html";
                fis1 = new FileInputStream(fileName);
            } else {
                fileExists = false;
            }
        }
        //Construct the response message.
        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;

        if (fileExists) {
            statusLine = "HTTP/1.0 200 OK" + CRLF;
            contentTypeLine = "Content-type: "
                    + contentType(fileName) + CRLF;
        } else {
            statusLine = "HTTP/1.0 404 NOT FOUND" + CRLF;
            contentTypeLine = "Content-type: " + "text/html" + CRLF;
            entityBody = "<html>\n" + "<head><title>Not Found</title></head>\n" + "<body>File not found</body>\n</html>\n";

        }
        // Send the status line.
        os.writeBytes(statusLine);
        // Send the content type line.
        os.writeBytes(contentTypeLine);
        // Send a blank line to indicate the end of the header lines.
        os.writeBytes(CRLF);

        // Send the entity body.
        if (fileExists) {
            sendBytes(fis1, os);
            fis1.close();
        } else {
            os.writeBytes(entityBody);
        }

        os.close();
        br.close();
        socket.close();
    }

    @Override
    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
        // Construct a 1K buffer to hold bytes on their way to the socket.
        byte[] buffer = new byte[1024];
        int bytes = 0;
        // Copy requested file into the socket's output stream.
        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }

    private static String contentType(String fileName) {
        if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        }
        if (fileName.endsWith(".gif") || fileName.endsWith(".GIF")) {
            return "image/gif";
        }
        if (fileName.endsWith(".jpeg")||fileName.endsWith(".jpg")) {
            return "image/jpeg";
        }
        if (fileName.endsWith(".png")){
            return "image/png";
        }
        if (fileName.endsWith(".sh")) {
            return "bourne/awk";
        }

        return "application/octet-stream";
    }
}
