package com.ghj.mt.message;


import io.netty.channel.socket.SocketChannel;

import java.util.concurrent.*;

public class MessageManager {
    private static MessageManager manager;
    private LinkedBlockingQueue<Message> mMessageQueue = new LinkedBlockingQueue<Message>();
    private ThreadPoolExecutor mPoolExecutor = new ThreadPoolExecutor(5, 10, 15, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadPoolExecutor.AbortPolicy());

    private MessageManager() {
    }

    public static MessageManager getInstance() {
        if (manager == null) {
            synchronized (MessageManager.class) {
                if (manager == null) {
                    manager = new MessageManager();
                }
            }
        }
        return manager;
    }

    public void putMessage(Message message) {
        try {
            mMessageQueue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        while (true) {
            try {
                Message message = mMessageQueue.take();
                mPoolExecutor.execute(new SendMessageTask(message));
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            } catch (RejectedExecutionException e) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                continue;
            }
        }

    }

    public void stop() {
        mMessageQueue.clear();
        mPoolExecutor.shutdownNow();
    }

    class SendMessageTask implements Runnable {
        private Message message;

        public SendMessageTask(Message message) {
            this.message = message;
        }

        public void run() {
            if (message.getReceiveIDs().size() < 2) {
                //发送单聊消息;
                SocketChannel channel = com.ghj.mt.message.UserManager.getInstance().getUserChannel(message.getReceiveIDs().get(0));
                if (channel != null && channel.isActive()) {
                    channel.writeAndFlush(message);
                }
            } else {
                //发送群聊消息;
                CopyOnWriteArrayList<Integer> userList = com.ghj.mt.message.UserManager.getInstance().getUserListInGroup(message.getGroupID());
                for (Integer user : userList) {
                    if (!user.equals(message.getClientID())) {
                        SocketChannel channel = com.ghj.mt.message.UserManager.getInstance().getUserChannel(user);
                        if (channel != null && channel.isActive()) {
                            channel.writeAndFlush(message);
                        }
                    }
                }
            }
        }
    }
}
