package org.flmelody.mybatis.generator.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author :ls9527
 * @date : 2021/6/30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableUIInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 类名
     */
    private String className;
}
