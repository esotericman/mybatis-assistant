package org.flmelody.mybatis.generator.pojo;


import org.flmelody.mybatis.generator.util.ClassNameStrategy;

import java.util.Collections;
import java.util.List;

/**
 * 默认生成器配置
 * @author : ls9527
 * @date : 2021/7/2
 */
public class DefaultMybatisGeneratorProperties extends MybatisGeneratorProperties {
    private final MybatisTemplateContext mybatisTemplateContext;

    public DefaultMybatisGeneratorProperties(MybatisTemplateContext mybatisTemplateContext) {
        this.mybatisTemplateContext = mybatisTemplateContext;
    }

    @Override
    public String getModuleName() {
        return mybatisTemplateContext.getModuleName();
    }

    @Override
    public List<ModuleInfoGo> getModuleUIInfoList() {
        return Collections.emptyList();
    }

    @Override
    public boolean isNeedsComment() {
        return true;
    }

    @Override
    public boolean isNeedToStringHashcodeEquals() {
        return true;
    }

    @Override
    public String getBasePackage() {
        return "generator";
    }

    @Override
    public String getRelativePackage() {
        return "model";
    }

    @Override
    public String getBasePath() {
        return "src/main/java";
    }


    @Override
    public String getEncoding() {
        return "UTF-8";
    }

    @Override
    public String getClassNameStrategy() {
        return ClassNameStrategy.ClassNameStrategyEnum.CAMEL.name();
    }
}
