package org.flmelody.mybatis.generator.plugin.helper;


import lombok.Data;

import java.util.List;

/**
 * @author all contributed people
 */
@Data
public class IntellijTableInfo {
    private String tableName;
    private String databaseType;
    private String tableRemark;
    private String tableType;
    private List<IntellijColumnInfo> columnInfos;
    private List<IntellijColumnInfo> primaryKeyColumns;

    public IntellijTableInfo() {
    }
}
