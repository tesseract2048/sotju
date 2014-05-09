package org.tju.so.crawler;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.tju.so.crawler.service.Scheduler;
import org.tju.so.model.crawler.TaskPriority;
import org.tju.so.model.notification.Notification;
import org.tju.so.model.notification.Notification.Receiver;
import org.tju.so.service.NotifyService;
import org.tju.so.service.NotifyService.NotificationListener;

/**
 * Bootstrap for crawler scheduler
 * 
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Component
public class SchedulerMain {

    private static final Logger LOG = LoggerFactory
            .getLogger(SchedulerMain.class);

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private NotifyService notifyService;

    public void run() throws Exception {
        notifyService.addNotificationListener(new NotificationListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void onNotification(Notification notification) {
                if (!notification.isFeedingReceiver(Receiver.CRAWLER_SCHEDULER))
                    return;
                switch (notification.getTopic()) {
                    case RELOAD:
                        scheduler.reload();
                        break;
                    case PUSH:
                        Map<String, Object> target = (Map<String, Object>) notification
                                .getArgument();
                        String url = (String) target.get("url");
                        Map<String, Object> params = (Map<String, Object>) target
                                .get("params");
                        try {
                            scheduler.scheduleNewTask(url, params,
                                    TaskPriority.HIGHER);
                        } catch (Exception e) {
                            LOG.error("Cannot schedule task: " + url, e);
                        }
                        break;
                    default:
                        break;
                }
            }

        });
        scheduler.run();
    }

    public static void main(String[] args) throws Exception {
        ApplicationContext appContext = new ClassPathXmlApplicationContext(
                "classpath:applicationContext-crawler.xml");
        ((ClassPathXmlApplicationContext) appContext).registerShutdownHook();
        SchedulerMain main = appContext.getBean(SchedulerMain.class);
        main.run();
    }
}
