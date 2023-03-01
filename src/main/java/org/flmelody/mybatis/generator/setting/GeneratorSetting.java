package org.flmelody.mybatis.generator.setting;

import lombok.Data;

/**
 * @author flmelody
 */
@Data
public class GeneratorSetting {
    private String moduleName;
    private String basePackage;
    private String relativePackage;
    private String encoding;
    private String basePath;
    private String modulePath;
    private String ignoreTablePrefix;
    private String ignoreTableSuffix;
    private String ignoreFieldPrefix;
    private String ignoreFieldSuffix;
    private boolean useLombokPlugin;
    private boolean overwriteToStringHashcodeEquals;
    private boolean appendComment;
    private boolean swagger2Annotation;
    private boolean swagger3Annotation;
    private boolean userJsr310;
}
