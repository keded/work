package pro.filatov.workstation4ceb.form.tree;

import pro.filatov.workstation4ceb.model.Model;

import javax.swing.*;

/**
 * Created by yuri.filatov on 02.08.2016.
 */
public class TreeViewPane extends  JScrollPane {


    public TreeViewPane() {
        super(new CheckBoxTree(Model.getTreeViewModel().getTreeModel()));
    }


}
