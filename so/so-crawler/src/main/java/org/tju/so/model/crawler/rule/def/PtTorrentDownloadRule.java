package org.tju.so.model.crawler.rule.def;

import java.util.Arrays;

import org.tju.so.model.crawler.rule.Extractor;
import org.tju.so.model.crawler.rule.Rule;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class PtTorrentDownloadRule extends Rule {

    public PtTorrentDownloadRule() {
        setId("pt_torrent_download");
        setSiteId("pt");
        setUrlPattern("http://pt\\.tju\\.edu\\.cn/download\\.php\\?id=(\\d+)(&.+)*");
        setRefreshRate(86400);
        // TODO
        setExtractors(Arrays.asList(new Extractor[] {}));
        getHeaders()
                .put("Cookie",
                        "c_secure_uid=Mjk2NTQ%3D; c_secure_ssl=bm9wZQ%3D%3D; c_secure_tracker_ssl=bm9wZQ%3D%3D; c_secure_login=bm9wZQ%3D%3D; c_secure_pass=23f23999c38c03e6ef055eb14ea5dcd9;");
    }

}
