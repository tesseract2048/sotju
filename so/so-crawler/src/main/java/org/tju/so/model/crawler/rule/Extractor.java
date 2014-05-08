package org.tju.so.model.crawler.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class Extractor {

    public final static String INVOKE_PREPARE = "_prepare";

    public final static String INVOKE_FINISH = "_finish";

    public enum PatternType {
        REGEX, DOM, WHOLE, SCRIPT
    }

    public enum FunctionType {
        NEW_CONTEXT, SET_SCHEMA_ID, SET_SITE_ID, SET_ID, ABSOLUTE_URL, FETCH, STORE, INDEX, DELETE, INDEX_LATER, DELETE_LATER, FORMAT_DATE, STRIP, STRIP_AND_STORE, HTML_TO_TEXT, SCRIPT
    }

    public static class FunctionInvoke {
        private FunctionType type;

        private String op1;

        private String op2;

        public FunctionInvoke() {}

        public FunctionInvoke(FunctionType type) {
            setType(type);
        }

        public FunctionInvoke(FunctionType type, String op1) {
            this(type);
            setOp1(op1);
        }

        public FunctionInvoke(FunctionType type, String op1, String op2) {
            this(type, op1);
            setOp2(op2);
        }

        /**
         * @return the type
         */
        public FunctionType getType() {
            return type;
        }

        /**
         * @param type
         *            the type to set
         */
        public void setType(FunctionType type) {
            this.type = type;
        }

        /**
         * @return the op1
         */
        public String getOp1() {
            return op1;
        }

        /**
         * @param op1
         *            the op1 to set
         */
        public void setOp1(String op1) {
            this.op1 = op1;
        }

        /**
         * @return the op2
         */
        public String getOp2() {
            return op2;
        }

        /**
         * @param op2
         *            the op2 to set
         */
        public void setOp2(String op2) {
            this.op2 = op2;
        }

    }

    public static class FunctionInvokeChain {
        private List<FunctionInvoke> functions;

        public FunctionInvokeChain() {
            functions = new ArrayList<FunctionInvoke>();
        }

        /**
         * @return the functions
         */
        public List<FunctionInvoke> getFunctions() {
            return functions;
        }

        /**
         * @param functions
         *            the functions to set
         */
        public void setFunctions(List<FunctionInvoke> functions) {
            this.functions = functions;
        }

        public FunctionInvokeChain append(FunctionInvoke function) {
            functions.add(function);
            return this;
        }

        public FunctionInvokeChain append(FunctionType type) {
            functions.add(new FunctionInvoke(type));
            return this;
        }

        public FunctionInvokeChain append(FunctionType type, String op1) {
            functions.add(new FunctionInvoke(type, op1));
            return this;
        }

        public FunctionInvokeChain append(FunctionType type, String op1,
                String op2) {
            functions.add(new FunctionInvoke(type, op1, op2));
            return this;
        }
    }

    private PatternType type;

    private String pattern;

    private Map<String, FunctionInvokeChain> invokes;

    public Extractor() {
        invokes = new HashMap<String, FunctionInvokeChain>();
    }

    public Extractor(PatternType type, String pattern) {
        this();
        setType(type);
        setPattern(pattern);
    }

    public Extractor function(String name, FunctionInvokeChain invoke) {
        invokes.put(name, invoke);
        return this;
    }

    /**
     * @return the type
     */
    public PatternType getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(PatternType type) {
        this.type = type;
    }

    /**
     * @return the pattern
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * @param pattern
     *            the pattern to set
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * @return the invokes
     */
    public Map<String, FunctionInvokeChain> getInvokes() {
        return invokes;
    }

    /**
     * @param invokes
     *            the invokes to set
     */
    public void setInvokes(Map<String, FunctionInvokeChain> invokes) {
        this.invokes = invokes;
    }

}
