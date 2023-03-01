package ${baseInfo.packageName};

import ${tableClass.fullClassName};
import ${serviceInterface.packageName}.${serviceInterface.fileName};
import ${mapperInterface.packageName}.${mapperInterface.fileName};

import org.springframework.stereotype.Service;

/**
* @author ${author!}
* used for table【${tableClass.tableName}<#if tableClass.remark?has_content>(${tableClass.remark!})</#if>】service
* created at ${.now?string('yyyy-MM-dd HH:mm:ss')}
*/
@Service
public class ${baseInfo.fileName} implements ${serviceInterface.fileName}{

}




