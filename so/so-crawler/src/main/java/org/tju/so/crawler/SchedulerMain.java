package org.tju.so.crawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.tju.so.crawler.service.Scheduler;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Component
public class SchedulerMain {

    @Autowired
    private Scheduler scheduler;

    public void run() throws Exception {
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
