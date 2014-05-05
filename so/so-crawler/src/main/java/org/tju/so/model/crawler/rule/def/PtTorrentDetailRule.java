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
public class PtTorrentDetailRule extends Rule {

    private final static String TITLE_PATTERN = ".pagetitle";

    private final static String ID_PATTERN = "bookmark\\((?<id>\\d+),0\\);";

    private final static String SIZE_PATTERN = "<b><b>大小：</b></b>(?<size>[^&]+)";

    private final static String INFO_PATTERN = "<td class=\"rowhead nowrap\" valign=\"top\" align=\"right\">详细信息</td><td class=\"rowfollow\" valign=\"top\" align=\"left\">(?<info>.+?)</td>";

    private final static String DESCRIPTION_PATTERN = "#kdescr";

    private final static String VIEW_PATTERN = "<td class=\"no_border_wide\"><b>查看: </b>(?<view>\\d+)</td>";

    private final static String CLICK_PATTERN = "<td class=\"no_border_wide\"><b>点击: </b>(?<click>\\d+)</td>";

    private final static String DOWNLOAD_PATTERN = "<b>完成:</b> </b><a href=\"viewsnatches.php\\?id=(\\d+)\"><b>(?<download>\\d+)</b>次</a>";

    private final static String SEEDER_LEECHER_PATTERN = "<div id=\"peercount\"><b>(?<seeder>\\d+)个做种者</b> \\| <b>(?<leecher>\\d+)个下载者</b>";

    private final static String TORRENT_NAME_PATTERN = ".index[href*=download]";

    public PtTorrentDetailRule() {
        setId("pt_torrent_detail");
        setSiteId("pt");
        setUrlPattern("http://pt\\.tju\\.edu\\.cn/details\\.php\\?id=(\\d+).*");
        setRefreshRate(86400*5);
        setExtractors(Arrays.asList(new Extractor[] {
            new Extractor(PatternType.REGEX, ID_PATTERN).function("id",
                    new FunctionInvokeChain().append(FunctionType.SET_ID)),
            new Extractor(PatternType.DOM, TITLE_PATTERN).function("text",
                    new FunctionInvokeChain().append(
                            FunctionType.STRIP_AND_STORE, "title")),
            new Extractor(PatternType.REGEX, SIZE_PATTERN).function("size",
                    new FunctionInvokeChain().append(
                            FunctionType.STRIP_AND_STORE, "size")),
            new Extractor(PatternType.REGEX, INFO_PATTERN).function("info",
                    new FunctionInvokeChain().append(FunctionType.HTML_TO_TEXT)
                            .append(FunctionType.STRIP_AND_STORE, "info")),
            new Extractor(PatternType.DOM, DESCRIPTION_PATTERN).function(
                    "text", new FunctionInvokeChain().append(
                            FunctionType.STRIP_AND_STORE, "description")),
            new Extractor(PatternType.REGEX, VIEW_PATTERN).function("view",
                    new FunctionInvokeChain().append(
                            FunctionType.STRIP_AND_STORE, "view")),
            new Extractor(PatternType.REGEX, CLICK_PATTERN).function("click",
                    new FunctionInvokeChain().append(
                            FunctionType.STRIP_AND_STORE, "click")),
            new Extractor(PatternType.REGEX, DOWNLOAD_PATTERN).function(
                    "download", new FunctionInvokeChain().append(
                            FunctionType.STRIP_AND_STORE, "download")),
            new Extractor(PatternType.REGEX, SEEDER_LEECHER_PATTERN).function(
                    "seeder",
                    new FunctionInvokeChain().append(
                            FunctionType.STRIP_AND_STORE, "seeder")).function(
                    "leecher",
                    new FunctionInvokeChain().append(
                            FunctionType.STRIP_AND_STORE, "leecher")),
            new Extractor(PatternType.DOM, TORRENT_NAME_PATTERN).function(
                    "href",
                    new FunctionInvokeChain().append(FunctionType.ABSOLUTE_URL)
                            .append(FunctionType.FETCH)).function(
                    "text",
                    new FunctionInvokeChain().append(
                            FunctionType.STRIP_AND_STORE, "torrentName")),
        }));
        getHeaders()
                .put("Cookie",
                        "c_secure_uid=Mjk2NTQ%3D; c_secure_ssl=bm9wZQ%3D%3D; c_secure_tracker_ssl=bm9wZQ%3D%3D; c_secure_login=bm9wZQ%3D%3D; c_secure_pass=23f23999c38c03e6ef055eb14ea5dcd9;");
    }

}
