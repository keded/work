package pro.filatov.workstation4ceb.form.editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * GUI for About dialog with simple a GridBagLayout layout. 
 *@author Mansur Y.
 */
public class AboutDialog extends JDialog {
	/**
	 * App's name
	 */
	private final String TITLE_NAME = "About CEB ASM Editor ";
	private static final long serialVersionUID = 1752605222930917064L;
	/**
	 * Main JPanel
	 */
	private JPanel mainPanel;	
	/**
	 * JLabel for About's dialog logo
	 */
	private JLabel image;
	/**
	 * JLabels for app's name, version, description and author name
	 */
	private JLabel appNameLabel;
	private JLabel appVerLabel;
	private JLabel appDescLabel;
	private JLabel appAuthorLabel;
	/**
	 * JButton for close button in the dialog
	 */
	private JButton close;
	/**
	 * ImageIcon for about's dialog logo. Loads it from current class classpath.
	 */
	private ImageIcon iconAbout = new ImageIcon(ClassLoader.getSystemResource("icons/about_logo.png"));
	/**
	 * String holders for all labels : app version, name, authors name and description
	 */
	private String appVersion = "0.1b";
	private String appName = "CebAsmEditor";
	private String appAuthor = "Created by Yuriy Filatov";
	private String appDescription = "Asm editor with basic features";
	
	public AboutDialog() {
		initialDialog(); 
		setTitle(TITLE_NAME);	
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
	}

	/**
	 * Main init method which uses GridBagLayout as a layout for about dialog
	 */
	private void initialDialog() {
		mainPanel = new JPanel();
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new FlowLayout());

		/**
		 * GridBagLayout
		 */
		
		GridBagLayout layout = new GridBagLayout();		
		GridBagConstraints con = new GridBagConstraints();
		layout.setConstraints(mainPanel, con);
		mainPanel.setLayout(layout);
		con.insets = new Insets(4, 4, 4, 4);
		con.anchor = GridBagConstraints.CENTER;
				
		// row 1
		image = new JLabel(iconAbout);
		con.gridx = 0;
		con.gridy = 0;
		con.gridheight = 1;
		con.gridwidth = 2;		
		mainPanel.add(image, con);
		
		// row 2
		appNameLabel = new JLabel(appName);
		appNameLabel.setFont(new Font("Dialog", Font.BOLD, 16));				
		con.gridx = 0;
		con.gridy = 2;
		mainPanel.add(appNameLabel, con);
				
		// row 3
		appVerLabel = new JLabel(appVersion);
		appVerLabel.setFont(new Font("Dialog", Font.PLAIN, 16));		
		con.gridx = 0;
		con.gridy = 3;		
		mainPanel.add(appVerLabel, con);
				
		// row 4
		appDescLabel = new JLabel(appDescription);
		appDescLabel.setFont(new Font("Courier New", Font.PLAIN, 12));
		con.gridx = 0;
		con.gridy = 4;		
		mainPanel.add(appDescLabel, con);
		
		appAuthorLabel = new JLabel(appAuthor);
		appAuthorLabel.setFont(new Font("Courier New", Font.PLAIN, 12));
		
		// row 5
		con.gridx = 0;
		con.gridy = 5;				
		mainPanel.add(appAuthorLabel, con);		
		con.insets = new Insets(10, 10, 10, 10);
		close = new JButton("Close");
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		con.gridheight = 1;
		con.gridwidth = 1;
		con.anchor = GridBagConstraints.EAST;
		con.gridx = 1;
		con.gridy = 6;		
		
		mainPanel.add(close, con);
		contentPane.add(mainPanel);
	}
}

