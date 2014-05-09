package org.tju.so.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility for embedded script execution, i.e. javascript
 * 
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class ScriptUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ScriptUtil.class);

    /**
     * Execute given script and return its result
     * 
     * @param script
     * @return Result (could be any system type)
     */
    public static Object eval(String script) {
        ScriptEngine engine = new ScriptEngineManager()
                .getEngineByName("JavaScript");
        try {
            Object ret = engine.eval(script);
            return ret;
        } catch (ScriptException e) {
            LOG.error("Script execution failed", e);
            return null;
        }
    }
}
