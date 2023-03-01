package org.flmelody.mybatis.generator.util;

/**
 * @author all contributed people
 */
public interface ClassNameStrategy {
    String getText();
    String calculateClassName(String tableName, String ignorePrefix, String ignoreSuffix);

    enum ClassNameStrategyEnum {
        CAMEL("camel"),
        SAME("unchanged");
        private final String text;

        public String getText() {
            return text;
        }

        ClassNameStrategyEnum(String text) {
            this.text = text;
        }
    }
}
