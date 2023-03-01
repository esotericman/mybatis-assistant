package org.flmelody.mybatis.generator.plugin.helper;

/**
 * @author flmelody
 */
public enum ShellCallbackMethod {
    NEW_FILE("New File"),
    SMART_MERGE("Overwrite File"),
    OVERWRITE("Smart Merge");
    final String text;

    ShellCallbackMethod(String text) {
        this.text = text;
    }
    public static ShellCallbackMethod getByText(String text){
        ShellCallbackMethod[] values = ShellCallbackMethod.values();
        for (ShellCallbackMethod value : values) {
            if (value.text.equals(text)){
                return value;
            }
        }
        return NEW_FILE;
    }
}
