package com.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

public class Sender {
    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("localhost");

        //always open a connection like this in try
        boolean producer = false;
        if(producer) {
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
        else {
            //always open a connection like this in try
            Connection connection = factory.newConnection();
            Channel channel =  connection.createChannel() ;
            //queue created of name "hello-world"
            channel.queueDeclare("hello-world" , true,false,false,null);
            //receive
            channel.basicConsume("hello-world", true, (consumeTag, message) -> {
                String receivedMsg = new String(message.getBody() , "UTF-8");
                System.out.println("received message " + receivedMsg);
            }, consumerTag -> {

            });
        }


    }
}
