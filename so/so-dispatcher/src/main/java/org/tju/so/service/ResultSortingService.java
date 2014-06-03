package org.tju.so.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tju.so.model.DocumentIdentity;
import org.tju.so.search.context.Context;
import org.tju.so.search.context.Query;
import org.tju.so.search.context.ResultItem;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
@Service
public class ResultSortingService {

    private static final Logger LOG = LoggerFactory
            .getLogger(ResultSortingService.class);

    @Autowired
    private ClickModelService clickModelService;

    private void sortWithClickModel(String keyword, int start,
            List<ResultItem> items) throws Exception {
        int lastItemIndex = clickModelService.getMaxPosition() - start;
        if (lastItemIndex < 0)
            return;
        final Map<DocumentIdentity, Double> relevances = clickModelService
                .getRelevance(keyword);
        List<ResultItem> sortingItems = items.subList(0, lastItemIndex);
        Collections.sort(sortingItems, new Comparator<ResultItem>() {

            @Override
            public int compare(ResultItem o1, ResultItem o2) {
                double r1 = relevances.get((DocumentIdentity) o1.getEntity());
                double r2 = relevances.get((DocumentIdentity) o2.getEntity());
                if (r1 > r2)
                    return -1;
                if (r2 > r1)
                    return 1;
                return 0;
            }

        });
    }

    public void postSort(Context context) {
        Query query = context.getQuery();
        if (query.getSchemaIds().length == 0 && query.getSiteIds().length == 0) {
            try {
                sortWithClickModel(query.getQuery(), query.getStart(),
                        context.getResult());
            } catch (Exception e) {
                LOG.warn("Failed to sort results with click model", e);
            }
        }
    }

}
