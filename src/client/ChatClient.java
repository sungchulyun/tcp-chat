package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
    public static void main(String[] args) {
        String serverIp = "127.0.0.1";
        int serverPort = 8888;

        try (
                Socket socket = new Socket(serverIp, serverPort);
                BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                ){
            System.out.println("채팅 서버에 연결됨");

            Thread receiver = new Thread(() -> {
                try {
                    String message;
                    while((message = in.readLine()) != null){
                        System.out.println("💬 " + message);
                    }
                } catch (IOException e) {
                    System.out.println("❌ 서버 연결 끊김");
                }
            });
            receiver.start();

            String input;
            while((input = keyboard.readLine()) != null){
                out.println(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
