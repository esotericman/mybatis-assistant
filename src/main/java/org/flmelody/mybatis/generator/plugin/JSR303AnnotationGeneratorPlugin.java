package org.flmelody.mybatis.generator.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.sql.Types;

/**
 * plugin for generating JSR-303 validation annotations
 *
 * @author flmelody
 */
public class JSR303AnnotationGeneratorPlugin implements AnnotationGeneratorPlugin {
    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType(new FullyQualifiedJavaType("javax.validation.constraints.*"));
    }

    @Override
    public void addFiledAnnotation(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        String remarks = introspectedColumn.getRemarks();
        if (!introspectedColumn.isNullable()) {
            if (!introspectedColumn.isIdentity()) {
                field.addAnnotation("@NotNull(message = \"" + remarks + "不能为空\")");
            }
        }

        if (introspectedColumn.isStringColumn()) {
            field.addAnnotation("@Size(max = " + introspectedColumn
                    .getLength() + ", message = \"" + remarks + "的长度不能超过{max}\")");
        }

        if (introspectedColumn.getJdbcType() == Types.DOUBLE || introspectedColumn.getJdbcType() == Types.FLOAT
                || introspectedColumn.getJdbcType() == Types.NUMERIC || introspectedColumn.getJdbcType() == Types.DECIMAL) {
            // 整数位数
            int integerDigits = introspectedColumn.getLength() - introspectedColumn.getScale();

            field.addAnnotation("@Digits(integer = " + integerDigits + ", fraction = " + introspectedColumn
                    .getScale() + ", message = \"" + remarks + "为不合理数值\")");
        }
    }
}
