package org.tju.so.crawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.tju.so.model.holder.SchemaHolder;
import org.tju.so.model.holder.SiteHolder;
import org.tju.so.model.schema.def.Article;
import org.tju.so.model.schema.def.Movie;
import org.tju.so.model.schema.def.Torrent;
import org.tju.so.model.site.Site;
import org.tju.so.model.site.def.Eweb;
import org.tju.so.model.site.def.Pt;
import org.tju.so.model.site.def.Seeworld;
import org.tju.so.model.site.def.Tjunews;
import org.tju.so.search.provider.SearchProvider;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Component
public class SchemaImporter {

    @Autowired
    private SearchProvider search;

    @Autowired
    private SchemaHolder schemaHolder;

    @Autowired
    private SiteHolder siteHolder;

    public void run() {
        schemaHolder.put(new Article());
        schemaHolder.put(new Torrent());
        schemaHolder.put(new Movie());
        siteHolder.put(new Eweb());
        siteHolder.put(new Pt());
        siteHolder.put(new Tjunews());
        siteHolder.put(new Seeworld());

        search.initIndices();
        search.updateSite(new Eweb());
        search.updateSite(new Pt());
        search.updateSite(new Tjunews());
        search.updateSite(new Seeworld());
        search.updateSchema(new Site[] {
            new Eweb(), new Tjunews()
        }, new Article());
        search.updateSchema(new Site[] {
            new Pt()
        }, new Torrent());
        search.updateSchema(new Site[] {
            new Seeworld()
        }, new Movie());
    }

    public static void main(String[] args) {
        ApplicationContext appContext = new ClassPathXmlApplicationContext(
                "classpath:applicationContext-crawler.xml");
        ((ClassPathXmlApplicationContext) appContext).registerShutdownHook();
        SchemaImporter importer = appContext.getBean(SchemaImporter.class);
        importer.run();
    }
}
