package org.flmelody.mybatis.generator.template;

import lombok.Getter;
import org.flmelody.mybatis.generator.pojo.FieldInfo;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 实体类的信息
 */
@Getter
public class ClassMetadata {
    /**
     * 类的全称(包括包名)
     */
    private String fullClassName;
    /**
     * 类的简称
     */
    private String shortClassName;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 表的注释
     */
    private String remark;
    /**
     * 主键字段列表
     */
    private List<FieldInfo> pkFields;
    /**
     * 全部字段
     */
    private List<FieldInfo> allFields;
    /**
     * 除了主键的所有字段
     */
    private List<FieldInfo> baseFields;
    /**
     * 所有的blob字段
     */
    private List<FieldInfo> baseBlobFields;
    /**
     * 需要导入的实体类的所有字段类型
     */
    private List<String> importList;

    public static ClassMetadata build(IntrospectedTable introspectedTable) {
        ClassMetadata classMetadata = new ClassMetadata();
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        classMetadata.fullClassName = introspectedTable.getBaseRecordType();
        classMetadata.shortClassName = type.getShortName();
        classMetadata.tableName = introspectedTable.getFullyQualifiedTable().getIntrospectedTableName();
        classMetadata.remark = introspectedTable.getRemarks() == null ? "" : introspectedTable.getRemarks();

        classMetadata.pkFields = introspectedTable.getPrimaryKeyColumns()
                .stream()
                .map(FieldInfo::build)
                .collect(Collectors.toList());

        classMetadata.allFields = Stream.of(introspectedTable.getPrimaryKeyColumns(),
                        introspectedTable.getBaseColumns(),
                        introspectedTable.getBLOBColumns())
                .flatMap(Collection::stream)
                .map(FieldInfo::build)
                .collect(Collectors.toList());

        classMetadata.baseFields = introspectedTable.getBaseColumns().stream()
                .map(FieldInfo::build)
                .collect(Collectors.toList());

        classMetadata.baseBlobFields = Stream.of(introspectedTable.getBaseColumns(),
                        introspectedTable.getBLOBColumns())
                .flatMap(Collection::stream)
                .map(FieldInfo::build)
                .collect(Collectors.toList());
        // 拿到所有需要import的类型, 不是java.lang包开头的,并且不是数组类型 去重的所有类型
        classMetadata.importList = classMetadata.allFields.stream()
                .filter(fieldInfo -> !fieldInfo.isColumnIsArray())
                .map(FieldInfo::getFullTypeName)
                .filter(typeName -> !typeName.startsWith("java.lang"))
                .distinct()
                .collect(Collectors.toList());
        return classMetadata;
    }
}
