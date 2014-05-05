package org.tju.so.model.crawler.rule.def;

import java.util.Arrays;

import org.tju.so.model.crawler.rule.Extractor;
import org.tju.so.model.crawler.rule.Extractor.FunctionInvokeChain;
import org.tju.so.model.crawler.rule.Extractor.FunctionType;
import org.tju.so.model.crawler.rule.Extractor.PatternType;
import org.tju.so.model.crawler.rule.Rule;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class TjunewsInfoListRule extends Rule {

    private final static String LIST_TITLE_PATTERN = "<td width=\"421\" height=\"33\" class=\"table_point\">&nbsp;<a href=\"(?<link>[^\"]+)\"([^<]+)>(?<title>[^<]+)</a></td>[\\r\\n\\t ]+<td width=\"100\" class=\"table_point\"><span class=font_12_gray>\\[(?<date>[^\\]]+)\\][\\r\\n\\t ]+</span>[\\r\\n\\t ]+</td>";

    private final static String COLUMN_PATTERN = ".menulink[href=./]";

    private final static String PAGE_PATTERN = "var currentPage = (?<page>\\d+);";

    public TjunewsInfoListRule() {
        setId("tjunews_info_list");
        setSiteId("tjunews");
        setUrlPattern("http://www\\.tju\\.edu\\.cn/newscenter/([_a-zA-Z]+)/index([_0-9]*).htm");
        setRefreshRate(86400);
        setExtractors(Arrays
                .asList(new Extractor[] {
                    new Extractor(PatternType.DOM, COLUMN_PATTERN).function(
                            "text", new FunctionInvokeChain().append(
                                    FunctionType.STRIP_AND_STORE, "column")),
                    new Extractor(PatternType.REGEX, PAGE_PATTERN)
                            .function(
                                    "page",
                                    new FunctionInvokeChain()
                                            .append(FunctionType.SCRIPT,
                                                    "'index_' + (parseInt(ret) + 1) + '.htm'")
                                            .append(FunctionType.ABSOLUTE_URL)
                                            .append(FunctionType.FETCH)),
                    new Extractor(PatternType.REGEX, LIST_TITLE_PATTERN)
                            .function(
                                    Extractor.INVOKE_PREPARE,
                                    new FunctionInvokeChain()
                                            .append(FunctionType.NEW_CONTEXT)
                                            .append(FunctionType.SET_SITE_ID,
                                                    "$siteId")
                                            .append(FunctionType.SET_SCHEMA_ID,
                                                    "article"))
                            .function(
                                    "link",
                                    new FunctionInvokeChain()
                                            .append(FunctionType.ABSOLUTE_URL)
                                            .append(FunctionType.STRIP_AND_STORE,
                                                    "url")
                                            .append(FunctionType.FETCH))
                            .function(
                                    "title",
                                    new FunctionInvokeChain().append(
                                            FunctionType.STRIP_AND_STORE,
                                            "title"))
                            .function(
                                    "date",
                                    new FunctionInvokeChain().append(
                                            FunctionType.FORMAT_DATE,
                                            "yyyy-MM-dd").append(
                                            FunctionType.STRIP_AND_STORE,
                                            "date"))
                }));
    }

}
