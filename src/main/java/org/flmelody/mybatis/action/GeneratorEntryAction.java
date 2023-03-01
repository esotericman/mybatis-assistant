package org.flmelody.mybatis.action;


import com.intellij.database.model.DasObject;
import com.intellij.database.psi.DbTable;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import org.flmelody.mybatis.generator.MybatisGenerator;
import org.flmelody.mybatis.generator.pojo.MybatisGeneratorProperties;
import org.flmelody.mybatis.generator.pojo.TableUIInfo;
import org.flmelody.mybatis.generator.pojo.MybatisTemplateContext;
import org.flmelody.mybatis.generator.setting.TemplatesSettings;
import org.flmelody.mybatis.generator.ui.MybatisGeneratorUIManager;
import org.flmelody.mybatis.util.PluginExistsUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type Mybatis generator main action.
 *
 * @author all contribuited people
 */
public class GeneratorEntryAction extends AnAction {
    /**
     * 点击后打开插件主页面
     *
     * @param e AnActionEvent
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        PsiElement[] psiElements = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (psiElements == null || psiElements.length == 0) {
            Messages.showMessageDialog("Please select one or more tables", "Mybatis Generator", Messages.getWarningIcon());
            return;
        }
        List<DbTable> dbTables = Stream.of(psiElements).filter(element -> element instanceof DbTable).map(element -> (DbTable) element).collect(Collectors.toList());
        MybatisGeneratorUIManager mybatisGeneratorUIManager = new MybatisGeneratorUIManager(project);
        // 填充默认的选项
        mybatisGeneratorUIManager.fillData(project, dbTables);
        mybatisGeneratorUIManager.show();
        // 模态窗口选择 OK, 生成相关代码
        if (mybatisGeneratorUIManager.getExitCode() == Messages.YES) {
            // 生成代码
            MybatisGeneratorProperties mybatisGeneratorProperties = mybatisGeneratorUIManager.determineGenerateConfig();
            if (!mybatisGeneratorProperties.checkGenerate()) {
                return;
            }
            generateCode(project, dbTables, mybatisGeneratorProperties);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(GeneratorEntryAction.class);

    public void generateCode(Project project, List<DbTable> psiElements, MybatisGeneratorProperties mybatisGeneratorProperties) {
        try {
            // 保存配置, 更新最后一次存储的配置
            TemplatesSettings templatesSettings = TemplatesSettings.getInstance(project);
            MybatisTemplateContext templateConfigs = templatesSettings.getTemplateConfigs();
            templateConfigs.setMybatisGeneratorProperties(mybatisGeneratorProperties);
            templateConfigs.setTemplateName(mybatisGeneratorProperties.getTemplatesName());
            templateConfigs.setModuleName(mybatisGeneratorProperties.getModuleName());
            templatesSettings.setTemplateConfigs(templateConfigs);

            Map<String, DbTable> tableMapping = psiElements.stream().collect(Collectors.toMap(DasObject::getName, a -> a, (a, b) -> a));
            for (TableUIInfo uiInfo : mybatisGeneratorProperties.getTableUIInfoList()) {
                String tableName = uiInfo.getTableName();
                DbTable dbTable = tableMapping.get(tableName);
                if (dbTable != null) {
                    // 生成代码
                    MybatisGenerator.generate(project,
                            mybatisGeneratorProperties,
                            templatesSettings.getTemplateSettingMap(),
                            dbTable,
                            uiInfo.getClassName(),
                            uiInfo.getTableName());
                }
            }
            VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
            logger.info("全部代码生成成功, 文件内容已更新. config: {}", mybatisGeneratorProperties);
            notify(project, mybatisGeneratorProperties.getModuleName());
        } catch (Exception e) {
            logger.error("生成代码出错", e);
        }
    }

    private void notify(Project project, String content) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("Mybatis Assistant Notification")
                .createNotification("Mybatis assistant", content+" generate finished", NotificationType.INFORMATION)
                .notify(project);
    }


    @Override
    public void update(@NotNull AnActionEvent e) {
        Boolean visible = null;
        PsiElement[] psiElements = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (psiElements == null || psiElements.length == 0) {
            visible = false;
        }
        boolean existsDbTools = PluginExistsUtils.existsDbTools();
        if (!existsDbTools) {
            visible = false;
        }
        if (visible == null) {
            if (!Stream.of(psiElements).allMatch(item -> CheckMatch.checkAssignableFrom(item.getClass()))) {
                visible = false;
            }
        }
        // 未安装Database Tools插件时，不展示菜单
        if (visible != null) {
            e.getPresentation().setEnabledAndVisible(false);
        }

    }

    private static class CheckMatch {
        public static boolean checkAssignableFrom(Class<? extends PsiElement> aClass) {
            try {
                return DbTable.class.isAssignableFrom(aClass);
            } catch (Exception e) {
                return false;
            }
        }
    }

}
