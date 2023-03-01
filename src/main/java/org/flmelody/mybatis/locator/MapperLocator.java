package org.flmelody.mybatis.locator;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.flmelody.mybatis.util.JavaUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The type Mapper locator.
 *
 * @author yanglin
 */
public class MapperLocator {

    /**
     * The constant LocateStrategy.
     */
    public static LocateStrategy defaultLocateStrategy = new PackageLocateStrategy();

    /**
     * Gets instance.
     *
     * @param project the project
     * @return the instance
     */
    public static MapperLocator getInstance(@NotNull Project project) {
        return project.getService(MapperLocator.class);
    }

    /**
     * Process boolean.
     *
     * @param method the method
     * @return the boolean
     */
    public boolean process(@Nullable PsiMethod method) {
        return null != method && process(method.getContainingClass());
    }

    /**
     * Process boolean.
     *
     * @param clazz the clazz
     * @return the boolean
     */
    public boolean process(@Nullable PsiClass clazz) {
        return null != clazz && JavaUtils.isElementWithinInterface(clazz) && defaultLocateStrategy.apply(clazz);
    }

}
