package org.tju.so.search.log;

/**
 * Log type definition
 * 
 * @author Tianyi HE <hty0807@gmail.com>
 */
@SuppressWarnings("rawtypes")
public enum EntryType {

    SEARCH(SearchLogEntry.class), SEARCH_RESULT_ITEM(
            SearchResultItemLogEntry.class), COMPLETE(CompleteLogEntry.class), CLICK(
            ClickLogEntry.class);

    EntryType(Class clazz) {
        this.clazz = clazz;
    }

    private Class clazz;

    public Class getClazz() {
        return clazz;
    }
}
