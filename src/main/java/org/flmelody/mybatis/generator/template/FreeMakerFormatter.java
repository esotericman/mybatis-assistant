package org.flmelody.mybatis.generator.template;


import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.flmelody.mybatis.generator.pojo.MybatisTemplateRoot;
import org.flmelody.mybatis.generator.pojo.ModuleInfoGo;
import org.mybatis.generator.api.JavaFormatter;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.config.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * freemarker 模板格式化代码生成
 */
public class FreeMakerFormatter implements JavaFormatter {

    public static final String TEMPLATE = "template";
    private static final String USER_NAME = "user.name";
    private final MybatisTemplateRoot rootObject;
    private final ClassMetadata classMetadata;
    protected Context context;

    public FreeMakerFormatter(MybatisTemplateRoot rootObject, ClassMetadata classMetadata) {
        this.rootObject = rootObject;
        this.classMetadata = classMetadata;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    private static final Logger logger = LoggerFactory.getLogger(FreeMakerFormatter.class);

    /**
     * free marker 生成的代码, 不关心这里设置的任何属性
     *
     * @param compilationUnit
     * @return
     */
    @Override
    public String getFormattedContent(CompilationUnit compilationUnit) {
        try {
            ModuleInfoGo templateSettingDTO = rootObject.getModuleUIInfo();
            String modulePath = rootObject.getModuleUIInfo().getModulePath() + "/" + templateSettingDTO.getBasePath();
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
            cfg.setDirectoryForTemplateLoading(new File(modulePath));
            // 设置模板加载器

            TemplateLoader templateLoader = getTemplateLoader(rootObject.getTemplateBasePath());
            cfg.setTemplateLoader(templateLoader);

            cfg.setDefaultEncoding(templateSettingDTO.getEncoding());
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

            Template templateName = cfg.getTemplate(rootObject.getModuleUIInfo().getConfigFileName());
            // 因为 StringWriter 的close实现为空, 所以没有调用writer.close()
            Writer writer = new StringWriter();
            Map<String, Object> map = new HashMap<>();

            map.put("baseInfo", templateSettingDTO);
            map.put("tableClass", classMetadata);
            map.put("author", System.getProperty(USER_NAME, "mybatis"));
            map.putAll(rootObject.toMap());
            templateName.process(map, writer);
            final String templateContent = writer.toString();
            logger.info("模板内容生成成功, pathname: {}", modulePath);
            return templateContent;
        } catch (IOException | TemplateException e) {
            StringWriter out = new StringWriter();
            try (PrintWriter stringWriter = new PrintWriter(out)) {
                e.printStackTrace(stringWriter);
            }
            logger.error("模板内容生成失败", e);
            return "填充模板出错," + out.toString();
        }
    }

    private TemplateLoader getTemplateLoader(String basePath) throws IOException {
        return new FileTemplateLoader(new File(basePath));
    }

}
