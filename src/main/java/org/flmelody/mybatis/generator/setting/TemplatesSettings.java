package org.flmelody.mybatis.generator.setting;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.flmelody.mybatis.generator.pojo.ConfigSetting;
import org.flmelody.mybatis.generator.pojo.MybatisTemplateContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author all contributed people
 */
@State(name = "TemplatesSettings", storages = {@Storage("mybatis/templates.xml")})
public class TemplatesSettings implements PersistentStateComponent<TemplatesSettings> {

    private MybatisTemplateContext templateConfigs;

    @NotNull
    public static TemplatesSettings getInstance(Project project) {
        TemplatesSettings service = project.getService(TemplatesSettings.class);
        // 配置的默认值
        if (service.templateConfigs == null) {
            // 默认配置
            MybatisTemplateContext mybatisTemplateContext = new MybatisTemplateContext();
            mybatisTemplateContext.setTemplateSettingMap(new HashMap<>(2<<3));
            mybatisTemplateContext.setProjectPath(project.getBasePath());
            service.templateConfigs = mybatisTemplateContext;
        }
        return service;
    }

    @Override
    public @Nullable TemplatesSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull TemplatesSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public MybatisTemplateContext getTemplateConfigs() {
        return templateConfigs;
    }

    public void setTemplateConfigs(MybatisTemplateContext templateConfigs) {
        this.templateConfigs = templateConfigs;
    }

    /**
     * 默认的配置更改是无效的
     *
     * @return
     */
    public Map<String, ConfigSetting> getTemplateSettingMap() {
        // 保存全局配置
        Map<String, ConfigSetting> setTemplateSettingMap = DefaultSettingsConfig.defaultSettings();
        final Map<String, ConfigSetting> templateSettingMap = new LinkedHashMap<>(setTemplateSettingMap);
        // 加载自定义配置
        final Map<String, ConfigSetting> settingMap = templateConfigs.getTemplateSettingMap();
        templateSettingMap.putAll(settingMap);
        return templateSettingMap;
    }
}
