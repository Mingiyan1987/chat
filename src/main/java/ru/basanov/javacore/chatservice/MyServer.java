package ru.basanov.javacore.chatservice;

import ru.basanov.javacore.api.chatservice.AuthService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {
    public ServerSocket serverSocket;
    private List<ClientHandler> clients;
    private AuthService authService = new BaseAuthService();
    private final int PORT = 8189;

    public AuthService getAuthService() {
        return authService;
    }

    public MyServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            Socket socket = null;
            authService.start();
            clients = new ArrayList<ClientHandler>();
            while (true) {
                System.out.println("Сервер ожидает подключения");
                socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(this, socket);
                clientHandler.start();
                System.out.println("Клиент подключился");
            }
        } catch (IOException e) {
            System.out.println("Ошибка при работе сервера");
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            authService.stop();
        }
    }

    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler o: clients) {
            if (o.getName().equals(nick)) return true;
        }
        return false;
    }

    public synchronized void broadcastMsg(String msg) {
        for (ClientHandler o: clients) {
            o.sendMsg(msg);
        }
    }

    public synchronized void unsubscribe(ClientHandler o) {
        clients.remove(o);
        broadcastClientList();
    }

    public synchronized void subscribe(ClientHandler o) {
        clients.add(o);
        broadcastClientList();
    }

    public synchronized void sendMsgToClient(ClientHandler from, String nickTo, String msg) {
        for (ClientHandler o: clients) {
            if (o.getNameNick().equals(nickTo)) {
                o.sendMsg("от " + o.getNameNick() + ": " + msg);
                from.sendMsg("клиенту " + nickTo + ": " + msg);
                return;
            }
        }
        from.sendMsg("Участника с ником" + nickTo + " нет в чат комнате");
    }

    public synchronized void broadcastClientList() {
        StringBuilder sb = new StringBuilder("/clients");
        for (ClientHandler o: clients) {
            sb.append(o.getNameNick() + " ");
        }
        String msg = sb.toString();
        broadcastMsg(msg);
    }
}
