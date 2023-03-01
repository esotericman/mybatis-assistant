package org.flmelody.mybatis.generator.plugin.helper;


import lombok.Data;

@Data
public class IntellijColumnInfo {
    private String name;
    private int dataType;
    private boolean generatedColumn;
    private boolean autoIncrement;
    private int size;
    private int decimalDigits;
    private String remarks;
    private String columnDefaultValue;
    private Boolean nullable;
    private short keySeq;

    public IntellijColumnInfo() {
    }
}
