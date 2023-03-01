package org.flmelody.mybatis.generator.ui;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ui.configuration.ChooseModulesDialog;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.TableView;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import org.flmelody.mybatis.generator.util.CamelStrategy;
import org.flmelody.mybatis.generator.util.ClassNameStrategy;
import org.flmelody.mybatis.generator.util.UnchangedStrategy;
import org.flmelody.mybatis.generator.pojo.ModelInfo;
import org.flmelody.mybatis.generator.pojo.MybatisGeneratorProperties;
import org.flmelody.mybatis.generator.pojo.TableUIInfo;
import org.flmelody.mybatis.util.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * @author all contributed people
 */
public class TablePreviewUI {
    private JPanel rootPanel;
    private JPanel listPanel;
    private JTextField ignoreTablePrefixTextField;
    private JTextField ignoreTableSuffixTextField;
    private JTextField fieldPrefixTextField;
    private JTextField fieldSuffixTextField;
    private JTextField superClassTextField;
    private JTextField encodingTextField;
    private JTextField basePackageTextField;
    private JTextField relativePackageTextField;
    private JTextField basePathTextField;
    private JTextField moduleChooseTextField;
    private JRadioButton camelRadioButton;
    private JRadioButton unchangedRadioButton;
    private JTextField classNamePrefixTextField;
    private JTextField classNameSuffixTextField;
    private final ButtonGroup classNameStrategyButtonGroup = new ButtonGroup();
    private PsiElement[] tableElements;
    private List<DbTable> dbTables;
    private String moduleName;

    ListTableModel<TableUIInfo> model = new ListTableModel<>(
            new MybaitsTableColumnInfo("tableName", false),
            new MybaitsTableColumnInfo("className", true)
    );

    public TablePreviewUI() {
        TableView<TableUIInfo> tableView = new TableView<>(model);
        GridConstraints gridConstraints = new GridConstraints();
        gridConstraints.setFill(GridConstraints.FILL_HORIZONTAL);

        listPanel.add(ToolbarDecorator.createDecorator(tableView)
                        .setPreferredSize(new Dimension(860, 200))
                        .disableAddAction()
                        .disableRemoveAction()
                        .disableUpDownActions()
                        .createPanel(),
                gridConstraints);


    }


    public ModelInfo buildDomainInfo() {
        ModelInfo modelInfo = new ModelInfo();
        modelInfo.setModulePath(moduleChooseTextField.getText());
        modelInfo.setBasePath(basePathTextField.getText());
        modelInfo.setBasePackage(basePackageTextField.getText());
        modelInfo.setRelativePackage(relativePackageTextField.getText());
        modelInfo.setEncoding(encodingTextField.getText());
        // 放一个自己名字的引用
        modelInfo.setFileName("${model.fileName}");
        return modelInfo;

    }


    public JPanel getRootPanel() {
        return rootPanel;
    }

    public void fillData(Project project, List<DbTable> dbTables, MybatisGeneratorProperties mybatisGeneratorProperties) {
        this.dbTables = dbTables;
        String ignorePrefix = mybatisGeneratorProperties.getIgnoredTablePrefix();
        String ignoreSuffix = mybatisGeneratorProperties.getIgnoredTableSuffix();

        classNameStrategyButtonGroup.add(camelRadioButton);
        classNameStrategyButtonGroup.add(unchangedRadioButton);

        String classNameStrategy = mybatisGeneratorProperties.getClassNameStrategy();
        selectClassNameStrategyByName(findStrategyByName(classNameStrategy));
        refreshTableNames(classNameStrategy, dbTables, ignorePrefix, ignoreSuffix);

        ignoreTablePrefixTextField.setText(mybatisGeneratorProperties.getIgnoredTablePrefix());
        ignoreTableSuffixTextField.setText(mybatisGeneratorProperties.getIgnoredTableSuffix());
        fieldPrefixTextField.setText(mybatisGeneratorProperties.getIgnoredColumnPrefix());
        fieldSuffixTextField.setText(mybatisGeneratorProperties.getIgnoredColumnSuffix());
        superClassTextField.setText(mybatisGeneratorProperties.getSuperClass());
        encodingTextField.setText(mybatisGeneratorProperties.getEncoding());
        basePackageTextField.setText(mybatisGeneratorProperties.getBasePackage());
        basePathTextField.setText(mybatisGeneratorProperties.getBasePath());
        relativePackageTextField.setText(mybatisGeneratorProperties.getRelativePackage());
        classNamePrefixTextField.setText(mybatisGeneratorProperties.getClassNamePrefix());
        classNameSuffixTextField.setText(mybatisGeneratorProperties.getClassNameSuffix());
        moduleName = mybatisGeneratorProperties.getModuleName();

        if (!StringUtils.isEmpty(moduleName)) {
            Module[] modules = ModuleManager.getInstance(project).getModules();
            for (Module module : modules) {
                if (module.getName().equals(moduleName)) {
                    chooseModulePath(module);
                }
            }
        }

        moduleChooseTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                chooseModule(project);
            }
        });

        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refreshTableNames(classNameStrategy, dbTables, ignoreTablePrefixTextField.getText(), ignoreTableSuffixTextField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refreshTableNames(classNameStrategy, dbTables, ignoreTablePrefixTextField.getText(), ignoreTableSuffixTextField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refreshTableNames(classNameStrategy, dbTables, ignoreTablePrefixTextField.getText(), ignoreTableSuffixTextField.getText());
            }
        };

        final ItemListener classNameChangeListener = e -> {
            final Object source = e.getItem();
            if (!(source instanceof JRadioButton)) {
                return;
            }
            JRadioButton radioButton = (JRadioButton) source;
            if (!radioButton.isSelected()) {
                return;
            }
            refreshTableNames(findClassNameStrategy(), dbTables, ignorePrefix, ignoreSuffix);
        };
        camelRadioButton.addItemListener(classNameChangeListener);
        unchangedRadioButton.addItemListener(classNameChangeListener);

        ignoreTablePrefixTextField.getDocument().addDocumentListener(listener);

        ignoreTableSuffixTextField.getDocument().addDocumentListener(listener);
    }


    private void refreshTableNames(String classNameStrategyName, List<DbTable> dbTables, String ignorePrefix, String ignoreSuffix) {
        for (int currentRowIndex = model.getRowCount() - 1; currentRowIndex >= 0; currentRowIndex--) {
            model.removeRow(currentRowIndex);
        }
        ClassNameStrategy classNameStrategy = findStrategyByName(classNameStrategyName);
        for (DbTable dbTable : dbTables) {
            String tableName = dbTable.getName();
            String className = classNameStrategy.calculateClassName(tableName, ignorePrefix, ignoreSuffix);
            model.addRow(new TableUIInfo(tableName, className));
        }
    }

    List<ClassNameStrategy> classNameStrategies = new ArrayList<>() {
        {
            add(new CamelStrategy());
            add(new UnchangedStrategy());
        }
    };

    private ClassNameStrategy findStrategyByName(String classNameStrategyName) {
        ClassNameStrategy classNameStrategy = null;
        for (ClassNameStrategy nameStrategy : classNameStrategies) {
            if (nameStrategy.getText().equals(classNameStrategyName)) {
                classNameStrategy = nameStrategy;
                break;
            }
        }
        // 策略为空, 或者不是SAME的, 统统都是驼峰命名
        if (classNameStrategy == null) {
            classNameStrategy = new CamelStrategy();
        }
        return classNameStrategy;
    }

    private void chooseModule(Project project) {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        ChooseModulesDialog dialog = new ChooseModulesDialog(project, Arrays.asList(modules), "Choose Module", "Choose single module");
        dialog.setSingleSelectionMode();
        dialog.show();

        List<Module> chosenElements = dialog.getChosenElements();
        if (chosenElements.size() > 0) {
            Module module = chosenElements.get(0);
            chooseModulePath(module);
            moduleName = module.getName();
        }
    }

    private void chooseModulePath(Module module) {

        String moduleDirPath = ModuleUtil.getModuleDirPath(module);
        int childModuleIndex = indexFromChildModule(moduleDirPath);
        if (hasChildModule(childModuleIndex)) {
            Optional<String> pathFromModule = getPathFromModule(module);
            if (pathFromModule.isPresent()) {
                moduleDirPath = pathFromModule.get();
            } else {
                moduleDirPath = moduleDirPath.substring(0, childModuleIndex);
            }
        }

        moduleChooseTextField.setText(moduleDirPath);
    }

    private boolean hasChildModule(int childModuleIndex) {
        return childModuleIndex > -1;
    }

    private int indexFromChildModule(String moduleDirPath) {
        return moduleDirPath.indexOf(".idea");
    }

    private Optional<String> getPathFromModule(Module module) {
        // 兼容gradle获取子模块
        VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
        if (contentRoots.length == 1) {
            return Optional.of(contentRoots[0].getPath());
        }
        return Optional.empty();
    }

    public void refreshGenerateConfig(MybatisGeneratorProperties mybatisGeneratorProperties) {
        mybatisGeneratorProperties.setIgnoredTablePrefix(ignoreTablePrefixTextField.getText());
        mybatisGeneratorProperties.setIgnoredTableSuffix(ignoreTableSuffixTextField.getText());
        mybatisGeneratorProperties.setIgnoredColumnPrefix(fieldPrefixTextField.getText());
        mybatisGeneratorProperties.setIgnoredColumnSuffix(fieldSuffixTextField.getText());
        mybatisGeneratorProperties.setSuperClass(superClassTextField.getText());
        mybatisGeneratorProperties.setEncoding(encodingTextField.getText());
        mybatisGeneratorProperties.setBasePackage(basePackageTextField.getText());
        mybatisGeneratorProperties.setBasePath(basePathTextField.getText());
        mybatisGeneratorProperties.setRelativePackage(relativePackageTextField.getText());
        mybatisGeneratorProperties.setModulePath(moduleChooseTextField.getText());
        mybatisGeneratorProperties.setModuleName(moduleName);
        mybatisGeneratorProperties.setClassNamePrefix(classNamePrefixTextField.getText());
        mybatisGeneratorProperties.setClassNameSuffix(classNameSuffixTextField.getText());
        mybatisGeneratorProperties.setClassNameStrategy(findClassNameStrategy());
        // 保存对象, 用于传递和对象生成
        mybatisGeneratorProperties.setTableUIInfoList(model.getItems());
    }

    private void selectClassNameStrategyByName(ClassNameStrategy classNameStrategy) {
        Enumeration<AbstractButton> elements = classNameStrategyButtonGroup.getElements();
        while (elements.hasMoreElements()) {
            AbstractButton abstractButton = elements.nextElement();
            if (abstractButton.getText().equals(classNameStrategy.getText())) {
                abstractButton.setSelected(true);
                break;
            }
        }
    }

    private String findClassNameStrategy() {
        String name = null;
        Enumeration<AbstractButton> elements = classNameStrategyButtonGroup.getElements();
        while (elements.hasMoreElements()) {
            AbstractButton abstractButton = elements.nextElement();
            if (abstractButton.isSelected()) {
                name = abstractButton.getText();
                break;
            }
        }
        return name;
    }

    private static class MybaitsTableColumnInfo extends ColumnInfo<TableUIInfo, String> {

        private final boolean editable;

        public MybaitsTableColumnInfo(String name, boolean editable) {
            super(name);
            this.editable = editable;
        }

        @Override
        public boolean isCellEditable(TableUIInfo tableUIInfo) {
            return editable;
        }

        @Nullable
        @Override
        public TableCellEditor getEditor(TableUIInfo tableUIInfo) {
            DefaultCellEditor defaultCellEditor = new DefaultCellEditor(new JTextField(getName()));
            defaultCellEditor.addCellEditorListener(new CellEditorListener() {
                @Override
                public void editingStopped(ChangeEvent e) {
                    Object cellEditorValue = defaultCellEditor.getCellEditorValue();
                    tableUIInfo.setClassName(cellEditorValue.toString());
                }

                @Override
                public void editingCanceled(ChangeEvent e) {

                }
            });
            return defaultCellEditor;
        }

        @Nullable
        @Override
        public String valueOf(TableUIInfo item) {
            String value = null;
            if (getName().equals("tableName")) {
                value = item.getTableName();
            } else if (getName().equals("className")) {
                value = item.getClassName();
            }
            return value;
        }
    }
}
