package org.tju.so.crawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.tju.so.crawler.service.Worker;
import org.tju.so.model.notification.Notification;
import org.tju.so.model.notification.Notification.Receiver;
import org.tju.so.model.notification.Notification.Topic;
import org.tju.so.service.NotifyService;
import org.tju.so.service.NotifyService.NotificationListener;

/**
 * Bootstrap for crawler worker
 * 
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Component
public class WorkerMain {

    @Autowired
    private Worker worker;

    @Autowired
    private NotifyService notifyService;

    public void run() throws Exception {
        notifyService.addNotificationListener(new NotificationListener() {

            @Override
            public void onNotification(Notification notification) {
                if (!notification.isFeedingReceiver(Receiver.CRAWLER_WORKER))
                    return;
                if (notification.getTopic() != Topic.RELOAD)
                    return;
                worker.reload();
            }

        });
        worker.run();
    }

    public static void main(String[] args) throws Exception {
        ApplicationContext appContext = new ClassPathXmlApplicationContext(
                "classpath:applicationContext-crawler.xml");
        ((ClassPathXmlApplicationContext) appContext).registerShutdownHook();
        WorkerMain main = appContext.getBean(WorkerMain.class);
        main.run();
    }
}
