package org.tju.so.crawler.service;

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tju.so.crawler.fetcher.Fetcher;
import org.tju.so.crawler.fetcher.HttpFetcher;
import org.tju.so.crawler.parser.BitTorrentParser;
import org.tju.so.crawler.parser.Parser;
import org.tju.so.crawler.parser.ReadableContentParser;
import org.tju.so.model.crawler.data.Context;
import org.tju.so.model.crawler.data.Task;
import org.tju.so.model.crawler.rule.Extractor;
import org.tju.so.model.crawler.rule.Extractor.FunctionInvoke;
import org.tju.so.model.crawler.rule.Extractor.FunctionInvokeChain;
import org.tju.so.model.crawler.rule.Extractor.FunctionType;
import org.tju.so.model.crawler.rule.Rule;
import org.tju.so.model.entity.Entity;
import org.tju.so.model.holder.SchemaHolder;
import org.tju.so.model.holder.SiteHolder;
import org.tju.so.model.schema.Field;
import org.tju.so.model.schema.FieldType;
import org.tju.so.search.provider.SearchProvider;

import com.google.gson.Gson;

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
    private SchemaHolder schemaHolder;

    @Autowired
    private SiteHolder siteHolder;

    @Autowired
    private SearchProvider searchProvider;

    private boolean isDryRun;

    /**
     * @return the isDryRun
     */
    public boolean isDryRun() {
        return isDryRun;
    }

    /**
     * @param isDryRun
     *            the isDryRun to set
     */
    public void setDryRun(boolean isDryRun) {
        this.isDryRun = isDryRun;
    }

    public void reload() {
        schemaHolder.flush();
        siteHolder.flush();
    }

    private Fetcher getFetcher(String url) {
        Fetcher fetcher = new HttpFetcher();
        fetcher.init(url);
        return fetcher;
    }

    private Parser getParser(String mimeType, byte[] data) {
        Parser parser = null;
        String pureMimeType = mimeType;
        if (mimeType.contains(";"))
            pureMimeType = mimeType.substring(0, mimeType.indexOf(";"));
        switch (pureMimeType) {
            case "text/html":
            case "text/xml":
            case "text/plain":
            case "application/xml":
            case "application/json":
                parser = new ReadableContentParser();
                break;
            case "application/x-bittorrent":
                parser = new BitTorrentParser();
                break;
        }
        if (parser != null)
            parser.init(mimeType, data);
        return parser;
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
        for (Map.Entry<String, Object> entry: context.getParsedValues()
                .entrySet()) {
            Object value = entry.getValue();
            Field field = entity.getSchema().getField(entry.getKey());
            if (field == null) {
                throw new Exception("No defination for field " + entry.getKey());
            }
            if (field.getType() == FieldType.OBJECT) {
                value = new Gson().fromJson(value.toString(), Map.class);
            }
            if (field.getType() == FieldType.ARRAY) {
                value = new Gson().fromJson(value.toString(), List.class);
            }
            entity.getFieldValues().put(entry.getKey(), value);
        }

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

    private String mergeUrls(String baseUrl, String href) throws Exception {
        String base = baseUrl;
        if (base.contains("?"))
            base = base.substring(0, base.indexOf("?"));
        if (base.contains("#"))
            base = base.substring(0, base.indexOf("#"));
        if (href.startsWith("?") || href.startsWith("#"))
            return new URL(base + href).toString();
        else
            return new URL(new URL(base), href).toString();
    }

    private Context invokeExtractor(Task task, Rule rule, Context context,
            FunctionInvokeChain chain, String baseUrl, String groupName,
            Object value) throws Exception {
        if (value instanceof Collection || value instanceof Map) {
            value = new Gson().toJson(value);
        }
        String ret = "" + value;
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
                    if (op1 == null)
                        op1 = ret;
                    context.setSchemaId(op1);
                    LOG.info("Context schemaId set to " + op1);
                    break;
                case SET_SITE_ID:
                    if (op1 == null)
                        op1 = ret;
                    context.setSiteId(op1);
                    LOG.info("Context siteId set to " + op1);
                    break;
                case SET_ID:
                    if (op1 == null)
                        op1 = ret;
                    context.setId(op1);
                    LOG.info("Context id set to " + op1);
                    break;
                case ABSOLUTE_URL:
                    if (op1 == null && op2 == null) {
                        op1 = baseUrl;
                        op2 = ret;
                    }
                    ret = mergeUrls(op1, op2);
                    break;
                case FETCH:
                    if (op1 == null)
                        op1 = ret;
                    LOG.info("Fetch later: " + op1);
                    context.getNewTasks().add(
                            new Task(context.getContextId(), op1, task
                                    .getPriority()));
                    break;
                case STRIP_AND_STORE:
                    if (op2 == null)
                        op2 = ret;
                    op2 = op2.trim();
                case STORE:
                    if (op2 == null)
                        op2 = ret;
                    LOG.info("Store [" + op1 + "]: " + op2);
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
                        op2 = ret;
                    ret = formatDate(op1, op2);
                    break;
                case STRIP:
                    if (op1 == null)
                        op1 = ret;
                    ret = op1.trim();
                    break;
                case FINISH:
                    context.setFinished(true);
                    break;
                case HTML_TO_TEXT:
                    if (op1 == null)
                        op1 = ret;
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

    public void executeTask(Rule rule, Task task) throws Exception {
        /* get rule */
        String url = task.getUrl();

        /* prepare context */
        String contextId = task.getContextId();
        LOG.info("Loading context " + contextId + "...");
        Context context;
        if (isDryRun)
            context = new Context();
        else
            context = storage.getContext(contextId);
        if (context == null)
            throw new Exception("Context not found: " + contextId);

        Set<Context> relatedContexts = new HashSet<Context>();
        relatedContexts.add(context);

        try {
            /* fetch remote data */
            Fetcher fetcher = getFetcher(url);
            if (fetcher == null) {
                throw new Exception("No fetcher found for " + url);
            }
            LOG.info("Fetching " + url + "...");
            fetcher.fetch(rule.getHeaders());

            /* parse data */
            Parser parser = getParser(fetcher.getMimeType(), fetcher.getData());
            if (parser == null) {
                throw new Exception("No parser found for "
                        + fetcher.getMimeType());
            }
            LOG.info("Parsing " + fetcher.getMimeType());

            /* extract values */
            LOG.info("Invoking " + rule.getExtractors().size()
                    + " extractor(s)...");
            for (Extractor extractor: rule.getExtractors()) {
                List<Map<String, Object>> mchs = parser.extract(extractor);
                if (mchs == null) {
                    LOG.warn("No match found for extractor: "
                            + extractor.getType() + ", "
                            + extractor.getPattern());
                    continue;
                }
                LOG.info(mchs.size() + " matches found for extractor: "
                        + extractor.getType() + ", " + extractor.getPattern());
                for (Map<String, Object> groups: mchs) {
                    relatedContexts.addAll(invokeExtractor(task, rule, context,
                            extractor, url, groups));
                }
            }
        } catch (Exception e) {
            /* mark as finished so that current context would be removed later */
            context.setFinished(true);
        } finally {
            /* save contexts */
            if (!isDryRun) {
                LOG.info("Saving " + relatedContexts.size()
                        + " related contexts...");
                for (Context rContext: relatedContexts) {
                    LOG.info("Post-processing context "
                            + rContext.getContextId() + "...");
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

    }
}
