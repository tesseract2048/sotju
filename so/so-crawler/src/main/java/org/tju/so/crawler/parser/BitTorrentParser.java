package org.tju.so.crawler.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tju.so.model.crawler.rule.Extractor;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class BitTorrentParser implements Parser {

    private static final Logger LOG = LoggerFactory
            .getLogger(BitTorrentParser.class);

    private Map<String, Object> torrent = null;

    @SuppressWarnings("unchecked")
    @Override
    public void init(String mimeType, byte[] data) {
        try {
            torrent = Bencode.decode(data);
            LOG.info("Torrent parsed as bencode successfully.");
        } catch (Exception e) {
            torrent = new HashMap<String, Object>();
            LOG.error("Failed to parse bencode.");
        }
    }

    @Override
    public List<Map<String, Object>> extract(Extractor extractor) {
        String pattern = extractor.getPattern();
        switch (extractor.getType()) {
            case SCRIPT:
                return ParserUtil.extractWithScript(torrent, pattern);
            default:
                return null;
        }
    }

}
