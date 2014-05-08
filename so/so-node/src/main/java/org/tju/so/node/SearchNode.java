package org.tju.so.node;

import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.tju.so.model.notification.Notification;
import org.tju.so.model.notification.Notification.Receiver;
import org.tju.so.service.NotifyService;
import org.tju.so.service.NotifyService.NotificationListener;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Component
public class SearchNode {

    private static final Logger LOG = LoggerFactory.getLogger(SearchNode.class);

    @Autowired
    private NotifyService notifyService;

    private volatile boolean isRunning = true;

    public void run() throws Exception {
        notifyService.addNotificationListener(new NotificationListener() {

            @Override
            public void onNotification(Notification notification) {
                if (!notification.isFeedingReceiver(Receiver.SEARCH_NODE))
                    return;
                LOG.warn("Notification received but no handler was implemented");
            }

        });
        Node node = NodeBuilder.nodeBuilder().client(false).node();
        node.start();
        while (isRunning)
            Thread.sleep(1000);
        node.close();
    }

    public static void main(String[] args) throws Exception {
        ApplicationContext appContext = new ClassPathXmlApplicationContext(
                "classpath:applicationContext-node.xml");
        ((ClassPathXmlApplicationContext) appContext).registerShutdownHook();
        SearchNode searchNode = appContext.getBean(SearchNode.class);
        searchNode.run();
    }
}
