package org.flmelody.mybatis.generator.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author  all contribuited people
 */
@Data
public class ModelInfo implements Serializable {
    private String encoding;
    private String basePackage;
    private String relativePackage;
    private String fileName;
    private String basePath;
    private String modulePath;

    public ModelInfo copyFromFileName(String extraDomainName) {
        ModelInfo modelInfo = new ModelInfo();
        modelInfo.setModulePath(modulePath);
        modelInfo.setBasePath(basePath);
        modelInfo.setEncoding(encoding);
        modelInfo.setBasePackage(basePackage);
        modelInfo.setFileName(extraDomainName);
        modelInfo.setRelativePackage(relativePackage);
        return modelInfo;
    }
}

