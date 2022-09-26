package com.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;


@ServerEndpoint("/webSocket/{username}")
@Slf4j
@Component
public class WebSocket {
    //concurrent包的线程安全Set，用来存放每个客户端对应的WebSocket对象。
    private static final CopyOnWriteArraySet<WebSocket> webSocketSet = new CopyOnWriteArraySet<>();
    private static int onlineNum;
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    private String username;

    /**
     * 建立连接成功
     *
     * @param session
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        onlineNum++;
        this.session = session;
        this.username = username;
        webSocketSet.add(this);
        log.info("【websocket消息】 有新的连接，总数{}", webSocketSet.size());
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        onlineNum--;
        this.session = session;
        this.username = username;
        webSocketSet.remove(this);
        log.info("【websocket消息】 连接断开，总数{}", webSocketSet.size());
    }

    /**
     * 接收客户端消息
     *
     * @param message
     */
    @OnMessage
    public void onMessage(String message) {
        log.info("【websocket消息】 收到客户端发来的消息：{}", message);
    }

    /**
     * 发送消息
     *
     * @param message
     */
    public void sendMessage(String message, String username) {
        log.info("【websocket消息】 发送消息：{}", message);
        for (WebSocket webSocket : webSocketSet) {
            try {
                System.out.println("收消息：" + webSocket.getUsername() + "==" + username);
                if (webSocket.getUsername().equals(username)) {
                    System.out.println("收消息：" + webSocket.getUsername() + "==" + username);
                    webSocket.session.getBasicRemote().sendText(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getUsername() {
        return this.username;
    }
}



