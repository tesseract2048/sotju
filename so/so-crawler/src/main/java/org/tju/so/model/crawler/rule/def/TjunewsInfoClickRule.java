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
public class TjunewsInfoClickRule extends Rule {

    public TjunewsInfoClickRule() {
        setId("tjunews_info_click");
        setSiteId("tjunews");
        setUrlPattern("http://121\\.193\\.130\\.63:8080/ClickCount/Onclick\\.do.*");
        setRefreshRate(86400 * 30);
        setExtractors(Arrays.asList(new Extractor[] {
            new Extractor(PatternType.DOM, "body").function(
                    "text",
                    new FunctionInvokeChain().append(
                            FunctionType.STRIP_AND_STORE, "read").append(
                            FunctionType.INDEX_LATER))
        }));
    }

}
