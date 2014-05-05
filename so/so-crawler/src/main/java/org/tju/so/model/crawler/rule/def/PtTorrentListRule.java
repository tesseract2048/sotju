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
public class PtTorrentListRule extends Rule {

    private final static String DETAIL_PATTERN = ".torrentname a[href*=details]";

    private final static String PAGE_PATTERN = "a:contains(下一页):nth-child(2)";

    public PtTorrentListRule() {
        setId("pt_torrent_list");
        setSiteId("pt");
        setUrlPattern("http://pt\\.tju\\.edu\\.cn/torrents\\.php(\\?.+)*");
        setRefreshRate(7200);
        setExtractors(Arrays
                .asList(new Extractor[] {
                    new Extractor(PatternType.DOM, PAGE_PATTERN).function(
                            "href",
                            new FunctionInvokeChain().append(
                                    FunctionType.ABSOLUTE_URL).append(
                                    FunctionType.FETCH)),
                    new Extractor(PatternType.DOM, DETAIL_PATTERN)
                            .function(
                                    Extractor.INVOKE_PREPARE,
                                    new FunctionInvokeChain()
                                            .append(FunctionType.NEW_CONTEXT)
                                            .append(FunctionType.SET_SITE_ID,
                                                    "$siteId")
                                            .append(FunctionType.SET_SCHEMA_ID,
                                                    "torrent"))
                            .function(
                                    "href",
                                    new FunctionInvokeChain()
                                            .append(FunctionType.ABSOLUTE_URL)
                                            .append(FunctionType.STRIP_AND_STORE,
                                                    "url")
                                            .append(FunctionType.FETCH))
                }));
        getHeaders()
                .put("Cookie",
                        "c_secure_uid=Mjk2NTQ%3D; c_secure_ssl=bm9wZQ%3D%3D; c_secure_tracker_ssl=bm9wZQ%3D%3D; c_secure_login=bm9wZQ%3D%3D; c_secure_pass=23f23999c38c03e6ef055eb14ea5dcd9;");
    }

}
