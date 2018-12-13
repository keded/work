package pro.filatov.workstation4ceb.form;

import pro.filatov.workstation4ceb.form.editor.AsmEditor;
import pro.filatov.workstation4ceb.form.editor.TextPane;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Created by yuri.filatov on 22.07.2016.
 */
public class MenuBar extends JMenuBar {


    private JMenuItem itemNewFile, itemOpen, itemSave, itemSaveAs, itemAbout, itemExit,
            itemSelectAll, itemCopy, itemPaste, itemCut, itemDelete, itemUndo, itemRedo, folderOpen;

    private JMenu file, view, help, edit, encoding;

    private JCheckBoxMenuItem itemStatusBar;
    private JCheckBoxMenuItem itemEditBar;
    private JCheckBoxMenuItem itemWrapLines;




    public MenuBar(final AsmEditor editor) {
        super();

        itemNewFile = new JMenuItem("New", Icons.iconNew);
        itemNewFile.setToolTipText("Create a new file");
        itemNewFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK));
        itemNewFile.setActionCommand("new");
        itemNewFile.addActionListener(editor);

        itemOpen = new JMenuItem("Open...", Icons.iconOpen);
        itemOpen.setToolTipText("Open a file");
        itemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
        itemOpen.setActionCommand("open");
        itemOpen.addActionListener(editor);

        folderOpen = new JMenuItem("Open folder...", Icons.iconOpen);
        folderOpen.setToolTipText("Open a folder");
        folderOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_MASK));
        folderOpen.setActionCommand("openFolder");
        folderOpen.addActionListener(editor);


        itemSave = new JMenuItem("Save", Icons.iconSave);
        itemSave.setToolTipText("Save the file");
        itemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
        itemSave.setActionCommand("save");
        itemSave.addActionListener(editor);

        itemSaveAs = new JMenuItem("Save As...", Icons.iconSave);
        itemSaveAs.setToolTipText("Save the file");
        itemSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK
                | ActionEvent.SHIFT_MASK));
        itemSaveAs.setActionCommand("saveAs");
        itemSaveAs.addActionListener(editor);

        itemAbout = new JMenuItem("About", Icons.iconAbout);
        itemAbout.setToolTipText("About the program");
        itemAbout.setActionCommand("about");
        itemAbout.addActionListener(editor);

        itemExit = new JMenuItem("Exit", Icons.iconExit);
        itemExit.setToolTipText("Exit the application");
        itemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_MASK));
        itemExit.setActionCommand("exit");
        itemExit.addActionListener(editor);

        itemSelectAll = new JMenuItem("Select All", Icons.iconSelectAll);
        itemSelectAll.setToolTipText("Select all text in this text area");
        itemSelectAll.setActionCommand("selectAll");
        itemSelectAll.addActionListener(editor);

        itemCopy = new JMenuItem("Copy", Icons.iconCopy);
        itemCopy.setToolTipText("Copy to a clipboard");
        itemCopy.setActionCommand("copy");
        itemCopy.addActionListener(editor);

        itemPaste = new JMenuItem("Paste", Icons.iconPaste);
        itemPaste.setToolTipText("Paste from a clipboard");
        itemPaste.setActionCommand("paste");
        itemPaste.addActionListener(editor);

        itemCut = new JMenuItem("Cut", Icons.iconCut);
        itemCut.setToolTipText("Cut to a clipboard");
        itemCut.setActionCommand("cut");
        itemCut.addActionListener(editor);

        itemDelete = new JMenuItem("Delete", Icons.iconDelete);
        itemDelete.setToolTipText("Delete the selected text");
        itemDelete.setActionCommand("delete");
        itemDelete.addActionListener(editor);

        itemUndo = new JMenuItem(editor.getUndoAction());
        itemUndo.setText("Undo");
        itemUndo.setIcon(Icons.iconUndo);
        itemUndo.setToolTipText("Undo last action");
        itemUndo.setActionCommand("undo");
        itemUndo.addActionListener(editor);

        itemRedo = new JMenuItem(editor.getRedoAction());
        itemRedo.setText("Redo");
        itemRedo.setIcon(Icons.iconRedo);
        itemRedo.setToolTipText("Redo last action");
        itemRedo.setActionCommand("redo");
        itemRedo.addActionListener(editor);

        encoding = new JMenu("Encoding");
        encoding.setAutoscrolls(true);
        encoding.setToolTipText("List of Encoding options");

        file = new JMenu("File");
        edit = new JMenu("Edit");
        view = new JMenu("View");
        help = new JMenu("Help");

        add(file);
        add(edit);
        add(view);
        add(help);


        file.add(itemNewFile);
        file.add(itemOpen);
        file.add(folderOpen);
        file.add(itemSave);
        file.add(itemSaveAs);
        file.addSeparator();
        file.add(itemExit);

        itemStatusBar = new JCheckBoxMenuItem("Statusbar");
        itemStatusBar.setToolTipText("Show statusbar");
        itemStatusBar.setState(true);
        /*
        itemStatusBar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               if (editor.getStatusBar() != null) {
                    if(editor.getStatusBar().isVisible()) {
                        editor.getStatusBar().setVisible(false);
                    } else {
                        editor.getStatusBar().setVisible(true);
                    }
                }
            }
        });
        */
        view.add(itemStatusBar);
        view.setToolTipText("View settings of the program");

        itemEditBar = new JCheckBoxMenuItem("Toolbar");
        itemEditBar.setToolTipText("Show edit toolbar");
        itemEditBar.setState(true);
       /*
        itemEditBar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(editor.getToolBar() !=null) {
                    if (editor.getToolBar().isVisible()) {
                        editor.getToolBar().setVisible(false);
                    } else {
                        editor.getToolBar().setVisible(true);
                    }
                }
            }
        });
        */
        view.add(itemEditBar);

        itemWrapLines = new JCheckBoxMenuItem("Wrap Lines", false);
        itemWrapLines.setToolTipText("Wrap lines depends on window's size");
        lineWrapOption(editor.getMainText(), itemWrapLines);
        view.add(itemWrapLines);
        edit.add(encoding);
        help.add(itemAbout);

    }

    /**
     * Line wrap check box option in Edit Menu on a menu bar<br>
     * If enables - wraps lines on the edge of the window
     * @param text text contained in a main text area
     * @param item check box item to connect with
     */
    private void lineWrapOption(final TextPane text, final JCheckBoxMenuItem item) {
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(item.isSelected()) {
                    text.setLineWrap(true);
                    text.setWrapStyleWord(true);
                } else if(!(item.isSelected())) {
                    text.setLineWrap(false);
                    text.setWrapStyleWord(false);
                }
            }
        });
    }
}
