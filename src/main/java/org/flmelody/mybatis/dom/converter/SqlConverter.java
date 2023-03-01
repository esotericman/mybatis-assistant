package org.flmelody.mybatis.dom.converter;

import com.intellij.util.xml.ConvertContext;
import org.flmelody.mybatis.dom.model.IdDomElement;
import org.flmelody.mybatis.dom.model.Mapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * The type Sql converter.
 *
 * @author yanglin
 */
public class SqlConverter extends IdBasedTagConverter {

    @NotNull
    @Override
    public Collection<? extends IdDomElement> getComparisons(@Nullable Mapper mapper, ConvertContext context) {
        return mapper.getSqls();
    }

}
