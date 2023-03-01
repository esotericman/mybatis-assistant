package org.flmelody.mybatis.dom.model;

import com.intellij.util.xml.*;
import org.flmelody.mybatis.dom.converter.DaoMethodConverter;

/**
 * The interface id dom element.
 *
 * @author yanglin
 */
public interface IdDomElement extends DomElement {

    /**
     * Gets id.
     *
     * @return the id
     */
    @Required
    @NameValue
    @Attribute("id")
    @Convert(DaoMethodConverter.class)
    GenericAttributeValue<Object> getId();

    /**
     * Sets value.
     *
     * @param content the content
     */
    void setValue(String content);
}
