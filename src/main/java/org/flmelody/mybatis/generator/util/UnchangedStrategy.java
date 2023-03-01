package org.flmelody.mybatis.generator.util;
/**
 * @author all contributed people
 */
public class UnchangedStrategy implements ClassNameStrategy{
    @Override
    public String getText() {
        return "unchanged";
    }

    @Override
    public String calculateClassName(String tableName, String ignorePrefix, String ignoreSuffix) {
        return tableName;
    }
}
