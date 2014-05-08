package org.tju.so.service;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.tju.so.model.holder.SchemaHolder;
import org.tju.so.model.holder.SiteHolder;
import org.tju.so.model.notification.Notification;
import org.tju.so.model.notification.Notification.Receiver;
import org.tju.so.model.notification.Notification.Topic;
import org.tju.so.service.NotifyService.NotificationListener;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Service
@Lazy(false)
public class DispatcherService {

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private SchemaHolder schemaHolder;

    @Autowired
    private SiteHolder siteHolder;

    @PostConstruct
    public void init() throws Exception {
        notifyService.addNotificationListener(new NotificationListener() {

            @Override
            public void onNotification(Notification notification) {
                if (!notification.isFeedingReceiver(Receiver.DISPATCHER))
                    return;
                if (notification.getTopic() != Topic.RELOAD)
                    return;
                schemaHolder.flush();
                siteHolder.flush();
            }

        });
    }
}
