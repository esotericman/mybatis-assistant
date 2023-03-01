package org.flmelody.mybatis.dom.model;

import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.flmelody.mybatis.dom.converter.SqlConverter;

/**
 * The interface Include.
 *
 * @author yanglin
 */
public interface Include extends DomElement {

    /**
     * Gets ref id.
     *
     * @return the ref id
     */
    @Attribute("refid")
    @Convert(SqlConverter.class)
    GenericAttributeValue<XmlTag> getRefId();

}
