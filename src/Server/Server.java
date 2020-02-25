package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {

    private Vector<ClientHandler> clients;
    private AuthService authService;

    public AuthService getAuthService() {
        return authService;
    }

    public Server() {
        clients = new Vector<>();
        authService = new SimpleAuthService();
        ServerSocket server = null;
        Socket socket = null;

        try {
            server = new ServerSocket(3443);
            System.out.println("Сервер запустился");

            while (true) {
                socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(socket, this);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert server != null;
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastMsg(String msg){
        for (ClientHandler c: clients ) {
            c.sendMsg(msg);
        }
    }

    public void subscribe(ClientHandler clientHandler){
        clients.add(clientHandler);
    }

    public void unsubscribe(ClientHandler clientHandler){
        clients.remove(clientHandler);
    }

    public void personalMsg(ClientHandler from, String to, String message) {
        for (ClientHandler c : clients) {
            if (c.getNick().equals(to)) {
                c.sendMsg("сообщение от " + from.getNick() + ": " + message);
                from.sendMsg("сообщение для " + to + ": " + message);
                return;
            }
        }
        from.sendMsg("Пользователь " + to + " не найден в чате");
    }
}
