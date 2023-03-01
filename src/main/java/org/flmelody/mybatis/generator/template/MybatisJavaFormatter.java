package org.flmelody.mybatis.generator.template;

import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.ArrayList;
import java.util.List;

import static org.mybatis.generator.api.dom.java.render.RenderingUtilities.*;

public class MybatisJavaFormatter extends DefaultJavaFormatter {
    @Override
    public String visit(TopLevelClass topLevelClass) {
        // refer: org.mybatis.generator.api.dom.java.render.TopLevelClassRenderer
        List<String> lines = new ArrayList<>();

        lines.addAll(topLevelClass.getFileCommentLines());
        lines.addAll(renderPackage(topLevelClass));
        lines.addAll(renderStaticImports(topLevelClass));
        lines.addAll(renderImports(topLevelClass));
        lines.addAll(renderInnerClassNoIndent(topLevelClass, topLevelClass));
        String lineSeparator = "\n";
//        final String lineSeparator = System.getProperty("line.separator");
        return String.join(lineSeparator, lines);
    }
}
