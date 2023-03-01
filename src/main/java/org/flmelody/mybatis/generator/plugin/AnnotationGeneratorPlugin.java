package org.flmelody.mybatis.generator.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.TopLevelClass;

/**
 * annotation plugin,used to generate annotations for model class and field
 *
 * @author flmelody
 * @see JSR303AnnotationGeneratorPlugin
 * @see Swagger2AnnotationGeneratorPlugin
 * @see Swagger3AnnotationGeneratorPlugin
 */
public interface AnnotationGeneratorPlugin {

    /**
     * add comment and annotation for class
     *
     * @param topLevelClass     topLevelClass
     * @param introspectedTable introspectedTable
     */
    void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable);

    /**
     * add annotation for field
     *
     * @param field              field
     * @param introspectedTable  introspectedTable
     * @param introspectedColumn introspectedColumn
     */
    void addFiledAnnotation(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn);
}
