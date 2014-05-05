package org.tju.so.crawler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.tju.so.crawler.service.TaskExecutor;
import org.tju.so.model.crawler.TaskPriority;
import org.tju.so.model.crawler.data.Context;
import org.tju.so.model.crawler.data.Task;
import org.tju.so.model.crawler.holder.RuleHolder;
import org.tju.so.model.crawler.rule.Rule;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Component
public class RuleTester {

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private RuleHolder ruleHolder;

    public void run(Rule rule, String url, String postData) throws Exception {
        if (!url.matches(rule.getUrlPattern()))
            throw new Exception("Url does not match rule pattern");
        Context ctx = new Context();
        Task task = new Task(ctx.getContextId(), url, TaskPriority.NORMAL);
        if (postData != null) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("postData", postData);
            task.setParams(params);
        }
        taskExecutor.setDryRun(true);
        taskExecutor.executeTask(rule, task);
    }

    public Rule getRule(String name) throws Exception {
        if (name.contains("_")) {
            return ruleHolder.get(name);
        } else {
            return (Rule) Class.forName(
                    "org.tju.so.model.crawler.rule.def." + name).newInstance();
        }
    }

    public static void main(String[] args) throws Exception {
        ApplicationContext appContext = new ClassPathXmlApplicationContext(
                "classpath:applicationContext-crawler.xml");
        ((ClassPathXmlApplicationContext) appContext).registerShutdownHook();
        RuleTester importer = appContext.getBean(RuleTester.class);

        String ruleName = args[0];
        String url = args[1];
        String postData = args[2];
        ruleName = "PtTorrentDownloadRule";
        url = "http://pt.tju.edu.cn/download.php?id=104368";
        postData = null;
        importer.run(importer.getRule(ruleName), url, postData);
    }
}
