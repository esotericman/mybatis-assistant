package org.flmelody.mybatis.provider;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import org.apache.commons.lang.StringUtils;
import org.flmelody.mybatis.annotation.Annotation;
import org.flmelody.mybatis.dom.model.Mapper;
import org.flmelody.mybatis.util.Icons;
import org.flmelody.mybatis.util.JavaUtils;
import org.flmelody.mybatis.util.MapperUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

/**
 * The type Injection line marker provider.
 *
 * @author yanglin
 */
public class InjectionLineMarkerProvider extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element,@NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (!(element instanceof PsiField)) {
            return;
        }
        PsiField field = (PsiField) element;
        if (!isTargetField(field)) {
            return;
        }

        PsiType type = field.getType();
        if (!(type instanceof PsiClassReferenceType)) {
            return;
        }

        Optional<PsiClass> clazz = JavaUtils.findClazz(element.getProject(), type.getCanonicalText());
        if (!clazz.isPresent()) {
            return;
        }

        PsiClass psiClass = clazz.get();
        Optional<Mapper> mapper = MapperUtils.findFirstMapper(element.getProject(), psiClass);
        if (!mapper.isPresent()) {
            return;
        }

        NavigationGutterIconBuilder<PsiElement> builder =
            NavigationGutterIconBuilder.create(Icons.SPRING_INJECTION_ICON)
                .setAlignment(GutterIconRenderer.Alignment.CENTER)
                .setTarget(psiClass)
                .setTooltipTitle("Data access object found - " + psiClass.getQualifiedName());
        result.add(builder.createLineMarkerInfo(field.getNameIdentifier()));
    }

    private boolean isTargetField(PsiField field) {
        if (JavaUtils.isAnnotationPresent(field, Annotation.AUTOWIRED)) {
            return true;
        }
        Optional<PsiAnnotation> resourceAnno = JavaUtils.getPsiAnnotation(field, Annotation.RESOURCE);
        if (resourceAnno.isPresent()) {
            PsiAnnotationMemberValue nameValue = resourceAnno.get().findAttributeValue("name");
            String name = nameValue.getText().replaceAll("\"", "");
            return StringUtils.isBlank(name) || name.equals(field.getName());
        }
        return false;
    }

}
