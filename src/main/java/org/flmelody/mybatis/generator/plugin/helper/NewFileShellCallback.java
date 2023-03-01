package org.flmelody.mybatis.generator.plugin.helper;

import org.mybatis.generator.internal.DefaultShellCallback;

/**
 * create new file instead of overwriting file
 *
 * @author flmelody
 */
public class NewFileShellCallback extends DefaultShellCallback {
    public NewFileShellCallback() {
        super(false);
    }
}
