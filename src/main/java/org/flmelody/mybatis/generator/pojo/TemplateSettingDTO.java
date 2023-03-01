package org.flmelody.mybatis.generator.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class TemplateSettingDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 配置名称
     */
    private String configName;
    /**
     * 配置文件名称
     */
    private String configFile;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 后缀
     */
    private String suffix;
    /**
     * 包名
     */
    private String packageName;
    /**
     * 编码
     */
    private String encoding;
    /**
     * 相对模块的资源文件路径
     */
    private String basePath;

}
