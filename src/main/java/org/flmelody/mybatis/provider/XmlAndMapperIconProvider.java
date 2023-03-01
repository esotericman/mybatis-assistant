package org.flmelody.mybatis.provider;

import com.intellij.ide.IconProvider;
import com.intellij.lang.Language;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import org.flmelody.mybatis.dom.model.Mapper;
import org.flmelody.mybatis.setting.MybatisSettings;
import org.flmelody.mybatis.util.Icons;
import org.flmelody.mybatis.util.MapperUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;

/**
 * mapper.xml 和 mapperClass 的文件图标修改为骚气的小鸟
 */
public class XmlAndMapperIconProvider extends IconProvider {
    private volatile MybatisSettings instance;

    @NotNull
    private MybatisSettings getInstance() {
        if (instance == null) {
            synchronized (XmlAndMapperIconProvider.class) {
                if (instance == null) {
                    instance = MybatisSettings.getInstance();
                }
            }
        }
        return instance;
    }

    @Override
    public @Nullable
    Icon getIcon(@NotNull PsiElement element, int flags) {
        if (isDefaultIcon()) {
            return null;
        }
        if (isMapperClass(element)) {
            return Icons.MAPPER_CLASS_ICON;
        }
        if (MapperUtils.isElementWithinMybatisFile(element)) {
            return Icons.MAPPER_XML_ICON;
        }
        return null;
    }

    /**
     * XML 和 Mapper 都使用默认图标
     *
     * @return
     */
    private boolean isDefaultIcon() {
        return getInstance().getMapperIcon() != null &&
            MybatisSettings.MapperIcon.DEFAULT.name()
                .equals(getInstance().getMapperIcon());
    }

    private boolean isMapperClass(@NotNull PsiElement element) {
        Language language = element.getLanguage();
        if (!language.isKindOf(JavaLanguage.INSTANCE)) {
            return false;
        }
        if (!(element instanceof PsiClass)) {
            return false;
        }
        PsiClass mayMapperClass = (PsiClass) element;
        if (!mayMapperClass.isInterface()) {
            return false;
        }

        Collection<Mapper> mappers = MapperUtils.findMappers(element.getProject(), mayMapperClass);
        return mappers.size() > 0;
    }


}
