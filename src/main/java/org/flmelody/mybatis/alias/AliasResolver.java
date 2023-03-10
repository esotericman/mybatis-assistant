package org.flmelody.mybatis.alias;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import org.flmelody.mybatis.util.JavaUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

/**
 * The type Alias resolver.
 *
 * @author yanglin
 */
public abstract class AliasResolver {

    /**
     * The Project.
     */
    protected Project project;

    /**
     * Instantiates a new Alias resolver.
     *
     * @param project the project
     */
    public AliasResolver(Project project) {
        this.project = project;
    }

    /**
     * Add alias desc optional.
     *
     * @param descSet the desc
     * @param clazz the clazz
     * @param alias the alias
     * @return the optional
     */
    protected Optional<AliasDesc> addAliasDesc(@NotNull Set<AliasDesc> descSet, @Nullable PsiClass clazz, @Nullable String alias) {
        if (null == alias || !JavaUtils.isModelClazz(clazz)) {
            return Optional.empty();
        }
        AliasDesc desc = new AliasDesc();
        descSet.add(desc);
        desc.setClazz(clazz);
        desc.setAlias(alias);
        return Optional.of(desc);
    }

    /**
     * Gets class alias descriptions.
     *
     * @param element the element
     * @return the class alias descriptions
     */
    @NotNull
    public abstract Set<AliasDesc> getClassAliasDescriptions(@Nullable PsiElement element);

    /**
     * Gets project.
     *
     * @return the project
     */
    public Project getProject() {
        return project;
    }

    /**
     * Sets project.
     *
     * @param project the project
     */
    public void setProject(Project project) {
        this.project = project;
    }
}
