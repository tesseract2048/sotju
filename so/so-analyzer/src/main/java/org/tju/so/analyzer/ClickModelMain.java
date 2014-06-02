package org.tju.so.analyzer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.tju.so.model.QuerySession;
import org.tju.so.service.ClickModelService;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Component
public class ClickModelMain {

    private static final Logger LOG = LoggerFactory
            .getLogger(ClickModelMain.class);

    @Autowired
    private ClickModelService clickModelService;

    public void run(String[] args) throws Exception {
        switch (args[0]) {
            case "reset":
                clickModelService.reset();
                LOG.info("DCM counters reseted.");
                break;
            case "update":
                if (args.length < 2) {
                    System.err.println("Usage: ClickModelMain update <date>");
                    return;
                }
                String date = args[1];
                LOG.info("Reading sessions on date " + date + " for update...");
                List<QuerySession> sessions = clickModelService
                        .getSessions(date);
                LOG.info(sessions.size() + " sessions(s) read, updating...");
                clickModelService.update(sessions);
                LOG.info("Done.");
                break;
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Usage: ClickModelMain <command>");
            return;
        }
        ApplicationContext appContext = new ClassPathXmlApplicationContext(
                "classpath:applicationContext-analyzer.xml");
        ((ClassPathXmlApplicationContext) appContext).registerShutdownHook();
        ClickModelMain main = appContext.getBean(ClickModelMain.class);
        main.run(args);
    }
}
