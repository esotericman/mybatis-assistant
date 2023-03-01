package org.flmelody.mybatis.util;

import com.intellij.openapi.util.IconLoader;
import com.intellij.util.PlatformIcons;
import com.intellij.util.ReflectionUtil;

import javax.swing.*;
import java.util.Objects;

/**
 * The interface Icons.
 *
 * @author yanglin
 */
public interface Icons {

    /**
     * The constant MYBATIS_LOGO.
     */
    Icon MYBATIS_LOGO = IconLoader.getIcon("/javaee/persistenceId.png", Icons.class);

    /**
     * The constant PARAM_COMPLETION_ICON.
     */
    Icon PARAM_COMPLETION_ICON = PlatformIcons.PARAMETER_ICON;
    /**
     * The constant MAPPER_LINE_MARKER_ICON.
     * mapper.xml文件中的方法左边的提示图标
     */
    Icon MAPPER_LINE_MARKER_ICON = IconLoader.getIcon("/images/mapper_method.svg", Icons.class);
    /**
     * The constant STATEMENT_LINE_MARKER_ICON.
     * mapper类文件中的方法左边的提示图标
     */
    Icon STATEMENT_LINE_MARKER_ICON = IconLoader.getIcon("/images/statement.svg", Icons.class);
    /**
     * The constant MAPPER_XML_ICON.
     * mapper.xml 文件的icon
     */
    Icon MAPPER_XML_ICON = MAPPER_LINE_MARKER_ICON;
    /**
     * The constant MAPPER_CLASS_ICON.
     * mapper 类文件的icon
     */
    Icon MAPPER_CLASS_ICON = STATEMENT_LINE_MARKER_ICON;

    /**
     * The constant SPRING_INJECTION_ICON.
     * 锤子不好看, 就用代表mapper文件的图标好了
     * Icon SPRING_INJECTION_ICON = IconLoader.getIcon("/images/injection.png");
     */
    Icon SPRING_INJECTION_ICON = MAPPER_CLASS_ICON;
}
