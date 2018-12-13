package pro.filatov.workstation4ceb.model.editor;

import pro.filatov.workstation4ceb.form.editor.IAppFrameEventListener;
import pro.filatov.workstation4ceb.form.tree.CheckBoxListElement;
import pro.filatov.workstation4ceb.model.Model;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuri.filatov on 02.08.2016.
 */
public class TreeViewModel implements IAppFrameEventListener {


    public TreeModel treeModel;

    public TreeModel createTreeModel(File infile){
        DefaultMutableTreeNode root;
        root  =new DefaultMutableTreeNode(new CheckBoxListElement(false, infile.getParentFile().getName(), false));
        createChildren(infile.getParentFile(), root);
        return new DefaultTreeModel(root);
    }



    private void updateTreeModel(File infile){
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new CheckBoxListElement(false, infile.getParentFile().getName(), false));
        createChildren(infile.getParentFile(), root);
        DefaultTreeModel defaultTreeModel = ((DefaultTreeModel)treeModel);
        defaultTreeModel.setRoot(root);
        defaultTreeModel.reload();
    }

    private void createChildren(File fileParent,
                                DefaultMutableTreeNode node) {
        File[] files = fileParent.listFiles();
        if (files == null) return;

        for (File file : files) {
            DefaultMutableTreeNode childNode;
            if(file.isDirectory()) {
                childNode = new DefaultMutableTreeNode(file.getName());
            }else{
                childNode = new DefaultMutableTreeNode(new CheckBoxListElement(false, file.getName(), false));
            }
            if(!file.getName().contains(".config")) {
                node.add(childNode);
            }
            if (file.isDirectory()) {
                createChildren(file, childNode);
            }
        }
    }

    public void updateCurrentFile(File newFile, File oldFile) {
        if(oldFile == null ||  !oldFile.getParent().equals(newFile.getParent()) ){
            updateTreeModel(newFile);
        }
    }

    public TreeModel getTreeModel() {
        if(treeModel == null){
            treeModel = createTreeModel(Model.getEditorModel().getCurrentFile());
        }
        return treeModel;
    }

    public void selectAllCheckBox(boolean mode){
        DefaultTreeModel defaultTreeModel = ((DefaultTreeModel)treeModel);
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) defaultTreeModel.getRoot();
        for(int i = 0; i < root.getChildCount(); i++){
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)root.getChildAt(i);
            Object data = node.getUserObject();
            if ( data instanceof CheckBoxListElement ) {
                CheckBoxListElement element = (CheckBoxListElement) data;
                element.setSelected(mode);
            }
        }
        defaultTreeModel.setRoot(root);
        defaultTreeModel.reload();
    }


    public List<File> getSelectedFiles(){
        DefaultMutableTreeNode root =(DefaultMutableTreeNode)treeModel.getRoot();
        List<File> selectedFiles = new ArrayList<>();
        getSelectedFilesFromNode(root, selectedFiles);
        return selectedFiles;
    }

    private void getSelectedFilesFromNode(DefaultMutableTreeNode node, List<File>selectedFiles){
        for(int i = 0; i < node.getChildCount(); i++){
            DefaultMutableTreeNode child =(DefaultMutableTreeNode) node.getChildAt(i);
            if(child.getChildCount() == 0){
                Object data = child.getUserObject();
                if(data instanceof CheckBoxListElement){
                    CheckBoxListElement element = (CheckBoxListElement)data;
                    if(element.isSelected()){
                        File file = new File(Model.getEditorModel().getCurrentFile().getParent() + File.separator + element.getText());
                        selectedFiles.add(file);
                    }
                }
            }else{
                getSelectedFilesFromNode(child, selectedFiles);
            }
        }





    }



}
