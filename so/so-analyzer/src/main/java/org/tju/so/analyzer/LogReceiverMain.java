package org.tju.so.analyzer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.tju.so.service.SearchLogReceiver;

/**
 * Bootstrap for log receiver
 * 
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Component
public class LogReceiverMain {

    @Autowired
    private SearchLogReceiver searchLogReceiver;

    public void run() throws Exception {
        searchLogReceiver.run();
    }

    public static void main(String[] args) throws Exception {
        ApplicationContext appContext = new ClassPathXmlApplicationContext(
                "classpath:applicationContext-analyzer.xml");
        ((ClassPathXmlApplicationContext) appContext).registerShutdownHook();
        LogReceiverMain main = appContext.getBean(LogReceiverMain.class);
        main.run();
    }
}
