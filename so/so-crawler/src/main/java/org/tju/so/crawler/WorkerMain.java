package org.tju.so.crawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.tju.so.crawler.service.Worker;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Component
public class WorkerMain {

    @Autowired
    private Worker worker;

    public void run() throws Exception {
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
