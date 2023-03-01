package org.flmelody.mybatis.generator;

import org.flmelody.mybatis.generator.plugin.AnnotationGeneratorPlugin;
import org.flmelody.mybatis.generator.plugin.JSR303AnnotationGeneratorPlugin;
import org.flmelody.mybatis.generator.plugin.Swagger2AnnotationGeneratorPlugin;
import org.flmelody.mybatis.generator.plugin.Swagger3AnnotationGeneratorPlugin;
import org.flmelody.mybatis.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.internal.DefaultCommentGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author all contributed people
 */
public class MybatisCommentGenerator extends DefaultCommentGenerator implements CommentGenerator {
    static final String USE_COMMENT = "useComment";
    static final String USE_JSR303_PLUGIN = "useJsr303Plugin";
    static final String USE_SWAGGER2_PLUGIN = "useSwagger2Plugin";
    static final String USE_SWAGGER3_PLUGIN = "useSwagger3Plugin";
    private Boolean needsComment;
    private final List<AnnotationGeneratorPlugin> annotationGeneratorPluginList = new ArrayList<>();

    public MybatisCommentGenerator() {

    }

    @Override
    public void addJavaFileComment(CompilationUnit compilationUnit) {

    }

    @Override
    public void addConfigurationProperties(Properties properties) {
        super.addConfigurationProperties(properties);
        this.needsComment = determineProperty(properties, USE_COMMENT);
        if (determineProperty(properties, USE_JSR303_PLUGIN)) {
            annotationGeneratorPluginList.add(new JSR303AnnotationGeneratorPlugin());
        }
        if (determineProperty(properties, USE_SWAGGER2_PLUGIN)) {
            annotationGeneratorPluginList.add(new Swagger2AnnotationGeneratorPlugin());
        } else if (determineProperty(properties, USE_SWAGGER3_PLUGIN)) {
            annotationGeneratorPluginList.add(new Swagger3AnnotationGeneratorPlugin());
        }
    }

    @NotNull
    private Boolean determineProperty(Properties properties, String propertyName) {
        String property = properties.getProperty(propertyName);
        if (property == null) {
            property = "false";
        }
        return Boolean.valueOf(property);
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
    }

    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        topLevelClass.addJavaDocLine("/**");
        // 加入表注释
        if (needsComment) {
            String remarks = introspectedTable.getRemarks();
            if (remarks == null) {
                remarks = "";
            }
            topLevelClass.addJavaDocLine(" * " + remarks);
        }
        topLevelClass.addJavaDocLine(" */");
        annotationGeneratorPluginList.forEach(annotationGeneratorPlugin -> annotationGeneratorPlugin.addModelClassComment(topLevelClass, introspectedTable));
    }


    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        if (needsComment) {
            method.addJavaDocLine("/**");
            String remarks = introspectedColumn.getRemarks();
            if (StringUtils.isEmpty(remarks)) {
                remarks = "";
            }
            method.addJavaDocLine(" * " + remarks);

            method.addJavaDocLine(" */");
        }

    }

    @Override
    public void addComment(XmlElement xmlElement) {
        if (needsComment) {
            xmlElement.addElement(new TextElement("<!--" + MergeConstants.NEW_ELEMENT_TAG + "-->"));
        }

    }

    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
    }

    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        if (needsComment) {
            method.addJavaDocLine("/**");
            String remarks = introspectedColumn.getRemarks();
            if (StringUtils.isEmpty(remarks)) {
                remarks = "";
            }
            method.addJavaDocLine(" * " + remarks);

            method.addJavaDocLine(" */");
        }
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        if (needsComment) {
            field.addJavaDocLine("/**");
            String remarks = introspectedColumn.getRemarks();
            if (StringUtils.isEmpty(remarks)) {
                remarks = "";
            }
            field.addJavaDocLine(" * " + remarks);
            field.addJavaDocLine(" */");
        }

    }


    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
        if (needsComment) {
            innerClass.addJavaDocLine("/**");
            innerClass.addJavaDocLine(" */");
        }

    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
        if (needsComment) {
            field.addJavaDocLine("/**");
            String remarks = field.getName();
            if (StringUtils.isEmpty(remarks)) {
                remarks = "";
            }
            field.addJavaDocLine(" * " + remarks);
            field.addJavaDocLine(" */");
        }
    }

    @Override
    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> imports) {
    }

    @Override
    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> imports) {
        addSetterComment(method, introspectedTable, introspectedColumn);
    }

    @Override
    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> imports) {
        addFieldComment(field, introspectedTable, introspectedColumn);
        annotationGeneratorPluginList.forEach(annotationGeneratorPlugin -> annotationGeneratorPlugin.addFiledAnnotation(field, introspectedTable, introspectedColumn));
    }


}
