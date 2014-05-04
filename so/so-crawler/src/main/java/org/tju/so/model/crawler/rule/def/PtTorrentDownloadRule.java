package org.tju.so.model.crawler.rule.def;

import java.util.Arrays;

import org.tju.so.model.crawler.rule.Extractor;
import org.tju.so.model.crawler.rule.Rule;
import org.tju.so.model.crawler.rule.Extractor.FunctionInvokeChain;
import org.tju.so.model.crawler.rule.Extractor.FunctionType;
import org.tju.so.model.crawler.rule.Extractor.PatternType;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class PtTorrentDownloadRule extends Rule {

    private static final String EXTRACT_SCRIPT = "if (content['0'].info.files) {\n"
            + "  var files = [];\n"
            + "  for (var i in content['0'].info.files) {\n"
            + "    files.push({'name': content['0'].info.files[i].path.join('/'), 'length': content['0'].info.files[i].length})\n"
            + "  }\n"
            + "  return [{'files':files}];\n"
            + "} else {\n"
            + "  return [{'files':[{'name': content['0'].info['name'], 'length': content['0'].info['length']}]}];\n"
            + "}";

    public PtTorrentDownloadRule() {
        setId("pt_torrent_download");
        setSiteId("pt");
        setUrlPattern("http://pt\\.tju\\.edu\\.cn/download\\.php\\?id=(\\d+).*");
        setRefreshRate(NEVER_REFRESH);
        setExtractors(Arrays.asList(new Extractor[] {
            new Extractor(PatternType.SCRIPT, EXTRACT_SCRIPT).function(
                    "files",
                    new FunctionInvokeChain().append(
                            FunctionType.STRIP_AND_STORE, "files").append(
                            FunctionType.INDEX_LATER))
        }));
        getHeaders()
                .put("Cookie",
                        "c_secure_uid=Mjk2NTQ%3D; c_secure_ssl=bm9wZQ%3D%3D; c_secure_tracker_ssl=bm9wZQ%3D%3D; c_secure_login=bm9wZQ%3D%3D; c_secure_pass=23f23999c38c03e6ef055eb14ea5dcd9;");
    }

}
