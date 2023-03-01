package org.flmelody.mybatis.setting;

import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * The type Mybatis configurable.
 *
 * @author yanglin
 */
public class MybatisConfigurable implements SearchableConfigurable {

    private final MybatisSettings mybatisSettings;

    private MybatisSettingForm mybatisSettingForm;

    /**
     * Instantiates a new Mybatis configurable.
     */
    public MybatisConfigurable() {
        mybatisSettings = MybatisSettings.getInstance();
    }

    @Override
    @NotNull
    public String getId() {
        return "Mybatis";
    }

    @Override
    public Runnable enableSearch(String option) {
        return null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return getId();
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return getId();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (null == mybatisSettingForm) {
            this.mybatisSettingForm = new MybatisSettingForm();
        }
        return mybatisSettingForm.mainPanel;
    }

    @Override
    public boolean isModified() {
        return  !mybatisSettings.getInsertGenerator().equals(mybatisSettingForm.insertPatternTextField.getText())
            || !mybatisSettings.getDeleteGenerator().equals(mybatisSettingForm.deletePatternTextField.getText())
            || !mybatisSettings.getUpdateGenerator().equals(mybatisSettingForm.updatePatternTextField.getText())
            || !mybatisSettings.getSelectGenerator().equals(mybatisSettingForm.selectPatternTextField.getText())
            || (mybatisSettingForm.defaultRadioButton.isSelected() ?
            MybatisSettings.MapperIcon.BIRD.name().equals(mybatisSettings.getMapperIcon())
            : MybatisSettings.MapperIcon.DEFAULT.name().equals(mybatisSettings.getMapperIcon()));
    }

    @Override
    public void apply() {
        mybatisSettings.setInsertGenerator(mybatisSettingForm.insertPatternTextField.getText());
        mybatisSettings.setDeleteGenerator(mybatisSettingForm.deletePatternTextField.getText());
        mybatisSettings.setUpdateGenerator(mybatisSettingForm.updatePatternTextField.getText());
        mybatisSettings.setSelectGenerator(mybatisSettingForm.selectPatternTextField.getText());

        String mapperIcon = mybatisSettingForm.defaultRadioButton.isSelected() ?
            MybatisSettings.MapperIcon.DEFAULT.name() :
            MybatisSettings.MapperIcon.BIRD.name();
        mybatisSettings.setMapperIcon(mapperIcon);
    }

    @Override
    public void reset() {
        mybatisSettingForm.insertPatternTextField.setText(mybatisSettings.getInsertGenerator());
        mybatisSettingForm.deletePatternTextField.setText(mybatisSettings.getDeleteGenerator());
        mybatisSettingForm.updatePatternTextField.setText(mybatisSettings.getUpdateGenerator());
        mybatisSettingForm.selectPatternTextField.setText(mybatisSettings.getSelectGenerator());

        JRadioButton jRadioButton =  mybatisSettingForm.birdRadioButton;
        if (MybatisSettings.MapperIcon.DEFAULT.name().equals(mybatisSettings.getMapperIcon())) {
            jRadioButton = mybatisSettingForm.defaultRadioButton;
        }
        jRadioButton.setSelected(true);
    }

    @Override
    public void disposeUIResources() {
        mybatisSettingForm.mainPanel = null;
    }

}
