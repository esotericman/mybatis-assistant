package org.flmelody.mybatis.alias;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import org.flmelody.mybatis.util.JavaUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * The type Inner alias resolver.
 *
 * @author yanglin
 */
public class InnerAliasResolver extends AliasResolver {
    private static final Logger logger = LoggerFactory.getLogger(InnerAliasResolver.class);
    private volatile Set<AliasDesc> innerAliasDescSet = null;

    /**
     * Instantiates a new Inner alias resolver.
     *
     * @param project the project
     */
    public InnerAliasResolver(Project project) {
        super(project);
    }

    private Set<AliasDesc> getAliasDescSet() {
        Set<AliasDesc> aliasDescSet = new HashSet<>();
        addAliasDesc(aliasDescSet, "java.lang.String", "string");
        addAliasDesc(aliasDescSet, "java.lang.Byte", "byte");
        addAliasDesc(aliasDescSet, "java.lang.Long", "long");
        addAliasDesc(aliasDescSet, "java.lang.Short", "short");
        addAliasDesc(aliasDescSet, "java.lang.Integer", "int");
        addAliasDesc(aliasDescSet, "java.lang.Integer", "integer");
        addAliasDesc(aliasDescSet, "java.lang.Double", "double");
        addAliasDesc(aliasDescSet, "java.lang.Float", "float");
        addAliasDesc(aliasDescSet, "java.lang.Boolean", "boolean");
        addAliasDesc(aliasDescSet, "java.util.Date", "date");
        addAliasDesc(aliasDescSet, "java.math.BigDecimal", "decimal");
        addAliasDesc(aliasDescSet, "java.lang.Object", "object");
        addAliasDesc(aliasDescSet, "java.util.Map", "map");
        addAliasDesc(aliasDescSet, "java.util.HashMap", "hashmap");
        addAliasDesc(aliasDescSet, "java.util.List", "list");
        addAliasDesc(aliasDescSet, "java.util.ArrayList", "arraylist");
        addAliasDesc(aliasDescSet, "java.util.Collection", "collection");
        addAliasDesc(aliasDescSet, "java.util.Iterator", "iterator");
        return aliasDescSet;
    }

    private void addAliasDesc(Set<AliasDesc> aliasDescSet, String clazz, String alias) {
        Optional<PsiClass> psiClassOptional = JavaUtils.findClazz(project, clazz);
        if (psiClassOptional.isPresent()) {
            PsiClass psiClass = psiClassOptional.get();
            AliasDesc aliasDesc = AliasDesc.create(psiClass, alias);
            aliasDescSet.add(aliasDesc);
        } else {
            logger.error("无法找到别名映射, class: {}, alias: {}", clazz, alias);
        }

    }

    /**
     * 支持延迟识别, 当项目第一次打开时，可能未配置JDK， 在未配置JDK时， 内部别名无法注册。
     * 这里支持等手动配置JDK后才开始缓存
     *
     * @param element the element
     * @return
     */
    @NotNull
    @Override
    public Set<AliasDesc> getClassAliasDescriptions(@Nullable PsiElement element) {
        if (innerAliasDescSet == null) {
            Set<AliasDesc> aliasDescSet = getAliasDescSet();
            if (!aliasDescSet.isEmpty()) {
                synchronized (this) {
                    this.innerAliasDescSet = aliasDescSet;
                }
            }
        }
        if (innerAliasDescSet == null) {
            return Collections.emptySet();
        }
        return innerAliasDescSet;
    }

}
