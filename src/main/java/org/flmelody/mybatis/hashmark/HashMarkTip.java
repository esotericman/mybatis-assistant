package org.flmelody.mybatis.hashmark;

import com.intellij.codeInsight.completion.CompletionResultSet;
import org.flmelody.mybatis.dom.model.Mapper;

public interface HashMarkTip {
    String getName();

    void tipValue(CompletionResultSet completionResultSet, Mapper mapper);
}
