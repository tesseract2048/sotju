package org.tju.so.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tju.so.model.notification.Notification;
import org.tju.so.model.notification.Notification.Receiver;
import org.tju.so.model.notification.Notification.Topic;
import org.tju.so.service.JedisService.JedisTransaction;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import com.google.gson.Gson;

/**
 * Service to receive, process or create cluster notification via redis
 * 
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Service
public class NotifyService {

    private static final Logger LOG = LoggerFactory
            .getLogger(NotifyService.class);

    public static interface NotificationListener {
        void onNotification(Notification notification);
    }

    @Autowired
    private JedisService jedisService;

    @Resource
    private String notificationChannel;

    private List<NotificationListener> listeners;

    private volatile boolean isListening;

    @PostConstruct
    public void init() {
        listeners = new ArrayList<NotificationListener>();
        isListening = false;
    }

    /**
     * Create cluster notification and send it
     * 
     * @param receiver
     * @param topic
     * @param argument
     * @throws Exception
     */
    public void createNotification(Receiver receiver, Topic topic,
            Object argument) throws Exception {
        Notification notification = new Notification(receiver, topic, argument);
        final String message = new Gson().toJson(notification);
        long notified = jedisService.commit(new JedisTransaction<Long>() {

            @Override
            public Long execute(Jedis jedis) throws Exception {
                return jedis.publish(notificationChannel, message);
            }

        });
        LOG.info("Notified " + notified + " peer(s).");
    }

    /**
     * Setup listening for cluster notifications. When notification arrived, all
     * listeners will be invoked in another thread immediately.
     * 
     * @throws Exception
     */
    public synchronized void setupListening() throws Exception {
        if (isListening)
            return;
        new Thread() {
            @Override
            public void run() {
                try {
                    jedisService.commit(new JedisTransaction<Boolean>() {

                        @Override
                        public Boolean execute(Jedis jedis) throws Exception {
                            jedis.subscribe(new JedisPubSub() {

                                @Override
                                public void onMessage(String channel,
                                        String message) {
                                    if (!channel.equals(notificationChannel)) {
                                        LOG.warn("Received subscribed message with wrong channel: "
                                                + channel);
                                        return;
                                    }
                                    Notification notification = new Gson()
                                            .fromJson(message,
                                                    Notification.class);
                                    LOG.debug("Notification received: "
                                            + notification);
                                    for (NotificationListener listener: listeners) {
                                        listener.onNotification(notification);
                                    }
                                }

                                @Override
                                public void onPMessage(String pattern,
                                        String channel, String message) {}

                                @Override
                                public void onSubscribe(String channel,
                                        int subscribedChannels) {}

                                @Override
                                public void onUnsubscribe(String channel,
                                        int subscribedChannels) {}

                                @Override
                                public void onPUnsubscribe(String pattern,
                                        int subscribedChannels) {}

                                @Override
                                public void onPSubscribe(String pattern,
                                        int subscribedChannels) {}

                            }, notificationChannel);
                            return true;
                        }

                    }, false);
                } catch (Exception e) {
                    LOG.error("Failed to setup listening", e);
                    isListening = false;
                }
            }
        }.start();
        isListening = true;
        LOG.info("Listening on channel " + notificationChannel + ".");
    }

    /**
     * Attach cluster notification listeners
     * 
     * @param listener
     * @throws Exception
     */
    public void addNotificationListener(NotificationListener listener)
            throws Exception {
        if (!isListening) {
            setupListening();
        }
        listeners.add(listener);
        LOG.info("New listener added.");
    }
}
