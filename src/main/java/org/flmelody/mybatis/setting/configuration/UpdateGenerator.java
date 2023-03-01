package org.flmelody.mybatis.setting.configuration;

import com.intellij.psi.PsiMethod;
import org.flmelody.mybatis.dom.model.IdDomElement;
import org.flmelody.mybatis.dom.model.Mapper;
import org.jetbrains.annotations.NotNull;

/**
 * <p>
 * Update 代码生成器
 * </p>
 *
 * @author yanglin jobob
 * @since 2018 -07-30
 */
public class UpdateGenerator extends AbstractStatementGenerator {

    /**
     * Instantiates a new Update generator.
     *
     * @param patterns the patterns
     */
    public UpdateGenerator(@NotNull String... patterns) {
        super(patterns);
    }

    @NotNull
    @Override
    protected IdDomElement getTarget(@NotNull Mapper mapper, @NotNull PsiMethod method) {
        return mapper.addUpdate();
    }

    @NotNull
    @Override
    public String getId() {
        return "UpdateGenerator";
    }

    @NotNull
    @Override
    public String getDisplayText() {
        return "Update Statement";
    }

}
