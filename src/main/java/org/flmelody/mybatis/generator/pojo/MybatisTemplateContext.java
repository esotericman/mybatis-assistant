package org.flmelody.mybatis.generator.pojo;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Mybatis模板上下文配置
 *
 * @author all contributed people
 */
@Data
public class MybatisTemplateContext {
    /**
     * 项目路径
     */
    private String projectPath;
    /**
     * 模块名称
     */
    private String moduleName;
    /**
     * 注解类型
     */
    private String annotationType;
    /**
     * 模板名称
     */
    private String templateName;
    /**
     * 扩展的自定义模板
     */
    private Map<String, ConfigSetting> templateSettingMap = new HashMap<>();

    private MybatisGeneratorProperties mybatisGeneratorProperties;
}
