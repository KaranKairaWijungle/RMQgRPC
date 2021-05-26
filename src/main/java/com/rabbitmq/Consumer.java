package com.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

public class Consumer {
    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("localhost");

        boolean consumer = false;
        if(consumer) {
            //always open a connection like this in try
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            //queue created of name "hello-world"
            channel.queueDeclare("hello-world", true, false, false, null);
            //receive
            channel.basicConsume("hello-world", true, (consumeTag, message) -> {
                String receivedMsg = new String(message.getBody(), "UTF-8");
                System.out.println("received message " + receivedMsg);
            }, consumerTag -> {

            });
        }else {
            try (Connection connection = factory.newConnection()) {
                Channel channel = connection.createChannel();
                //queue created of name "hello-world"
                channel.queueDeclare("hello-world", true, false, false, null);
                String message = "Message from the Sender " + LocalDateTime.now();

                //send
                channel.basicPublish("", "hello-world", false, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
                System.out.println("message sent");
            }
        }



    }
}
