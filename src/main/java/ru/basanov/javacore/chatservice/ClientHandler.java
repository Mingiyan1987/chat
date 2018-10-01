package ru.basanov.javacore.chatservice;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread{
    private MyServer myServer;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String nameNick;

    public String getNameNick() {
        return nameNick;
    }

    public ClientHandler(MyServer myServer, Socket socket) {
        try {
            this.myServer = myServer;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.nameNick = "";
        } catch (IOException e) {
            throw new RuntimeException("Проблемы при созданиии обработчика клиента");
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String str = in.readUTF();
                if (str.startsWith("/s")) {
                    String[] parts = str.split("/s");
                    String nick = myServer.getAuthService().getNickByLoginPass(parts[1], parts[2]);
                    if (nick != null) {
                        if (!myServer.isNickBusy(nick)) {
                            sendMsg("/authok " + nick);
                            this.nameNick = nick;
                            myServer.broadcastMsg(this.nameNick + " зашел в чат");
                            myServer.subscribe(this);
                            break;
                        } else sendMsg("Учетная запись уже используется");
                    } else {
                        sendMsg("Неверные логин/пароль");
                    }
                }
            }
            while (true) {
                String str = in.readUTF();
                if (str.startsWith("/")) {
                    if (str.equals("/end")) break;
                    if (str.startsWith("/w ")) {
                        String[] tokens = str.split("\\s");
                        String nick = tokens[1];
                        String msg = str.substring(4 + nick.length());
                        myServer.sendMsgToClient(this, nick, msg);
                    }
                } else {
                    myServer.broadcastMsg(nameNick + ": " + str);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            myServer.unsubscribe(this);
            myServer.broadcastMsg(nameNick + " вышел из чата");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
