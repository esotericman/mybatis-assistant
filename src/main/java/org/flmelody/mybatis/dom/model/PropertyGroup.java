package org.flmelody.mybatis.dom.model;

import com.intellij.psi.PsiClass;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.flmelody.mybatis.dom.converter.ColumnConverter;
import org.flmelody.mybatis.dom.converter.JdbcTypeConverter;
import org.flmelody.mybatis.dom.converter.PropertyConverter;
import org.flmelody.mybatis.dom.converter.TypeHandlerConverter;

/**
 * The interface Property group.
 *
 * @author yanglin
 */
public interface PropertyGroup extends DomElement {

    /**
     * Gets property.
     *
     * @return the property
     */
    @Attribute("property")
    @Convert(PropertyConverter.class)
    GenericAttributeValue<XmlAttributeValue> getProperty();

    /**
     * column
     *
     * @return
     */
    @Attribute("column")
    @Convert(value = ColumnConverter.class, soft = true)
    GenericAttributeValue<XmlAttributeValue> getColumn();

    /**
     * jdbcType
     *
     * @return
     */
    @Attribute("jdbcType")
    @Convert(JdbcTypeConverter.class)
    GenericAttributeValue<XmlAttributeValue> getJdbcType();

    /**
     * jdbcType
     *
     * @return
     */
    @Attribute("typeHandler")
    @Convert(TypeHandlerConverter.class)
    GenericAttributeValue<PsiClass> getTypeHandler();
}
