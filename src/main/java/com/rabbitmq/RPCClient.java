package com.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class RPCClient implements AutoCloseable {

    private Connection connection;
    private Channel channel;
    private String requestQueueName = "rpc_queue";

    public RPCClient() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();
    }

    public static void main(String[] argv) {
        try (RPCClient fibonacciRpc = new RPCClient()) {
            for (int i = 0; i < 32; i++) {
                String i_str = Integer.toString(i);
                System.out.println(" [x] Requesting fib(" + i_str + ")");
//                String response = fibonacciRpc.call(i_str);
                fibonacciRpc.call(i_str);
                System.out.println("Called rpc for " + i_str);
//                System.out.println(" [.] Got '" + response + "'");
            }
        } catch (IOException | TimeoutException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void call(String message) throws IOException, InterruptedException {
        final String corrId = UUID.randomUUID().toString();

        channel.queueDeclare(requestQueueName , true,false,false,null);
        String replyQueueName = channel.queueDeclare().getQueue();
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .deliveryMode(2)
                .build();

        channel.basicPublish("", requestQueueName, props,message.getBytes("UTF-8"));
        System.out.println("request published ");
        // Donot consume immeditately -> this makes gRPC async
//        final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);
//
//        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
//            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
//                response.offer(new String(delivery.getBody(), "UTF-8"));
//            }
//        }, consumerTag -> {
//        });

//        String result = response.take();
//        channel.basicCancel(ctag);
////        return result;
    }

    public void close() throws IOException {
        connection.close();
    }
}