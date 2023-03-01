package org.flmelody.mybatis.plugin;

import org.flmelody.mybatis.generator.pojo.MybatisTemplateRoot;
import org.flmelody.mybatis.generator.pojo.ModuleInfoGo;
import org.flmelody.mybatis.generator.template.ClassMetadata;
import org.flmelody.mybatis.generator.template.FreeMakerFormatter;
import org.flmelody.mybatis.generator.template.FreemarkerFile;
import org.jetbrains.annotations.Nullable;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

/**
 * Mybatis模板填充插件
 */
public class MybatisTemplatePlugin extends PluginAdapter {

    public static final String ROOT = "root";
    private static final Logger logger = LoggerFactory.getLogger(MybatisTemplatePlugin.class);

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        String root = properties.getProperty(ROOT);
        MybatisTemplateRoot rootObject = readRootObject(root);

        assert rootObject != null;
        ModuleInfoGo moduleUIInfo = rootObject.getModuleUIInfo();

        String modulePath = rootObject.getModuleUIInfo().getModulePath() + "/" + moduleUIInfo.getBasePath();
        final File file = new File(modulePath);
        if (!file.exists()) {
            final boolean created = file.mkdirs();
            logger.info("模块目录不存在,已创建目录. modulePath: {},created:{}", file.getAbsolutePath(), created);
        }
        TopLevelClass topLevelClass = new TopLevelClass(moduleUIInfo.getFileName());
        FreeMakerFormatter javaFormatter = new FreeMakerFormatter(rootObject, ClassMetadata.build(introspectedTable));
        javaFormatter.setContext(context);

        GeneratedJavaFile generatedJavaFile = new FreemarkerFile(topLevelClass,
                javaFormatter,
                modulePath,
                moduleUIInfo.getEncoding(),
                moduleUIInfo.getFileNameWithSuffix(),
                moduleUIInfo.getPackageName());
        logger.info("模板文件构建完成, modulePath: {}", modulePath);
        return Collections.singletonList(generatedJavaFile);
    }

    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable introspectedTable) {
        return Collections.emptyList();
    }

    @Nullable
    private MybatisTemplateRoot readRootObject(String root) {
        MybatisTemplateRoot rootObject = null;
        try {
            byte[] decode = Base64.getDecoder().decode(root.getBytes(StandardCharsets.UTF_8));
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(decode))) {
                rootObject = (MybatisTemplateRoot) objectInputStream.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return rootObject;
    }
}
