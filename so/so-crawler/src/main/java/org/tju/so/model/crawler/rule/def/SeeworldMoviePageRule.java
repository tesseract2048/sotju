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
public class SeeworldMoviePageRule extends Rule {

    private final static String NEXT_PAGE_SCRIPT = "var maxPage = Math.ceil(content.page.count / content.page.size);\n"
            + "var currPage = content.page.page;\n"
            + "if (currPage < maxPage) {\n"
            + "  return [{\"nextPage\": (currPage + 1).toString()}];\n"
            + "} else {\n" + "  return [];\n" + "}";

    private final static String MOVIE_SCRIPT = "return content.resultList;";

    public SeeworldMoviePageRule() {
        setId("seeworld_movie_page");
        setSiteId("seeworld");
        setUrlPattern("http://see\\.tju\\.edu\\.cn/Movie/getMoviesByPage_json.*");
        setRefreshRate(3600);
        setExtractors(Arrays
                .asList(new Extractor[] {
                    new Extractor(PatternType.SCRIPT, NEXT_PAGE_SCRIPT)
                            .function(
                                    "nextPage",
                                    new FunctionInvokeChain()
                                            .append(FunctionType.ABSOLUTE_URL,
                                                    "$url",
                                                    "?extId=$q_extId&page=$val&type=$q_type")
                                            .append(FunctionType.FETCH, "$ret",
                                                    "extId=$q_extId&page=$val&type=$q_type")),
                    new Extractor(PatternType.SCRIPT, MOVIE_SCRIPT)
                            .function(
                                    Extractor.INVOKE_PREPARE,
                                    new FunctionInvokeChain()
                                            .append(FunctionType.NEW_CONTEXT)
                                            .append(FunctionType.SET_SITE_ID,
                                                    "$siteId")
                                            .append(FunctionType.SET_SCHEMA_ID,
                                                    "movie"))
                            .function(
                                    "ID",
                                    new FunctionInvokeChain()
                                            .append(FunctionType.SET_ID)
                                            .append(FunctionType.STRIP_AND_STORE,
                                                    "url",
                                                    "http://see.tju.edu.cn/movie/$val.html"))
                            .function(
                                    "title",
                                    new FunctionInvokeChain().append(
                                            FunctionType.STRIP_AND_STORE,
                                            "title"))
                            .function(
                                    "introduction",
                                    new FunctionInvokeChain().append(
                                            FunctionType.HTML_TO_TEXT).append(
                                            FunctionType.STRIP_AND_STORE,
                                            "description"))
                            .function(
                                    "image",
                                    new FunctionInvokeChain().append(
                                            FunctionType.ABSOLUTE_URL,
                                            "http://see.tju.edu.cn/poster/",
                                            "$ret").append(
                                            FunctionType.STRIP_AND_STORE,
                                            "image"))
                            .function(
                                    "clickCount",
                                    new FunctionInvokeChain().append(
                                            FunctionType.STRIP_AND_STORE,
                                            "view"))
                            .function(
                                    "commentsCount",
                                    new FunctionInvokeChain().append(
                                            FunctionType.STRIP_AND_STORE,
                                            "comment"))
                            .function(
                                    "addTime",
                                    new FunctionInvokeChain().append(
                                            FunctionType.FORMAT_DATE,
                                            "yyyy-MM-dd HH:mm:ss").append(
                                            FunctionType.STRIP_AND_STORE,
                                            "addTime"))
                            .function(
                                    Extractor.INVOKE_FINISH,
                                    new FunctionInvokeChain()
                                            .append(FunctionType.INDEX_LATER))
                }));
        getHeaders().put("Cookie",
                "token=0682babcac107f2f05f43618253dea19; ID=1225;");
    }

}
