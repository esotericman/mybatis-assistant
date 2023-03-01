package org.flmelody.mybatis.generator.pojo;

import com.intellij.openapi.ui.Messages;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Mybatis生成器相关配置
 *
 * @author all contributed people
 */
@Data
public class MybatisGeneratorProperties {
    /**
     * 界面恢复
     */
    private String moduleName;
    /**
     * 基础包名
     */
    private String basePackage;
    /**
     * 相对包路径
     */
    private String relativePackage;
    /**
     * 编码方式, 默认: UTF-8
     */
    private String encoding;
    /**
     * 模块的源码相对路径
     */
    private String basePath;
    /**
     * 模块路径
     */
    private String modulePath;

    /**
     * 需要生成 toString,hashcode,equals
     */
    private boolean needToStringHashcodeEquals;
    /**
     * 需要生成实体类注释
     */
    private boolean needsComment;
    /**
     * 实体类需要继承的父类
     */
    private String superClass;
    /**
     * 忽略表的前缀
     */
    private String ignoredTablePrefix;
    /**
     * 忽略表的后缀
     */
    private String ignoredTableSuffix;
    /**
     * 忽略的列前缀
     */
    private String ignoredColumnPrefix;
    /**
     * 忽略的列后缀
     */
    private String ignoredColumnSuffix;
    /**
     * lombok
     */
    private boolean useLombokPlugin;
    /**
     * swagger2
     */
    private boolean useSwagger2Plugin;
    /**
     * swagger3
     */
    private boolean useSwagger3Plugin;
    /**
     * jsr303 validation
     */
    private boolean useJsr303;
    /**
     * jsr310 date and time
     */
    private boolean useJsr310;
    private boolean useActualColumns;
    /**
     * 是否生成实体类
     */
    private Boolean needsModel;
    private boolean useActualColumnAnnotationInject;
    /**
     * 模板组名称
     */
    private String templatesName;
    /**
     * 类名后缀
     */
    private String classNamePrefix;
    /**
     * 类名后缀
     */
    private String classNameSuffix;
    /**
     * 已选择的模板名称
     */
    private List<ModuleInfoGo> moduleUIInfoList;
    /**
     * 要生成的表信息列表
     */

    private transient List<TableUIInfo> tableUIInfoList;

    /**
     * 类名生成策略
     * CAMEL: 根据表名生成驼峰命名
     * SAME: 使用表明
     */
    private String classNameStrategy;

    private String generatorOption;

    public List<TableUIInfo> getTableUIInfoList() {
        return tableUIInfoList;
    }

    public void setTableUIInfoList(List<TableUIInfo> tableUIInfoList) {
        this.tableUIInfoList = tableUIInfoList;
    }

    public String getClassNameSuffix() {
        return classNameSuffix;
    }

    public void setClassNameSuffix(String classNameSuffix) {
        this.classNameSuffix = classNameSuffix;
    }

    public boolean checkGenerate() {
        if (StringUtils.isEmpty(moduleName)) {
            Messages.showErrorDialog("ModuleName must not be empty", "Generate Info");
            return false;
        }
        if (StringUtils.isEmpty(templatesName)) {
            Messages.showErrorDialog("TemplatesName must not be empty", "Generate Info");
            return false;
        }
        return true;
    }
}
