package org.flmelody.mybatis.generator.ui;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import org.flmelody.mybatis.generator.pojo.*;
import org.flmelody.mybatis.generator.setting.TemplatesSettings;
import org.flmelody.mybatis.util.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author all contributied people
 */
public class MybatisGeneratorUIManager extends DialogWrapper {
    private final CodeGenerateUI codeGenerateUI = new CodeGenerateUI();

    private final TablePreviewUI tablePreviewUI = new TablePreviewUI();

    private final JPanel rootPanel = new JPanel();

    private final List<JPanel> containerPanelList;

    private final Action previousAction;

    private int page = 0;
    private Project project;

    public MybatisGeneratorUIManager(@Nullable Project project) {
        super(project);
        this.setTitle("Generate Options");
        setOKButtonText("Next");
        setCancelButtonText("Cancel");

        previousAction = new DialogWrapperAction("Previous") {
            @Override
            protected void doAction(ActionEvent e) {
                page = page - 1;
                switchPage(page);
                previousAction.setEnabled(false);
                setOKButtonText("Next");
            }
        };
        // 默认禁用 上一个设置
        previousAction.setEnabled(false);
        // 初始化容器列表
        List<JPanel> list = new ArrayList<>();
        list.add(tablePreviewUI.getRootPanel());
        list.add(codeGenerateUI.getRootPanel());
        containerPanelList = list;
        // 默认切换到第一页
        switchPage(0);

        super.init();
    }

    @Override
    protected void doOKAction() {
        int lastPage = 1;
        if (page == lastPage) {
            super.doOKAction();
            return;
        }
        // 替换第二个panel的占位符
        ModelInfo modelInfo = tablePreviewUI.buildDomainInfo();
        if (StringUtils.isEmpty(modelInfo.getModulePath())) {
            Messages.showMessageDialog("Please select module to generate files", "Generate File", Messages.getWarningIcon());
            return;
        }
        if (!new File(modelInfo.getModulePath()).exists()) {
            Messages.showMessageDialog("ModulePath is not a legal path", "Generate File", Messages.getWarningIcon());
            return;
        }
        page = page + 1;
        setOKButtonText("Finish");
        previousAction.setEnabled(true);

        TemplatesSettings templatesSettings = TemplatesSettings.getInstance(project);
        final MybatisTemplateContext mybatisTemplateContext = templatesSettings.getTemplateConfigs();
        final Map<String, ConfigSetting> settingMap = templatesSettings.getTemplateSettingMap();
        if (settingMap.isEmpty()) {
            throw new RuntimeException("无法获取模板");
        }
        codeGenerateUI.fillData(project,
                mybatisGeneratorProperties,
                modelInfo,
                mybatisTemplateContext.getTemplateName(),
                settingMap);
        switchPage(page);

    }


    private void switchPage(int newPage) {
        rootPanel.removeAll();
        JPanel comp = containerPanelList.get(newPage);
        rootPanel.add(comp);
        rootPanel.repaint();//刷新页面，重绘面板
        rootPanel.validate();//使重绘的面板确认生效
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return rootPanel;
    }

    @Override
    protected Action[] createActions() {
        return new Action[]{previousAction, getOKAction(), getCancelAction()};
    }

    private MybatisGeneratorProperties mybatisGeneratorProperties;

    public void fillData(Project project, List<DbTable> tableElements) {
        this.project = project;
        TemplatesSettings templatesSettings = TemplatesSettings.getInstance(project);
        final MybatisTemplateContext mybatisTemplateContext = templatesSettings.getTemplateConfigs();
        mybatisGeneratorProperties = mybatisTemplateContext.getMybatisGeneratorProperties();
        if (mybatisGeneratorProperties == null) {
            mybatisGeneratorProperties = new DefaultMybatisGeneratorProperties(mybatisTemplateContext);
        }

        final Map<String, ConfigSetting> settingMap = templatesSettings.getTemplateSettingMap();
        if (settingMap.isEmpty()) {
            throw new RuntimeException("无法获取模板");
        }

        tablePreviewUI.fillData(project, tableElements, mybatisGeneratorProperties);

    }

    public MybatisGeneratorProperties determineGenerateConfig() {
        MybatisGeneratorProperties mybatisGeneratorProperties = new MybatisGeneratorProperties();
        codeGenerateUI.refreshGenerateConfig(mybatisGeneratorProperties);
        tablePreviewUI.refreshGenerateConfig(mybatisGeneratorProperties);
        return mybatisGeneratorProperties;
    }


}
