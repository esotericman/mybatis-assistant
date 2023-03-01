package org.flmelody.mybatis.reference;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.xml.XmlAttributeValue;
import org.flmelody.mybatis.dom.converter.PropertySetterFind;
import org.flmelody.mybatis.util.JavaUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * The type Psi field reference set resolver.
 *
 * @author yanglin
 */
public class PsiFieldReferenceSetResolver extends ContextReferenceSetResolver<XmlAttributeValue, PsiField> {

    /**
     * Instantiates a new Psi field reference set resolver.
     *
     * @param xmlAttributeValue the xmlAttributeValue
     */
    protected PsiFieldReferenceSetResolver(XmlAttributeValue xmlAttributeValue) {
        super(xmlAttributeValue);
    }

    @NotNull
    @Override
    public String getText() {
        return getElement().getValue();
    }

    @NotNull
    @Override
    public Optional<PsiField> resolve(PsiField current, String text) {
        PsiType type = current.getType();
        if (type instanceof PsiClassReferenceType && !((PsiClassReferenceType) type).hasParameters()) {
            PsiClass clazz = ((PsiClassReferenceType) type).resolve();
            if (null != clazz) {
                return JavaUtils.findSettablePsiField(clazz, text);
            }
        }
        return Optional.empty();
    }

    @NotNull
    @Override
    public Optional<PsiField> getStartElement(@Nullable String firstText) {
        PropertySetterFind propertySetterFind = new PropertySetterFind();
        return propertySetterFind.getStartElement(firstText, getElement());
    }

}
