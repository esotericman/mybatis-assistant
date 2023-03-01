package org.flmelody.mybatis.dom.description;


import com.intellij.util.xml.DomFileDescription;
import org.flmelody.mybatis.dom.model.Mapper;

/**
 * <p>
 * mapper.xml 文件属性提示
 * </p>
 *
 * @author yanglin jobob
 * @since 2018 -07-30
 */
public class MapperDescription extends DomFileDescription<Mapper> {

    public static final String [] HTTP_MYBATIS_ORG_DTD_MYBATIS_3_MAPPER_DTD =
        new String[]{"http://mybatis.org/dtd/mybatis-3-mapper.dtd",
            "http://www.mybatis.org/dtd/mybatis-3-mapper.dtd",
            "https://mybatis.org/dtd/mybatis-3-mapper.dtd",
            "https://www.mybatis.org/dtd/mybatis-3-mapper.dtd"
    };

    /**
     * Instantiates a new Mapper description.
     */
    public MapperDescription() {
        super(Mapper.class, "mapper");
    }

    @Override
    protected void initializeFileDescription() {
        registerNamespacePolicy("MybatisXml",
            HTTP_MYBATIS_ORG_DTD_MYBATIS_3_MAPPER_DTD);
    }
}
