package org.tju.so.crawler.service;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tju.so.crawler.fetcher.Fetcher;
import org.tju.so.crawler.fetcher.HttpFetcher;
import org.tju.so.crawler.parser.Parser;
import org.tju.so.crawler.parser.ReadableContentParser;
import org.tju.so.model.crawler.data.Context;
import org.tju.so.model.crawler.data.Task;
import org.tju.so.model.crawler.holder.RuleHolder;
import org.tju.so.model.crawler.rule.Extractor;
import org.tju.so.model.crawler.rule.Extractor.FunctionInvoke;
import org.tju.so.model.crawler.rule.Extractor.FunctionInvokeChain;
import org.tju.so.model.crawler.rule.Extractor.FunctionType;
import org.tju.so.model.crawler.rule.Rule;
import org.tju.so.model.entity.Entity;
import org.tju.so.model.holder.SchemaHolder;
import org.tju.so.model.holder.SiteHolder;
import org.tju.so.search.provider.SearchProvider;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Component
public class TaskExecutor {

    private static final Logger LOG = LoggerFactory
            .getLogger(TaskExecutor.class);

    @Autowired
    private Storage storage;

    @Autowired
    private RuleHolder ruleHolder;

    @Autowired
    private SchemaHolder schemaHolder;

    @Autowired
    private SiteHolder siteHolder;

    @Autowired
    private SearchProvider searchProvider;

    private Map<String, Rule> rules;

    @PostConstruct
    public void init() {
        rules = new HashMap<String, Rule>();
        for (Rule rule: ruleHolder.getAll()) {
            String pattern = rule.getUrlPattern();
            rules.put(pattern, rule);
            LOG.info("Rule loaded for pattern " + pattern);
        }
    }

    public void reload() {
        ruleHolder.flush();
        schemaHolder.flush();
        siteHolder.flush();
        init();
    }

    private Fetcher getFetcher(String url) {
        Fetcher fetcher = new HttpFetcher();
        fetcher.init(url);
        return fetcher;
    }

    private Parser getParser(String mimeType, byte[] data) {
        Parser parser = new ReadableContentParser();
        parser.init(mimeType, data);
        return parser;
    }

    private Rule getRule(String url) {
        for (Map.Entry<String, Rule> entry: rules.entrySet()) {
            if (url.matches(entry.getKey()))
                return entry.getValue();
        }
        return null;
    }

    private boolean checkRefresh(String url, int refreshRate) throws Exception {
        long lastRefreshTime = storage.getRefresh(url);
        long currentTime = storage.currentTime();
        if (currentTime - lastRefreshTime < refreshRate)
            return false;
        storage.putRefresh(url, currentTime);
        return true;
    }

    private String formatArgs(String args, Rule rule, Context context,
            String baseUrl, String groupName, Object value, String ret) {
        args = args.replace("$name", "" + groupName);
        args = args.replace("$val", "" + value);

        args = args.replace("$ruleId", "" + rule.getId());
        args = args.replace("$siteId", "" + rule.getSiteId());

        args = args.replace("$c_id", "" + context.getId());
        args = args.replace("$c_contextId", "" + context.getContextId());
        args = args.replace("$c_schemaId", "" + context.getSchemaId());
        args = args.replace("$c_siteId", "" + context.getSiteId());

        args = args.replace("$url", "" + baseUrl);
        args = args.replace("$ret", "" + ret);

        return args;
    }

    private Entity makeEntity(Context context) throws Exception {
        Entity entity = new Entity();
        entity.setSite(siteHolder.get(context.getSiteId()));
        entity.setSchema(schemaHolder.get(context.getSchemaId()));
        entity.setId(context.getId());
        entity.setFieldValues(context.getParsedValues());

        if (entity.getSite() == null)
            throw new Exception("No site found");
        if (entity.getSchema() == null)
            throw new Exception("No schema found");
        if (entity.getId() == null)
            throw new Exception("No id available");
        return entity;
    }

    private void finishContext(Context context, boolean isDeleteing)
            throws Exception {
        LOG.info("Finishing context " + context.getContextId() + ", deleteing="
                + isDeleteing + "...");
        Entity entity = makeEntity(context);
        if (isDeleteing) {
            searchProvider.delete(entity);
        } else {
            searchProvider.index(entity);
        }
        context.setFinished(true);
    }

    private static String formatDate(String formatString, String input) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(formatString);
        return String.valueOf(fmt.parseDateTime(input.trim()).getMillis());
    }

    private Context invokeExtractor(Task task, Rule rule, Context context,
            FunctionInvokeChain chain, String baseUrl, String groupName,
            Object value) throws Exception {
        String ret = null;
        LOG.info("Invoking extractor for group " + groupName + ", value is "
                + value);
        for (FunctionInvoke invoke: chain.getFunctions()) {
            FunctionType type = invoke.getType();
            String op1 = invoke.getOp1();
            String op2 = invoke.getOp2();
            if (op1 != null)
                op1 = formatArgs(op1, rule, context, baseUrl, groupName, value,
                        ret);
            if (op2 != null)
                op2 = formatArgs(op2, rule, context, baseUrl, groupName, value,
                        ret);
            LOG.debug("Function invoke: " + type + ", " + op1 + ", " + op2);
            switch (type) {
                case NEW_CONTEXT:
                    context = new Context(context);
                    break;
                case SET_SCHEMA_ID:
                    context.setSchemaId(op1);
                    break;
                case SET_SITE_ID:
                    context.setSiteId(op1);
                    break;
                case SET_ID:
                    context.setId(op1);
                    break;
                case ABSOLUTE_URL:
                    if (op2 == null)
                        ret = new URL(new URL(baseUrl), op1).toString();
                    else
                        ret = new URL(new URL(op1), op2).toString();
                    break;
                case FETCH:
                    if (op1 == null)
                        op1 = "" + value;
                    context.getNewTasks().add(
                            new Task(context.getContextId(), op1, task
                                    .getPriority()));
                    break;
                case STRIP_AND_STORE:
                    if (op2 == null)
                        op2 = "" + value;
                    op2 = op2.trim();
                case STORE:
                    if (op2 == null)
                        op2 = "" + value;
                    context.getParsedValues().put(op1, op2);
                    break;
                case INDEX:
                    finishContext(context, false);
                    break;
                case DELETE:
                    finishContext(context, true);
                    break;
                case INDEX_LATER:
                    context.setIndexing(true);
                    break;
                case DELETE_LATER:
                    context.setDeleteing(true);
                    break;
                case FORMAT_DATE:
                    if (op2 == null)
                        op2 = "" + value;
                    ret = formatDate(op1, op2);
                    break;
                case STRIP:
                    if (op1 == null)
                        op1 = "" + value;
                    ret = op1.trim();
                    break;
                case FINISH:
                    context.setFinished(true);
                    break;
                case HTML_TO_TEXT:
                    if (op1 == null)
                        op1 = "" + value;
                    ret = Jsoup.parseBodyFragment(op1).text();
                    break;
            }
        }
        return context;
    }

    private Set<Context> invokeExtractor(Task task, Rule rule, Context context,
            Extractor extractor, String baseUrl, Map<String, Object> groups)
            throws Exception {
        Set<Context> relatedContexts = new HashSet<Context>();
        Map<String, FunctionInvokeChain> invokes = extractor.getInvokes();
        if (invokes.containsKey(Extractor.INVOKE_PREPARE)) {
            context = invokeExtractor(task, rule, context,
                    invokes.get(Extractor.INVOKE_PREPARE), baseUrl,
                    Extractor.INVOKE_PREPARE, null);
            relatedContexts.add(context);
        }
        for (Map.Entry<String, Object> entry: groups.entrySet()) {
            if (invokes.containsKey(entry.getKey())) {
                context = invokeExtractor(task, rule, context,
                        invokes.get(entry.getKey()), baseUrl, entry.getKey(),
                        entry.getValue());
                relatedContexts.add(context);
            }
        }
        if (invokes.containsKey(Extractor.INVOKE_FINISH)) {
            context = invokeExtractor(task, rule, context,
                    invokes.get(Extractor.INVOKE_FINISH), baseUrl,
                    Extractor.INVOKE_FINISH, null);
            relatedContexts.add(context);
        }
        return relatedContexts;
    }

    public void executeTask(Task task) throws Exception {
        /* get rule */
        String url = task.getUrl();
        Rule rule = getRule(url);
        if (rule == null)
            throw new Exception("No rule found for: " + url);
        if (!checkRefresh(url, rule.getRefreshRate())) {
            LOG.info("Refresh limit exceeded: " + url);
            return;
        }

        /* prepare context */
        String contextId = task.getContextId();
        LOG.info("Loading context " + contextId + "...");
        Context context = storage.getContext(contextId);
        if (context == null)
            throw new Exception("Context not found: " + contextId);

        /* fetch remote data */
        LOG.info("Fetching " + url + "...");
        Fetcher fetcher = getFetcher(url);
        fetcher.fetch(rule.getHeaders());

        /* parse data */
        LOG.info("Parsing " + fetcher.getMimeType());
        Set<Context> relatedContexts = new HashSet<Context>();
        relatedContexts.add(context);
        Parser parser = getParser(fetcher.getMimeType(), fetcher.getData());
        LOG.info("Invoking " + rule.getExtractors().size() + " extractor(s)...");
        for (Extractor extractor: rule.getExtractors()) {
            List<Map<String, Object>> mchs = parser.extract(extractor);
            LOG.info(mchs.size() + " matches found for extractor: "
                    + extractor.getType() + ", " + extractor.getPattern());
            for (Map<String, Object> groups: mchs) {
                relatedContexts.addAll(invokeExtractor(task, rule, context,
                        extractor, url, groups));
            }
        }

        /* save contexts */
        LOG.info("Saving " + relatedContexts.size() + " related contexts...");
        for (Context rContext: relatedContexts) {
            LOG.info("Post-processing context " + rContext.getContextId()
                    + "...");
            storage.putContext(rContext);
            if (rContext.isIndexing()) {
                finishContext(rContext, false);
            }
            if (rContext.isDeleteing()) {
                finishContext(rContext, true);
            }
            for (Task newTask: rContext.getNewTasks()) {
                storage.pushTask(newTask);
            }
            if (rContext.isFinished())
                storage.removeContext(rContext.getContextId());
        }

    }
}
