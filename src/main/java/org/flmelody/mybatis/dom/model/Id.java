package org.flmelody.mybatis.dom.model;

import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import org.flmelody.mybatis.dom.converter.PropertyConverter;

/**
 * The interface id.
 *
 * @author yanglin
 */
public interface Id extends PropertyGroup {

    @Override
    @Attribute("property")
    @Convert(PropertyConverter.class)
    GenericAttributeValue<XmlAttributeValue> getProperty();

}
