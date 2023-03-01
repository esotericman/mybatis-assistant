package org.flmelody.mybatis.generator.ui;

import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.ChooseModulesDialog;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.TableView;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.util.PlatformIcons;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import org.apache.commons.collections.CollectionUtils;
import org.flmelody.mybatis.generator.pojo.*;
import org.flmelody.mybatis.generator.util.DomainPlaceHolder;
import org.flmelody.mybatis.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author all contributed people
 */
public class CodeGenerateUI {
    public static final String DOMAIN = "model";
    private JPanel rootPanel;
    private JCheckBox commentCheckBox;
    private JCheckBox lombokCheckBox;
    private JCheckBox actualColumnCheckBox;
    private JCheckBox JSR310DateAPICheckBox;
    private JCheckBox toStringHashCodeEqualsCheckBox;
    private JPanel templateExtraPanel;
    private JPanel templateExtraRadiosPanel;
    private JCheckBox needsModelCheckBox;
    private JCheckBox JSR303AnnotationCheckBox;
    private JRadioButton noneAnnotationRadioButton;
    private JRadioButton swagger2AnnotationRadioButton;
    private JRadioButton swagger3AnnotationRadioButton;
    private JRadioButton newFileRadioButton;
    private JRadioButton overwriteFileRadioButton;
    private JRadioButton smartMergeRadioButton;
    private JPanel modelOptionsContainer;
    private JPanel annotatedDocumentsContainer;
    private JPanel generatorOptionsContainer;
    private final ButtonGroup generatorButtonGroup = new ButtonGroup();
    private final ButtonGroup templateButtonGroup = new ButtonGroup();
    private final ButtonGroup swaggerButtonGroup = new ButtonGroup();
    ListTableModel<ModuleInfoGo> model = new ListTableModel<>(
            new MybatisModuleInfo("config name", 120, false),
            new MybatisModuleInfo("module path", 420, true, true),
            new MybatisModuleInfo("base path", 140, true),
            new MybatisModuleInfo("package name", 160, true)
    );
    private Project project;
    private ModelInfo modelInfo;
    private String selectedTemplateName;

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public void fillData(Project project,
                         MybatisGeneratorProperties mybatisGeneratorProperties,
                         ModelInfo modelInfo,
                         String defaultsTemplatesName,
                         Map<String, ConfigSetting> templateSettingMap) {
        this.project = project;
        this.modelInfo = modelInfo;

        // 是否需要生成默认的模型类
        if (mybatisGeneratorProperties.getNeedsModel() != null) {
            needsModelCheckBox.setSelected(mybatisGeneratorProperties.getNeedsModel());
            registerCoreListener();
        }
        // 需要生成 toString/hashcode/equals
        if (mybatisGeneratorProperties.isNeedToStringHashcodeEquals()) {
            toStringHashCodeEqualsCheckBox.setSelected(true);
        }
        // 是否需要字段注释
        if (mybatisGeneratorProperties.isNeedsComment()) {
            commentCheckBox.setSelected(true);
        }
        // 使用lombok 注解生成实体类
        if (mybatisGeneratorProperties.isUseLombokPlugin()) {
            lombokCheckBox.setSelected(true);
        }
        // 使用数据库的字段作为列名
        if (mybatisGeneratorProperties.isUseActualColumns()) {
            actualColumnCheckBox.setSelected(true);
        }
        if (mybatisGeneratorProperties.isUseJsr303()) {
            JSR303AnnotationCheckBox.setSelected(true);
        }
        // 使用LocalDate
        if (mybatisGeneratorProperties.isUseJsr310()) {
            JSR310DateAPICheckBox.setSelected(true);
        }
        if (mybatisGeneratorProperties.isUseSwagger2Plugin()) {
            swagger2AnnotationRadioButton.setSelected(true);
        }
        if (mybatisGeneratorProperties.isUseSwagger3Plugin()) {
            swagger3AnnotationRadioButton.setSelected(true);
        }
        if (!swagger2AnnotationRadioButton.isSelected() && !swagger3AnnotationRadioButton.isSelected()) {
            noneAnnotationRadioButton.setSelected(true);
        }
        // 生成配置
        initGeneratorButtonGroup(mybatisGeneratorProperties);
        // 初始化表格, 用于展示要生成的文件模块
        initTemplates(mybatisGeneratorProperties, defaultsTemplatesName, templateSettingMap);
        // 注解式文档
        initApiButtonGroup();
    }

    private void registerCoreListener() {
        List<Component> components = Stream
                .of(modelOptionsContainer.getComponents(), generatorOptionsContainer.getComponents(), annotatedDocumentsContainer.getComponents())
                .flatMap(Stream::of).collect(Collectors.toList());
        needsModelCheckBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                for (Component component : components) {
                    if (!component.equals(needsModelCheckBox)) {
                        component.setEnabled(false);
                    }
                }
            } else if (e.getStateChange() == ItemEvent.SELECTED) {
                for (Component component : components) {
                    if (!component.equals(needsModelCheckBox)) {
                        component.setEnabled(true);
                    }
                }
            }
        });
        if (!needsModelCheckBox.isSelected()) {
            for (Component component : components) {
                if (!component.equals(needsModelCheckBox)) {
                    component.setEnabled(false);
                }
            }
        }
    }

    private void initGeneratorButtonGroup(MybatisGeneratorProperties mybatisGeneratorProperties) {
        generatorButtonGroup.add(newFileRadioButton);
        generatorButtonGroup.add(overwriteFileRadioButton);
        generatorButtonGroup.add(smartMergeRadioButton);
        Enumeration<AbstractButton> elements = generatorButtonGroup.getElements();
        while (elements.hasMoreElements()) {
            AbstractButton button = elements.nextElement();
            button.addItemListener(e -> {
                if (e.getStateChange() != ItemEvent.SELECTED) {
                    return;
                }
                final JRadioButton jRadioButton = (JRadioButton) e.getItem();
                mybatisGeneratorProperties.setGeneratorOption(jRadioButton.getText());
            });
        }
    }

    private void initApiButtonGroup() {
        swaggerButtonGroup.add(noneAnnotationRadioButton);
        swaggerButtonGroup.add(swagger2AnnotationRadioButton);
        swaggerButtonGroup.add(swagger3AnnotationRadioButton);
    }


    private void selectDefaultTemplateRadio(List<TemplateSettingDTO> list, String templatesName) {
        // 选择默认的模板, 或者记忆的模板
        if (StringUtils.isEmpty(templatesName)) {
            final Enumeration<AbstractButton> elements = templateButtonGroup.getElements();
            if (elements.hasMoreElements()) {
                final AbstractButton abstractButton = elements.nextElement();
                templatesName = abstractButton.getText();
            }
        }
        // 如果找不到, 并且无法选中默认模板就不选择了
        if (templatesName == null) {
            return;
        }
        final Enumeration<AbstractButton> elements = templateButtonGroup.getElements();
        while (elements.hasMoreElements()) {
            final AbstractButton abstractButton = elements.nextElement();
            final String text = abstractButton.getText();
            if (templatesName.equals(text)) {
                abstractButton.setSelected(true);
                break;
            }
        }
        initSelectedModuleTable(list, modelInfo.getModulePath());
    }

    private boolean refresh = false;

    private void initTemplates(MybatisGeneratorProperties mybatisGeneratorProperties,
                               String defaultsTemplatesName,
                               Map<String, ConfigSetting> templateSettingMap) {
        if (templateSettingMap.keySet().isEmpty()) {
            throw new RuntimeException("模板列表为空, 请加入模板");
        }
        // 使用上一次生成代码的模板名称
        this.selectedTemplateName = determineTemplateName(templateSettingMap, mybatisGeneratorProperties.getTemplatesName());
        TableView<ModuleInfoGo> tableView = new TableView<>(model);

        GridConstraints gridConstraints = new GridConstraints();
        gridConstraints.setFill(GridConstraints.FILL_NONE);

        initPanel(templateSettingMap, tableView, gridConstraints);

        initRadioLayout(templateSettingMap.keySet());

        final ItemListener itemListener = initItemListener(mybatisGeneratorProperties, defaultsTemplatesName, templateSettingMap);

        final Enumeration<AbstractButton> radios = templateButtonGroup.getElements();
        while (radios.hasMoreElements()) {
            final JRadioButton radioButton = (JRadioButton) radios.nextElement();
            radioButton.addItemListener(itemListener);
        }
        ConfigSetting configSetting = templateSettingMap.get(selectedTemplateName);
        selectDefaultTemplateRadio(configSetting.getTemplateSettingDTOList(), selectedTemplateName);

    }

    private ItemListener initItemListener(MybatisGeneratorProperties mybatisGeneratorProperties, String defaultsTemplatesName, Map<String, ConfigSetting> templateSettingMap) {
        return new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                final JRadioButton jRadioButton = (JRadioButton) e.getItem();
                // 只接受选择事件
                if (e.getStateChange() != ItemEvent.SELECTED) {
                    return;
                }
                String templatesName = jRadioButton.getText();
                ConfigSetting configSetting = buildTemplatesSettings(templatesName);
                List<ModuleInfoGo> moduleUIInfoList = buildModuleUIInfos(templatesName, configSetting.getTemplateSettingDTOList());
                initMemoryModuleTable(moduleUIInfoList);
                selectedTemplateName = jRadioButton.getText();
            }

            private List<ModuleInfoGo> buildModuleUIInfos(String templatesName, List<TemplateSettingDTO> list) {
                List<ModuleInfoGo> moduleUIInfoList = null;
                // 1. 优先选择默认的
                if (!refresh && templatesName.equals(mybatisGeneratorProperties.getTemplatesName())) {
                    moduleUIInfoList = mybatisGeneratorProperties.getModuleUIInfoList();
                    // 如果默认选择的数据是历史版本, 则重置为空. 使用插件的最新默认配置
                    boolean check = checkModuleInfo(moduleUIInfoList);
                    if (!check) {
                        moduleUIInfoList = null;
                    }
                }
                // 2. 其次根据选择的模板名称来决定使用哪个模板
                if (moduleUIInfoList == null) {
                    moduleUIInfoList = buildByTemplates(list, modelInfo.getModulePath());
                }
                return moduleUIInfoList;
            }

            private boolean checkModuleInfo(List<ModuleInfoGo> moduleUIInfoList) {
                for (ModuleInfoGo moduleInfoGo : moduleUIInfoList) {
                    if (moduleInfoGo.getFileNameWithSuffix() == null ||
                            moduleInfoGo.getModulePath() == null ||
                            moduleInfoGo.getEncoding() == null ||
                            moduleInfoGo.getFileName() == null ||
                            moduleInfoGo.getConfigName() == null ||
                            moduleInfoGo.getBasePath() == null ||
                            moduleInfoGo.getPackageName() == null ||
                            moduleInfoGo.getConfigFileName() == null) {
                        return false;
                    }
                }
                return true;
            }

            private ConfigSetting buildTemplatesSettings(String templatesName) {
                ConfigSetting configSetting = null;
                // 选择选定的模板
                if (!StringUtils.isEmpty(templatesName)) {
                    configSetting = templateSettingMap.get(templatesName);
                }
                // 选择默认模板
                if (configSetting == null && !StringUtils.isEmpty(defaultsTemplatesName)) {
                    configSetting = templateSettingMap.get(defaultsTemplatesName);
                }
                // 默认模板没有设置, 或者默认模板改了新的名字, 找到values的第一条记录
                if (configSetting == null) {
                    configSetting = templateSettingMap.values().iterator().next();
                }
                return configSetting;
            }

            private List<ModuleInfoGo> buildByTemplates(List<TemplateSettingDTO> list, String modulePath) {
                List<ModuleInfoGo> moduleUIInfoList = new ArrayList<>(list.size());
                // 添加列的内容
                for (TemplateSettingDTO templateSettingDTO : list) {
                    ModuleInfoGo item = new ModuleInfoGo();
                    item.setConfigName(templateSettingDTO.getConfigName());
                    // 默认使用实体模块的模块路径
                    item.setModulePath(modulePath);
                    item.setBasePath(templateSettingDTO.getBasePath());
                    item.setFileName(templateSettingDTO.getFileName());
                    item.setFileNameWithSuffix(templateSettingDTO.getFileName() + templateSettingDTO.getSuffix());
                    item.setPackageName(templateSettingDTO.getPackageName());
                    item.setEncoding(templateSettingDTO.getEncoding());
                    item.setConfigFileName(templateSettingDTO.getConfigFile());
                    moduleUIInfoList.add(item);
                }
                return moduleUIInfoList;
            }
        };
    }

    private void initPanel(Map<String, ConfigSetting> templateSettingMap,
                           TableView<ModuleInfoGo> tableView,
                           GridConstraints gridConstraints) {
        templateExtraPanel.add(ToolbarDecorator.createDecorator(tableView)
                .setToolbarPosition(ActionToolbarPosition.LEFT)
                .addExtraAction(new AnActionButton("Refresh Template", PlatformIcons.SYNCHRONIZE_ICON) {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent e) {
                        AbstractButton selectedTemplateName = findSelectedTemplateName();
                        if (selectedTemplateName == null) {
                            return;
                        }
                        ConfigSetting configSetting = templateSettingMap.get(selectedTemplateName.getText());
                        if (configSetting == null) {
                            return;
                        }
                        initSelectedModuleTable(configSetting.getTemplateSettingDTOList(), modelInfo.getModulePath());
                        refresh = true;
                    }

                })
                .disableAddAction()
                .disableUpDownActions()
                .setPreferredSize(new Dimension(840, 150))
                .createPanel(), gridConstraints);
    }

    private String determineTemplateName(Map<String, ConfigSetting> templateSettingMap, String templatesName) {
        // 使用上一次生成代码的模板
        String selectedTemplateName = templatesName;
        // 如果是第一次生成代码, 使用模板列表的第一个模板
        if (selectedTemplateName == null) {
            selectedTemplateName = templateSettingMap.keySet().iterator().next();
        }
        return selectedTemplateName;
    }

    private boolean initRadioTemplates = false;

    private void initRadioLayout(Set<String> strings) {
        if (initRadioTemplates) {
            return;
        }
        // N 行 3 列 的排版模式
        GridLayout templateRadioLayout = new GridLayout(0, 3, 0, 0);
        templateExtraRadiosPanel.setLayout(templateRadioLayout);
        // 添加动态模板组

        for (String templateName : strings) {
            JRadioButton comp = new JRadioButton();
            comp.setText(templateName);
            templateButtonGroup.add(comp);
            templateExtraRadiosPanel.add(comp);
        }
        initRadioTemplates = true;
    }

    private AbstractButton findSelectedTemplateName() {
        AbstractButton selectedButton = null;
        Enumeration<AbstractButton> elements = templateButtonGroup.getElements();
        while (elements.hasMoreElements()) {
            AbstractButton abstractButton = elements.nextElement();
            if (abstractButton.isSelected()) {
                selectedButton = abstractButton;
            }
        }
        return selectedButton;
    }

    private void initMemoryModuleTable(List<ModuleInfoGo> list) {
        // 扩展面板的列表内容
        // 移除所有行, 重新刷新
        for (int rowCount = model.getRowCount(); rowCount > 0; rowCount--) {
            model.removeRow(rowCount - 1);
        }

        // 添加列的内容
        for (ModuleInfoGo item : list) {
            // 忽略为空的设置
            if (item == null) {
                continue;
            }
            // domain 域特殊处理, 不可更改
            if (DOMAIN.equals(item.getConfigName())) {
                ModuleInfoGo itemX = new ModuleInfoGo();
                itemX.setModulePath(modelInfo.getModulePath());
                itemX.setBasePath(modelInfo.getBasePath());
                itemX.setEncoding(modelInfo.getEncoding());
                itemX.setFileName(modelInfo.getFileName());
                itemX.setPackageName(modelInfo.getBasePackage() + "." + modelInfo.getRelativePackage());
                itemX.setFileNameWithSuffix(modelInfo.getFileName() + ".java");
                itemX.setConfigFileName(item.getConfigFileName());
                itemX.setConfigName(DOMAIN);
                item = itemX;
            }
            model.addRow(item);
        }

    }

    /**
     * 初始化默认的模块选择表格
     *
     * @param list
     * @param modulePath
     */
    private void initSelectedModuleTable(List<TemplateSettingDTO> list, String modulePath) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        // 扩展面板的列表内容
        // 移除所有行, 重新刷新
        for (int rowCount = model.getRowCount(); rowCount > 0; rowCount--) {
            model.removeRow(rowCount - 1);
        }

        List<ModuleInfoGo> moduleInfoGoList = new ArrayList<>();
        // 添加列的内容
        for (TemplateSettingDTO templateSettingDTO : list) {
            ModuleInfoGo item = new ModuleInfoGo();
            item.setConfigName(templateSettingDTO.getConfigName());
            // 默认使用实体模块的模块路径
            item.setModulePath(modulePath);
            item.setBasePath(templateSettingDTO.getBasePath());
            item.setFileName(templateSettingDTO.getFileName());
            item.setFileNameWithSuffix(templateSettingDTO.getFileName() + templateSettingDTO.getSuffix());
            item.setPackageName(templateSettingDTO.getPackageName());
            item.setEncoding(templateSettingDTO.getEncoding());
            item.setConfigFileName(templateSettingDTO.getConfigFile());
            moduleInfoGoList.add(item);
        }
        initMemoryModuleTable(moduleInfoGoList);
    }


    private class MybatisModuleInfo extends ColumnInfo<ModuleInfoGo, String> {

        public MybatisModuleInfo(String name, int width, boolean editable) {
            super(name);
            this.width = width;
            this.editable = editable;
        }

        public MybatisModuleInfo(String name, int width, boolean editable, boolean moduleEditor) {
            super(name);
            this.width = width;
            this.editable = editable;
            this.moduleEditor = moduleEditor;
        }

        private final int width;
        private final boolean editable;
        private boolean moduleEditor;

        @Override
        public boolean isCellEditable(ModuleInfoGo moduleUIInfo) {
            return editable;
        }

        @Override
        public int getWidth(JTable table) {
            return width;
        }

        @Nullable
        @Override
        public TableCellRenderer getRenderer(ModuleInfoGo moduleUIInfo) {
            return new DefaultTableCellRenderer();
        }

        private void chooseModule(JTextField textField, ModuleInfoGo moduleUIInfo) {
            Module[] modules = ModuleManager.getInstance(project).getModules();
            ChooseModulesDialog dialog = new ChooseModulesDialog(project, Arrays.asList(modules), "Choose Module", "Choose single module");
            dialog.setSingleSelectionMode();
            dialog.show();

            List<Module> chosenElements = dialog.getChosenElements();
            if (chosenElements.size() > 0) {
                Module module = chosenElements.get(0);
                String modulePath = findModulePath(module);
                textField.setText(modulePath);
                setValue(moduleUIInfo, modulePath);
            }
        }

        @Nullable
        @Override
        public TableCellEditor getEditor(ModuleInfoGo moduleUIInfo) {
            JTextField textField = new JTextField();
            if (moduleEditor) {
                // 模块选择
                textField.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        chooseModule(textField, moduleUIInfo);
                    }
                });
            }

            DefaultCellEditor defaultCellEditor = new DefaultCellEditor(textField);
            defaultCellEditor.addCellEditorListener(new CellEditorListener() {
                @Override
                public void editingStopped(ChangeEvent e) {
                    // domain模板不支持属性的更改
                    if (DOMAIN.equals(moduleUIInfo.getConfigName())) {
                        // do nothing
                        return;
                    }
                    String s = defaultCellEditor.getCellEditorValue().toString();
                    if (getName().equals("module path")) {
                        moduleUIInfo.setModulePath(s);
                    } else if (getName().equals("base path")) {
                        moduleUIInfo.setBasePath(s);
                    } else if (getName().equals("package name")) {
                        moduleUIInfo.setPackageName(s);
                    }
                }

                @Override
                public void editingCanceled(ChangeEvent e) {

                }
            });
            return defaultCellEditor;
        }

        private String findModulePath(Module module) {
            String moduleDirPath = ModuleUtil.getModuleDirPath(module);
            int ideaIndex = moduleDirPath.indexOf(".idea");
            if (ideaIndex > -1) {
                moduleDirPath = moduleDirPath.substring(0, ideaIndex);
            }
            return moduleDirPath;
        }

        @Nullable
        @Override
        public String valueOf(ModuleInfoGo item) {
            if (item == null) {
                return "";
            }
            String value = null;
            if (getName().equals("config name")) {
                value = item.getConfigName();
            } else if (getName().equals("module path")) {
                value = DomainPlaceHolder.replace(item.getModulePath(), modelInfo);
                // domain 配置不可以更改模块
//                if (item.getConfigName().equals("domain")) {
//                    value = domainInfo.getModulePath();
//                }
            } else if (getName().equals("base path")) {
                value = DomainPlaceHolder.replace(item.getBasePath(), modelInfo);
                // domain 配置不可以基础路径
//                if (item.getConfigName().equals("domain")) {
//                    value = domainInfo.getBasePath();
//                }
            } else if (getName().equals("package name")) {
                value = DomainPlaceHolder.replace(item.getPackageName(), modelInfo);
                // domain 配置不可以更改相对路径
//                if (item.getConfigName().equals("domain")) {
//                    value = domainInfo.getBasePackage() + "." + domainInfo.getRelativePackage();
//                }
            }

            return value;
        }
    }

    public void refreshGenerateConfig(MybatisGeneratorProperties mybatisGeneratorProperties) {

        List<ModuleInfoGo> moduleUIInfoList = IntStream.range(0, model.getRowCount())
                .mapToObj(index -> model.getRowValue(index))
                .collect(Collectors.toList());
        mybatisGeneratorProperties.setModuleUIInfoList(moduleUIInfoList);

        mybatisGeneratorProperties.setNeedsComment(commentCheckBox.isSelected());
        mybatisGeneratorProperties.setNeedsModel(needsModelCheckBox.isSelected());
        mybatisGeneratorProperties.setNeedToStringHashcodeEquals(toStringHashCodeEqualsCheckBox.isSelected());
        mybatisGeneratorProperties.setUseLombokPlugin(lombokCheckBox.isSelected());
        mybatisGeneratorProperties.setUseActualColumns(actualColumnCheckBox.isSelected());
        mybatisGeneratorProperties.setUseJsr303(JSR303AnnotationCheckBox.isSelected());
        mybatisGeneratorProperties.setUseJsr310(JSR310DateAPICheckBox.isSelected());
        mybatisGeneratorProperties.setUseSwagger2Plugin(swagger2AnnotationRadioButton.isSelected());
        mybatisGeneratorProperties.setUseSwagger3Plugin(swagger3AnnotationRadioButton.isSelected());

        String templatesName = null;
        final Enumeration<AbstractButton> elements = templateButtonGroup.getElements();
        while (elements.hasMoreElements()) {
            final AbstractButton abstractButton = elements.nextElement();
            if (abstractButton.isSelected()) {
                templatesName = abstractButton.getText();
                break;
            }
        }
        mybatisGeneratorProperties.setTemplatesName(templatesName);

    }

}
