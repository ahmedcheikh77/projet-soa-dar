package com.dme.distributed.socket;

import lombok.extern.slf4j.Slf4j;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Socket Server for streaming medical data (images, files)
 */
@Slf4j
public class MedicalDataSocketServer {
    
    private int port;
    private ServerSocket serverSocket;
    private boolean running;

    public MedicalDataSocketServer(int port) {
        this.port = port;
        this.running = false;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            log.info("Medical Data Socket Server started on port: {}", port);

            new Thread(() -> {
                while (running) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        log.info("Client connected: {}", clientSocket.getInetAddress());
                        
                        new Thread(new ClientHandler(clientSocket)).start();
                    } catch (IOException e) {
                        if (running) {
                            log.error("Error accepting client connection", e);
                        }
                    }
                }
            }).start();
        } catch (IOException e) {
            log.error("Error starting socket server", e);
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            log.info("Medical Data Socket Server stopped");
        } catch (IOException e) {
            log.error("Error stopping socket server", e);
        }
    }

    /**
     * Inner class to handle client connections
     */
    @Slf4j
    private static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
                 BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream())) {

                // Read request header
                byte[] buffer = new byte[1024];
                int bytesRead = inputStream.read(buffer);
                String request = new String(buffer, 0, bytesRead);
                log.info("Received request: {}", request);

                // Send response (simulated medical data)
                String response = "Medical Record Data: {\"patientId\": 1, \"diagnosis\": \"Sample diagnosis\"}";
                outputStream.write(response.getBytes());
                outputStream.flush();
                
                log.info("Response sent successfully");

            } catch (IOException e) {
                log.error("Error handling client request", e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    log.error("Error closing socket", e);
                }
            }
        }
    }
}
