package org.flmelody.mybatis.setting;

import com.google.common.base.Joiner;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.flmelody.mybatis.setting.configuration.AbstractStatementGenerator;
import org.flmelody.mybatis.setting.configuration.GeneratorHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * The type Mybatis setting.
 * 这里是全局配置, 所以配置文件在目录($APP_CONFIG$)下
 *
 * @author yanglin
 */
@State(
    name = "MybatisSettings",
    storages = @Storage(value = "$APP_CONFIG$/mybatis.xml"))
public class MybatisSettings implements PersistentStateComponent<MybatisSettings>, Serializable {

    // 配置的默认值
    private String mapperIcon;
    private String insertGenerator;
    private String updateGenerator;
    private String deleteGenerator;
    private String selectGenerator;

    private static final transient Joiner joiner = Joiner.on(";");
    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static MybatisSettings getInstance() {
        MybatisSettings service = ServiceManager.getService(MybatisSettings.class);
        // 配置的默认值
        if (service.insertGenerator == null) {
            service.insertGenerator = joiner.join(GeneratorHolder.INSERT_GENERATOR.getPatterns());
        }
        if (service.updateGenerator == null) {
            service.updateGenerator = joiner.join(GeneratorHolder.UPDATE_GENERATOR.getPatterns());
        }
        if (service.deleteGenerator == null) {
            service.deleteGenerator = joiner.join(GeneratorHolder.DELETE_GENERATOR.getPatterns());
        }
        if (service.selectGenerator == null) {
            service.selectGenerator = joiner.join(GeneratorHolder.SELECT_GENERATOR.getPatterns());
        }
        if (service.mapperIcon == null) {
            service.mapperIcon = MapperIcon.BIRD.name();
        }
        return service;
    }

    @Nullable
    @Override
    public MybatisSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull MybatisSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getInsertGenerator() {
        return insertGenerator;
    }

    public String getUpdateGenerator() {
        return updateGenerator;
    }

    public String getDeleteGenerator() {
        return deleteGenerator;
    }

    public String getSelectGenerator() {
        return selectGenerator;
    }

    public String getMapperIcon() {
        return mapperIcon;
    }

    public void setInsertGenerator(String insertGenerator) {
        this.insertGenerator = insertGenerator;
    }

    public void setUpdateGenerator(String updateGenerator) {
        this.updateGenerator = updateGenerator;
    }

    public void setDeleteGenerator(String deleteGenerator) {
        this.deleteGenerator = deleteGenerator;
    }

    public void setSelectGenerator(String selectGenerator) {
        this.selectGenerator = selectGenerator;
    }

    public void setMapperIcon(String mapperIcon) {
        this.mapperIcon = mapperIcon;
    }

    public enum MapperIcon {
        DEFAULT,
        BIRD;
    }
}
