package org.flmelody.mybatis.ui;

/**
 * The interface List selection listener.
 *
 * @author yanglin
 */
public interface ListSelectionListener extends ExecutableListener {

    /**
     * Selected.
     *
     * @param index the index
     */
    public void selected(int index);

}
