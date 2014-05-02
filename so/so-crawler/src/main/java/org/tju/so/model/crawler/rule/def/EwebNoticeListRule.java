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
public class EwebNoticeListRule extends Rule {

    private final static String LIST_TITLE_PATTERN = "<TR>([\\r\\n\\t ]+)<TD bgColor=#ffffff>&nbsp;([\\r\\n\\t ]+)<a href=\"/toModule\\.do\\?prefix=/News&page=/newsDetail\\.do\\?infoid=(?<id>\\d+)\\\" target=\\\"blank\\\">([\\r\\n\\t ]+)<font color=\\\"(Blue|red)\\\">(?<title>[^<]+)</font>([\\r\\n\\t ]+)</a>([\\r\\n\\t ]+)</TD>([\\r\\n\\t ]+)<TD noWrap align=center bgColor=#ffffff>([\\r\\n\\t ]+)(?<date>[^<]+)([\\r\\n\\t ]+)</TD>([\\r\\n\\t ]+)</TR>";

    private final static String COLUMN_PATTERN = "<TD noWrap bgColor=#f3f3f3 width=\"90\"><font color=\"#FF0000\"><B>&nbsp;\\[(?<column>[^\\[]+)\\]</B></font> </TD>";

    public EwebNoticeListRule() {
        setId("eweb_notice_list");
        setSiteId("eweb");
        setUrlPattern("http://e.tju.edu.cn/News/noticeList.do");
        setRefreshRate(86400);
        setExtractors(Arrays
                .asList(new Extractor[] {
                    new Extractor(PatternType.REGEX, COLUMN_PATTERN).function(
                            "column", new FunctionInvokeChain().append(
                                    FunctionType.STRIP_AND_STORE, "column")),
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
                                    "id",
                                    new FunctionInvokeChain()
                                            .append(FunctionType.SET_ID, "$val")
                                            .append(FunctionType.FETCH,
                                                    "http://e.tju.edu.cn/toModule.do?prefix=/News&page=/newsDetail.do?infoid=$val"))
                            .function(
                                    "title",
                                    new FunctionInvokeChain().append(
                                            FunctionType.STRIP_AND_STORE,
                                            "title"))
                            .function(
                                    "date",
                                    new FunctionInvokeChain().append(
                                            FunctionType.FORMAT_DATE,
                                            "yy-MM-dd HH:mm:ss").append(
                                            FunctionType.STRIP_AND_STORE,
                                            "date", "$ret"))
                }));
    }

}
