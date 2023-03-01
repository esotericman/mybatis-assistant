package org.flmelody.mybatis.generator;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.softwareloop.mybatis.generator.plugins.LombokPlugin;
import org.flmelody.mybatis.generator.plugin.helper.*;
import org.flmelody.mybatis.generator.resolver.MybatisJavaTypeResolver;
import org.flmelody.mybatis.generator.pojo.*;
import org.flmelody.mybatis.generator.template.*;
import org.flmelody.mybatis.generator.util.DomainPlaceHolder;
import org.flmelody.mybatis.plugin.MybatisTemplatePlugin;
import org.flmelody.mybatis.util.DbToolsUtils;
import org.flmelody.mybatis.util.JavaUtils;
import org.flmelody.mybatis.util.SpringStringUtils;
import org.flmelody.mybatis.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.mybatis.generator.api.*;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.plugins.EqualsHashCodePlugin;
import org.mybatis.generator.plugins.SerializablePlugin;
import org.mybatis.generator.plugins.ToStringPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static org.flmelody.mybatis.generator.plugin.helper.ShellCallbackMethod.*;

/**
 * 代码生成入口
 *
 * @author ls9527
 */
public class MybatisGenerator {
    private static final Logger logger = LoggerFactory.getLogger(MybatisGenerator.class);
    private static final Map<ShellCallbackMethod, ShellCallback> SHELL_CALLBACK_HOLDER;
    private static final ProgressCallback NULL_PROGRESS_CALLBACK = new ProgressCallback() {
    };

    static {
        SHELL_CALLBACK_HOLDER = new HashMap<>();
        SHELL_CALLBACK_HOLDER.put(NEW_FILE, new NewFileShellCallback());
        SHELL_CALLBACK_HOLDER.put(SMART_MERGE, new SmartMergeJavaCallBack());
        SHELL_CALLBACK_HOLDER.put(OVERWRITE, new DefaultShellCallback(true));
    }

    public static void generate(Project project,
                                MybatisGeneratorProperties mybatisGeneratorProperties,
                                Map<String, ConfigSetting> configSettingMap,
                                DbTable dbTable,
                                String domainName,
                                String tableName) throws Exception {
        List<String> warnings = new ArrayList<>();
        Configuration config = new Configuration();

        Context context = new Context(ModelType.CONDITIONAL) {
            @Override
            public void validate(List<String> errors) {
            }

            @Override
            public void generateFiles(ProgressCallback callback, List<GeneratedJavaFile> generatedJavaFiles, List<GeneratedXmlFile> generatedXmlFiles, List<GeneratedKotlinFile> generatedKotlinFiles, List<GeneratedFile> otherGeneratedFiles, List<String> warnings) throws InterruptedException {
                super.generateFiles(callback, generatedJavaFiles, generatedXmlFiles, generatedKotlinFiles, otherGeneratedFiles, warnings);
                if (!mybatisGeneratorProperties.getNeedsModel()) {
                    generatedJavaFiles.removeIf(next -> !(next instanceof FreemarkerFile));
                }
            }
        };
        context.setId("MybatisContext");
        String encoding = mybatisGeneratorProperties.getEncoding();
        if (StringUtils.isEmpty(encoding)) {
            encoding = "UTF-8";
        }
        context.addProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING, encoding);
        context.addProperty(PropertyRegistry.CONTEXT_JAVA_FORMATTER, MybatisJavaFormatter.class.getName());

        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
        String targetProject = mybatisGeneratorProperties.getModulePath() + "/" + mybatisGeneratorProperties.getBasePath();
        javaModelGeneratorConfiguration.setTargetProject(targetProject);

        String relativePackage = mybatisGeneratorProperties.getRelativePackage();
        if (null != relativePackage && !"".equals(relativePackage)) {
            relativePackage = "." + relativePackage;
        }
        javaModelGeneratorConfiguration.setTargetPackage(mybatisGeneratorProperties.getBasePackage() + relativePackage);

        context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

        final List<ClassLoader> classLoaderList = new ArrayList<>();
        if (SpringStringUtils.hasText(mybatisGeneratorProperties.getSuperClass())) {
            javaModelGeneratorConfiguration.addProperty(PropertyRegistry.ANY_ROOT_CLASS, mybatisGeneratorProperties.getSuperClass());
            final Optional<PsiClass> psiClassOptional = JavaUtils.findClazz(project, mybatisGeneratorProperties.getSuperClass());
            psiClassOptional.ifPresent(psiClass -> classLoaderList.add(new MybatisClassPathDynamicClassLoader(psiClass)));

        }

        String extraDomainName = domainName;
        if (!StringUtils.isEmpty(mybatisGeneratorProperties.getClassNamePrefix())) {
            extraDomainName = StringUtils.upperCaseFirstChar(mybatisGeneratorProperties.getClassNamePrefix()) + extraDomainName;
        }

        if (!StringUtils.isEmpty(mybatisGeneratorProperties.getClassNameSuffix())) {
            extraDomainName = extraDomainName + StringUtils.upperCaseFirstChar(mybatisGeneratorProperties.getClassNameSuffix());
        }

        buildTableConfiguration(mybatisGeneratorProperties, context, tableName, extraDomainName);

        CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
        commentGeneratorConfiguration.setConfigurationType(MybatisCommentGenerator.class.getName());
        commentGeneratorConfiguration.addProperty("suppressDate", "true");
        commentGeneratorConfiguration.addProperty(MybatisCommentGenerator.USE_COMMENT, Boolean.valueOf(mybatisGeneratorProperties.isNeedsComment()).toString());
        commentGeneratorConfiguration.addProperty(MybatisCommentGenerator.USE_JSR303_PLUGIN, Boolean.valueOf(mybatisGeneratorProperties.isUseJsr303()).toString());
        commentGeneratorConfiguration.addProperty(MybatisCommentGenerator.USE_SWAGGER2_PLUGIN, Boolean.valueOf(mybatisGeneratorProperties.isUseSwagger2Plugin()).toString());
        commentGeneratorConfiguration.addProperty(MybatisCommentGenerator.USE_SWAGGER3_PLUGIN, Boolean.valueOf(mybatisGeneratorProperties.isUseSwagger3Plugin()).toString());

        context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);

        JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
        javaTypeResolverConfiguration.addProperty("forceBigDecimals", "false");
        context.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);
        final ConfigSetting configSetting = configSettingMap.get(mybatisGeneratorProperties.getTemplatesName());
        if (configSetting == null) {
            logger.error("未选择模板组名称, templatesName: {}", mybatisGeneratorProperties.getTemplatesName());
            return;
        }
        IntellijTableInfo intellijTableInfo = DbToolsUtils.buildIntellijTableInfo(dbTable);

        ModelInfo modelInfo = buildDomainInfo(mybatisGeneratorProperties, domainName);
        // 根据模板生成代码的插件
        configExtraPlugin(extraDomainName, context, modelInfo, configSetting, mybatisGeneratorProperties.getModuleUIInfoList());
        // 界面配置的扩展插件
        addPluginConfiguration(context, mybatisGeneratorProperties);
        config.addContext(context);

        Set<String> contexts = new HashSet<>();
        Set<String> fullyQualifiedTables = new HashSet<>();
        IntellijMyBatisGenerator intellijMyBatisGenerator = new IntellijMyBatisGenerator(config, SHELL_CALLBACK_HOLDER.get(getByText(mybatisGeneratorProperties.getGeneratorOption())), warnings);


        intellijMyBatisGenerator.generate(NULL_PROGRESS_CALLBACK,
                contexts,
                fullyQualifiedTables,
                true,
                classLoaderList,
                intellijTableInfo);
    }

    private static ModelInfo buildDomainInfo(MybatisGeneratorProperties mybatisGeneratorProperties, String domainName) {
        ModelInfo modelInfo = new ModelInfo();
        modelInfo.setModulePath(mybatisGeneratorProperties.getModulePath());
        modelInfo.setBasePath(mybatisGeneratorProperties.getBasePath());
        modelInfo.setBasePackage(mybatisGeneratorProperties.getBasePackage());
        modelInfo.setRelativePackage(mybatisGeneratorProperties.getRelativePackage());
        modelInfo.setEncoding(mybatisGeneratorProperties.getEncoding());
        modelInfo.setFileName(domainName);
        return modelInfo;
    }

    private static void buildTableConfiguration(MybatisGeneratorProperties mybatisGeneratorProperties, Context context, @NotNull String tableName, String extraDomainName) {
        TableConfiguration tc = new TableConfiguration(context);
        tc.setTableName(tableName);
        tc.setDomainObjectName(extraDomainName);
        if (mybatisGeneratorProperties.isUseActualColumns()) {
            tc.addProperty("useActualColumnNames", "true");
        }

        if (mybatisGeneratorProperties.isUseActualColumnAnnotationInject()) {
            tc.addProperty("useActualColumnAnnotationInject", "true");
        }
        boolean replaceNecessary = false;
        StringJoiner stringJoiner = new StringJoiner("|");
        if (!StringUtils.isEmpty(mybatisGeneratorProperties.getIgnoredColumnPrefix())) {
            stringJoiner.add("^" + mybatisGeneratorProperties.getIgnoredColumnPrefix());
            replaceNecessary = true;
        }
        if (!StringUtils.isEmpty(mybatisGeneratorProperties.getIgnoredColumnSuffix())) {
            stringJoiner.add(mybatisGeneratorProperties.getIgnoredColumnSuffix() + "$");
            replaceNecessary = true;
        }
        if (replaceNecessary) {
            final ColumnRenamingRule columnRenamingRule = new ColumnRenamingRule();
            columnRenamingRule.setSearchString(stringJoiner.toString());
            columnRenamingRule.setReplaceString("");
            tc.setColumnRenamingRule(columnRenamingRule);
        }
        context.addTableConfiguration(tc);
    }

    private static void configExtraPlugin(String extraDomainName,
                                          Context context,
                                          ModelInfo modelInfo,
                                          ConfigSetting configSetting,
                                          List<ModuleInfoGo> extraTemplateNames) {


        Map<String, TemplateSettingDTO> templateSettingDTOMap = configSetting.getTemplateSettingDTOList()
                .stream()
                .collect(Collectors.toMap(TemplateSettingDTO::getConfigName, m -> m, (a, b) -> a));

        List<MybatisTemplateRoot> templateRootList = new ArrayList<>();
        List<ModuleInfoGo> rootModuleInfo = new ArrayList<>();
        for (ModuleInfoGo moduleInfo : extraTemplateNames) {
            TemplateSettingDTO templateSettingDTO = templateSettingDTOMap.get(moduleInfo.getConfigName());
            if (templateSettingDTO != null) {
                ModelInfo customModelInfo = determineDomainInfo(extraDomainName, modelInfo, moduleInfo);
                ModuleInfoGo moduleInfoReplaced = replaceByDomainInfo(moduleInfo, customModelInfo);
                MybatisTemplateRoot templateRoot = buildRootConfig(customModelInfo, moduleInfoReplaced, configSetting, rootModuleInfo);
                templateRootList.add(templateRoot);
            }
        }

        for (MybatisTemplateRoot templateRoot : templateRootList) {
            addPlugin(context, templateRoot);
        }
    }

    private static ModelInfo determineDomainInfo(String extraDomainName, ModelInfo modelInfo, ModuleInfoGo moduleInfo) {
        ModelInfo customModelInfo = null;
        // 重置实体模板的类名填充
        if ("model".equals(moduleInfo.getConfigName())) {
            customModelInfo = modelInfo.copyFromFileName(extraDomainName);
        }
        if (customModelInfo == null) {
            customModelInfo = modelInfo;
        }
        return customModelInfo;
    }

    private static ModuleInfoGo replaceByDomainInfo(ModuleInfoGo moduleInfo, ModelInfo modelInfo) {
        ModuleInfoGo moduleUIInfo = new ModuleInfoGo();
        moduleUIInfo.setConfigName(moduleInfo.getConfigName());
        moduleUIInfo.setModulePath(DomainPlaceHolder.replace(moduleInfo.getModulePath(), modelInfo));
        moduleUIInfo.setBasePath(DomainPlaceHolder.replace(moduleInfo.getBasePath(), modelInfo));
        moduleUIInfo.setPackageName(DomainPlaceHolder.replace(moduleInfo.getPackageName(), modelInfo));
        moduleUIInfo.setFileName(DomainPlaceHolder.replace(moduleInfo.getFileName(), modelInfo));
        moduleUIInfo.setFileNameWithSuffix(DomainPlaceHolder.replace(moduleInfo.getFileNameWithSuffix(), modelInfo));
        moduleUIInfo.setConfigFileName(moduleInfo.getConfigFileName());
        moduleUIInfo.setEncoding(DomainPlaceHolder.replace(moduleInfo.getEncoding(), modelInfo));
        // 校验文件模板必须存在
        if (moduleUIInfo.getConfigFileName() == null) {
            throw new RuntimeException("模板文件为空, 无法生成代码. config: " + moduleUIInfo.getConfigName());
        }
        return moduleUIInfo;
    }

    private static void addPlugin(Context context, Serializable serializable) {
        PluginConfiguration serviceJavaPluginConfiguration = new PluginConfiguration();
        serviceJavaPluginConfiguration.setConfigurationType(MybatisTemplatePlugin.class.getName());
        // 模板的内容
        addRootMapToConfig(serializable, serviceJavaPluginConfiguration);
        context.addPluginConfiguration(serviceJavaPluginConfiguration);
    }

    private static void addRootMapToConfig(Serializable serializable, PluginConfiguration customPluginConfiguration) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(out)) {
            objectOutputStream.writeObject(serializable);
        } catch (IOException e) {
            logger.error("序列化数据失败", e);
            throw new RuntimeException("序列化数据失败");
        }
        byte[] encode = Base64.getEncoder().encode(out.toByteArray());
        customPluginConfiguration.addProperty(MybatisTemplatePlugin.ROOT, new String(encode));
    }

    private static MybatisTemplateRoot buildRootConfig(ModelInfo modelInfo,
                                                       ModuleInfoGo moduleUIInfo,
                                                       ConfigSetting configSetting,
                                                       List<ModuleInfoGo> rootModuleUIInfo) {
        MybatisTemplateRoot mybatisTemplateRoot = new MybatisTemplateRoot();
        mybatisTemplateRoot.setModelInfo(modelInfo);
        mybatisTemplateRoot.setModuleUIInfo(moduleUIInfo);
        // 替换模板内容
        mybatisTemplateRoot.setModuleInfoList(rootModuleUIInfo);
        rootModuleUIInfo.add(moduleUIInfo);
        mybatisTemplateRoot.setTemplateSettingDTOList(configSetting.getTemplateSettingDTOList());
        mybatisTemplateRoot.setTemplateBasePath(configSetting.getPath());
        return mybatisTemplateRoot;
    }


    /**
     * 添加相关插件（注意插件文件需要通过jar引入）
     *
     * @param context context
     * @param mybatisGeneratorProperties mybatisGeneratorProperties
     */
    private static void addPluginConfiguration(Context context, MybatisGeneratorProperties mybatisGeneratorProperties) {
        //实体添加序列化
        PluginConfiguration serializablePlugin = new PluginConfiguration();
        serializablePlugin.addProperty("type", SerializablePlugin.class.getName());
        serializablePlugin.setConfigurationType(SerializablePlugin.class.getName());
        context.addPluginConfiguration(serializablePlugin);

        // toString,hashCode,equals
        if (mybatisGeneratorProperties.isNeedToStringHashcodeEquals()) {
            PluginConfiguration equalsHashCodePlugin = new PluginConfiguration();
            equalsHashCodePlugin.addProperty("type", EqualsHashCodePlugin.class.getName());
            equalsHashCodePlugin.setConfigurationType(EqualsHashCodePlugin.class.getName());
            context.addPluginConfiguration(equalsHashCodePlugin);
            PluginConfiguration toStringPluginPlugin = new PluginConfiguration();
            toStringPluginPlugin.addProperty("type", ToStringPlugin.class.getName());
            toStringPluginPlugin.setConfigurationType(ToStringPlugin.class.getName());
            context.addPluginConfiguration(toStringPluginPlugin);
        }

        JavaTypeResolverConfiguration javaTypeResolverPlugin = new JavaTypeResolverConfiguration();
        javaTypeResolverPlugin.setConfigurationType(MybatisJavaTypeResolver.class.getName());
        //for JSR310
        javaTypeResolverPlugin.addProperty("supportJsr", String.valueOf(mybatisGeneratorProperties.isUseJsr310()));
        javaTypeResolverPlugin.addProperty("supportAutoNumeric", "true");
        context.setJavaTypeResolverConfiguration(javaTypeResolverPlugin);

        // Lombok 插件
        if (mybatisGeneratorProperties.isUseLombokPlugin()) {
            PluginConfiguration pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.addProperty("type", LombokPlugin.class.getName());
            pluginConfiguration.setConfigurationType(LombokPlugin.class.getName());
            context.addPluginConfiguration(pluginConfiguration);
        }


    }

}


