
package pro.filatov.workstation4ceb.form.tree;

import pro.filatov.workstation4ceb.model.Model;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class CheckBoxTree extends JTree {

  public CheckBoxTree(TreeModel model) {
    super(model);

    setCellRenderer(new CheckBoxRenderer());

    addMouseListener(new MouseL());

  }

  private DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();





  class CheckBoxRenderer extends JCheckBox  implements TreeCellRenderer {


    public CheckBoxRenderer() {

      setOpaque(true);
    }


    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row,  boolean hasFocus) {

        if (!(value instanceof DefaultMutableTreeNode)) {

            return renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        }
        Object data = ((DefaultMutableTreeNode)value).getUserObject();

        if ( data instanceof CheckBoxListElement ) {
            CheckBoxListElement element = (CheckBoxListElement)data;

            setSelected(element.isSelected());
            setText(element.getText());
            if(element.getText().contains("asm")) {
               this.setForeground(Color.BLUE);
            } else{
              this.setForeground(Color.BLACK);
            }
            if(element.getText().equals(Model.getEditorModel().getCurrentFile().getName())){
              this.setBackground(Color.ORANGE);
            }else{
              this.setBackground(Color.WHITE);
            }

            return this;
        }

        return renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
    }
  }




  class MouseL extends MouseAdapter {
    public void mousePressed(MouseEvent e) {

      TreePath path = getClosestPathForLocation(
          e.getX(), e.getY());
      if ( path == null ) return;

      Object _node = path.getLastPathComponent();
      if (_node instanceof DefaultMutableTreeNode) {
          DefaultMutableTreeNode node = (DefaultMutableTreeNode)_node;

          Object data = node.getUserObject();

          if ( data instanceof CheckBoxListElement ) {
          CheckBoxListElement element = (CheckBoxListElement)data;
          if(e.getClickCount() == 2){
              String pathParent = Model.getEditorModel().getCurrentFile().getParent();
              Model.getEditorModel().setCurrentFile(new File(pathParent +  File.separator + element.getText()));
              Model.getEditorModel().openActiveTab(0);
          } else {

              DefaultMutableTreeNode root = (DefaultMutableTreeNode) Model.getTreeViewModel().getTreeModel().getRoot();
              if(node.equals(root)){
                  Model.getTreeViewModel().selectAllCheckBox(!element.isSelected());
              }
              element.setSelected(!element.isSelected());
          }
          repaint(getPathBounds(path));
        }
      }



    }



  }
}