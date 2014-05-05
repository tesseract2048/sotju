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
public class TjunewsInfoDetailRule extends Rule {

    private final static String ID_PATTERN = "newsid=(?<id>\\d+)&";

    private final static String CONTENT_PATTERN = "#zoom";

    private final static String AUTHOR_PATTERN = "<td height=\"25\" align=\"center\" class=\"font_grey_en\">来源：(?<author>[^&]*)&nbsp;&nbsp;";

    private final static String READ_PATTERN = "iframe#click";

    public TjunewsInfoDetailRule() {
        setId("tjunews_info_detail");
        setSiteId("tjunews");
        setUrlPattern("http://www\\.tju\\.edu\\.cn/newscenter/([_a-zA-Z]+)/(\\d+)/([a-zA-Z_0-9]*).htm");
        setRefreshRate(86400 * 30);
        setExtractors(Arrays.asList(new Extractor[] {
            new Extractor(PatternType.REGEX, ID_PATTERN).function("id",
                    new FunctionInvokeChain().append(FunctionType.SET_ID)),
            new Extractor(PatternType.REGEX, AUTHOR_PATTERN).function("author",
                    new FunctionInvokeChain().append(
                            FunctionType.STRIP_AND_STORE, "author")),
            new Extractor(PatternType.DOM, READ_PATTERN).function("src",
                    new FunctionInvokeChain().append(FunctionType.FETCH)),
            new Extractor(PatternType.DOM, CONTENT_PATTERN).function("text",
                    new FunctionInvokeChain().append(
                            FunctionType.STRIP_AND_STORE, "content"))
        }));
    }

}
