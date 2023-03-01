package org.flmelody.mybatis.generator.template;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.JavaFormatter;
import org.mybatis.generator.api.dom.java.CompilationUnit;

public class FreemarkerFile extends GeneratedJavaFile {
    private final String fileName;
    private final String packageName;

    public FreemarkerFile(CompilationUnit compilationUnit, JavaFormatter javaFormatter, String targetProject, String fileEncoding, String fileName, String packageName) {
        super(compilationUnit, targetProject, fileEncoding, javaFormatter);
        this.fileName = fileName;
        this.packageName = packageName;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getFormattedContent() {
        return super.getFormattedContent();
    }

    @Override
    public String getTargetPackage() {
        return packageName;
    }
}
