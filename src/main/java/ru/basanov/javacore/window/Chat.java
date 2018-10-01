package ru.basanov.javacore.window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Chat extends JFrame{
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private JTextField inputWindow;
    private JTextArea messageArea;
    private JScrollPane messageWindow;
    private JButton theMessage;
    private JTextField loginField;
    private JTextField passField;
    private JButton clickAuth;
    private boolean authorised;
    private boolean session;
    private String myNick;
    private long endTimeMillis;


    public void setSession(boolean session) {
        this.session = session;
    }

    public boolean isSession() {
        return session;
    }

    public long getEndTimeMillis() {
        return endTimeMillis;
    }

    public void setEndTimeMillis(long endTimeMillis) {
        this.endTimeMillis = endTimeMillis;
    }

    public void setAuthorised(boolean authorised) {
        this.authorised = authorised;
    }

    public Chat() {
        setTitle("Чат");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(400, 150, 400, 400);
        setLayout(new BorderLayout());
        messageArea = new JTextArea(15,30);
        messageWindow = new JScrollPane(add(messageArea));
        loginField = new JTextField(10);
        passField = new JTextField(10);
        clickAuth = new JButton("Авторизоваться");
        ActionListener actionListenerAuth = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAuthClick();
                loginField.setText("");
                passField.setText("");
            }
        };
        clickAuth.addActionListener(actionListenerAuth);
        inputWindow = new JTextField(20);
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMsg();
            }
        };
        inputWindow.addActionListener(actionListener);
        theMessage = new JButton("Ввести текст");
        ActionListener actionListenerSend = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!inputWindow.getText().trim().isEmpty()) {
                    sendMsg();
                    inputWindow.grabFocus();
                }
            }
        };
        theMessage.setPreferredSize(new Dimension(150, 40));
        theMessage.addActionListener(actionListener);
        JPanel authPanel = new JPanel(new FlowLayout());
        authPanel.add(loginField);
        authPanel.add(passField);
        authPanel.add(clickAuth);
        JPanel messageIntput = new JPanel(new FlowLayout());
        messageIntput.add(inputWindow);
        messageIntput.add(theMessage);
        add(authPanel, BorderLayout.NORTH);
        add(messageWindow, BorderLayout.CENTER);
        add(messageIntput, BorderLayout.SOUTH);
        inputWindow.setCaretPosition(0);
    }

    public void onAuthClick() {
        if (socket == null || socket.isClosed()) {
            startChat("localhost", 8189);
        }
        try {
            out.writeUTF("/s" + loginField.getText() + "/s" + passField.getText());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка авторизации");
        }
    }

    public void sendMsg() {
        try {
            out.writeUTF(inputWindow.getText());
            inputWindow.setText("");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка отправки сообщения");
        }
    }

    public synchronized void startChat(String host, int PORT) {
        try {
            socket = new Socket(host, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            System.out.println("Чат запущен");
        } catch (IOException e) {
            closeChat();
            e.printStackTrace();
        }
    }

    public synchronized void showChat() {
        this.show();

    }

    public void closeChat() {
        try {
            setSession(false);
            setAuthorised(false);
            socket.close();
            setVisible(false);
            dispose();
            myNick = "";
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sessionChat() {
        try {
            while (authorised) {
                String str = in.readUTF();
                if (str.startsWith("/authok ")) {
                    setAuthorised(true);
                    myNick = str.split("/s")[1];
                    System.out.println("авторизовался" + str);
                    break;
                }
                messageArea.append(str + "\n");
            }
            while (session) {
                String str1 = in.readUTF();
                if (str1.equals("end")) {
                    closeChat();
                }
                messageArea.append(str1 + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeChat();
        }
    }


}
