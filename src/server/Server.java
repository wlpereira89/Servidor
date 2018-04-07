/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.*;
import java.net.*;
import java.util.*;

public final class Server {

    public static void main(String argv[]) throws Exception {
        //Set the port number.
        int port = 6789;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: "+ port);
            System.exit(1);
        }
        boolean serveron = true;
        while (serveron) {
            
            //Listen for a TCP connection request.
            try {
                Socket novo;
                novo = serverSocket.accept();                

                HttpRequest request = new HttpRequest(novo);
                Thread thread = new Thread(request);
                thread.start();
                
            } catch (Exception e) {
                System.out.println(e);
            }
            
            

        }
    }
}
