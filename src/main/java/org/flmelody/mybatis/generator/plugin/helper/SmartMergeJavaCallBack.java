package org.flmelody.mybatis.generator.plugin.helper;

import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;

/**
 * smart merge existed file with new file
 * @author flmelody
 */
public class SmartMergeJavaCallBack extends DefaultShellCallback {

    public SmartMergeJavaCallBack() {
        super(true);
    }

    @Override
    public boolean isMergeSupported() {
        return true;
    }

    @Override
    public String mergeJavaFile(String newFileSource, File existingFile, String[] javadocTags, String fileEncoding) throws ShellException {
        // todo:
        return newFileSource;
    }


}
