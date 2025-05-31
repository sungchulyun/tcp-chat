package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

    private static final int PORT = 8888;
    private static final Set<PrintWriter> clientWriters = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        ExecutorService pool = Executors.newCachedThreadPool();
        System.out.println("채팅 서버 시작됨. 포트 : " + PORT);

        while (true) {
            Socket socket = serverSocket.accept();
            pool.execute(new ClientHandler(socket));
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run(){
            try(
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    ) {
                out = new PrintWriter(socket.getOutputStream(), true);
                clientWriters.add(out);

                String message;
                while((message = in.readLine()) != null){
                    System.out.println("수신" + message);
                    for(PrintWriter writer : clientWriters){
                        writer.println(message);
                    }
                }
            } catch (IOException e) {
                System.out.println("클라이언트 연결 끊김");
            } finally {
                if(out != null){
                    clientWriters.remove(out);
                } try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
