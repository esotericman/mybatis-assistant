package org.flmelody.mybatis.generator.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;

/**
 * plugin for generating swagger2 annotations
 *
 * @author flmelody
 */
public class Swagger2AnnotationGeneratorPlugin implements AnnotationGeneratorPlugin {
    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType(new FullyQualifiedJavaType("io.swagger.annotations.ApiModel"));
        topLevelClass.addImportedType(new FullyQualifiedJavaType("io.swagger.annotations.ApiModelProperty"));
        topLevelClass.addAnnotation("@ApiModel(description = \"" + introspectedTable.getRemarks() + "\")");
    }

    @Override
    public void addFiledAnnotation(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        String remarks = introspectedColumn.getRemarks();
        field.addAnnotation("@ApiModelProperty(value = \"" + remarks + "\")");
    }
}
