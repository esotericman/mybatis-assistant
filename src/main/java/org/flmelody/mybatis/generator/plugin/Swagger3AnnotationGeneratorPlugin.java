package org.flmelody.mybatis.generator.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;

/**
 * plugin for generating swagger3 annotations-implements open api 3.0
 *
 * @author flmelody
 */
public class Swagger3AnnotationGeneratorPlugin implements AnnotationGeneratorPlugin {
    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType(new FullyQualifiedJavaType("io.swagger.v3.oas.annotations.media.Schema"));
        topLevelClass.addAnnotation("@Schema(description = \"" + introspectedTable.getRemarks() + "\")");
    }

    @Override
    public void addFiledAnnotation(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        String remarks = introspectedColumn.getRemarks();
        field.addAnnotation("@Schema(description = \"" + remarks + "\")");
    }
}
