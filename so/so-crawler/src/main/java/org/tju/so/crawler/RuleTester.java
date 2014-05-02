package org.tju.so.crawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.tju.so.crawler.service.TaskExecutor;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Component
public class RuleTester {

    @Autowired
    private TaskExecutor taskExecutor;

    public void run() {
        
    }

    public static void main(String[] args) {
        ApplicationContext appContext = new ClassPathXmlApplicationContext(
                "classpath:applicationContext-crawler.xml");
        ((ClassPathXmlApplicationContext) appContext).registerShutdownHook();
        RuleTester importer = appContext.getBean(RuleTester.class);
        importer.run();
    }
}
