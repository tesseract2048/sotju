package org.tju.so.crawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.tju.so.model.schema.def.Article;
import org.tju.so.model.site.Site;
import org.tju.so.model.site.def.Eweb;
import org.tju.so.search.provider.SearchProvider;

@Component
public class CrawlerMain {

    @Autowired
    private SearchProvider search;

    public void run() {
        search.updateSite(new Eweb());
        search.updateSchema(new Site[] {
            new Eweb()
        }, new Article());
    }

    public static void main(String[] args) {
        ApplicationContext appContext = new ClassPathXmlApplicationContext(
                "classpath:applicationContext-crawler.xml");
        ((ClassPathXmlApplicationContext) appContext).registerShutdownHook();
        CrawlerMain main = appContext.getBean(CrawlerMain.class);
        main.run();
    }
}
