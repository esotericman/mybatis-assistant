package org.flmelody.mybatis.generator.pojo;

import lombok.Data;

import java.util.List;

@Data
public class ConfigSetting {

    /**
     * 配置名称
     */
    private String name;
    /**
     * 配置路径
     */
    private String path;
    /**
     * 模板信息
     */
    private List<TemplateSettingDTO> templateSettingDTOList;
}
