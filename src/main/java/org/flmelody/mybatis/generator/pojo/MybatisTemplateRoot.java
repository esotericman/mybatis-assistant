package org.flmelody.mybatis.generator.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class MybatisTemplateRoot implements Serializable {

    /**
     * 模板路径
     */
    private String templateBasePath;

    private ModuleInfoGo moduleUIInfo;

    private ModelInfo modelInfo;


    private List<TemplateSettingDTO> templateSettingDTOList;

    private List<ModuleInfoGo> moduleInfoList = new ArrayList<>();

    public void setModuleInfoList(List<ModuleInfoGo> moduleInfoList) {
        this.moduleInfoList = moduleInfoList;
    }

    public Map<? extends String, ?> toMap() {
        return moduleInfoList.stream().collect(Collectors.toMap(ModuleInfoGo::getConfigName, v -> v, (a, b) -> a));
    }
}
