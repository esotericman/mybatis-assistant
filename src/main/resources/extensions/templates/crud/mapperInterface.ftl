package ${mapperInterface.packageName};

import ${tableClass.fullClassName};
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
* @author ${author!}
* @see ${tableClass.fullClassName}
* created at ${.now?string('yyyy-MM-dd HH:mm:ss')}
*/
@Repository
public interface ${mapperInterface.fileName} {
    /**
    * 根据主键删除数据记录
    *
    * @param id 标识
    * @return 影响数据记录数
    */
    int deleteByPrimaryKey(<#if (tableClass.pkFields?size==1)>${tableClass.pkFields[0].shortTypeName}</#if> id);

    /**
    * 插入数据记录
    *
    * @param record 数据记录
    * @return 影响数据记录数
    */
    int insert(${tableClass.shortClassName} record);

    /**
    * 插入数据记录（只插入非Null字段）
    *
    * @param record 数据记录
    * @return 影响数据记录数
    */
    int insertSelective(${tableClass.shortClassName} record);

    /**
    * 根据主键查找数据记录
    *
    * @param id 标识
    * @return 数据记录
    */
    ${tableClass.shortClassName} selectByPrimaryKey(<#if (tableClass.pkFields?size==1)>${tableClass.pkFields[0].shortTypeName}</#if> id);

    /**
    * 根据主键更新数据记录（只更新非Null字段）
    *
    * @param record 数据记录
    * @return 影响数据记录数
    */
    int updateByPrimaryKeySelective(${tableClass.shortClassName} record);

    /**
    * 根据主键更新数据记录
    *
    * @param record 数据记录
    * @return 影响数据记录数
    */
    int updateByPrimaryKey(${tableClass.shortClassName} record);

}
