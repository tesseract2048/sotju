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
public class EwebNewsDetailRule extends Rule {

    private final static String CONTENT_PATTERN = "td[width=86%]";

    private final static String AUTHOR_PATTERN = "<td>[\\r\\n\\t ]+信息来自：(?<author>[^<]*)</td>";

    private final static String READ_PATTERN = "<td align=\"right\">[\\r\\n\\t ]+阅读次数:[\\r\\n\\t ]+(?<read>[^<]*)</td>";

    public EwebNewsDetailRule() {
        setId("eweb_news_detail");
        setSiteId("eweb");
        setUrlPattern("http://e\\.tju\\.edu\\.cn/toModule\\.do\\?prefix=/News&page=/newsDetail\\.do\\?infoid=(\\d+)");
        setRefreshRate(86400 * 5);
        setExtractors(Arrays.asList(new Extractor[] {
            new Extractor(PatternType.DOM, CONTENT_PATTERN).function("text",
                    new FunctionInvokeChain().append(
                            FunctionType.STRIP_AND_STORE, "content")),
            new Extractor(PatternType.REGEX, AUTHOR_PATTERN).function("author",
                    new FunctionInvokeChain().append(
                            FunctionType.STRIP_AND_STORE, "author")),
            new Extractor(PatternType.REGEX, READ_PATTERN).function(
                    "read",
                    new FunctionInvokeChain().append(
                            FunctionType.STRIP_AND_STORE, "read").append(
                            FunctionType.INDEX_LATER))
        }));
    }

}
