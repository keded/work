package pro.filatov.workstation4ceb.form.editor;

import pro.filatov.workstation4ceb.form.AppFrame;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Simple GUI for search, replace, replace all text and its functionality.
 * Uses singleton pattern.
 * 
 * TODO: fix overuse of memory issue which occurs after few repeats 
 * of replace all feature and with a big files
 * TODO: odd behavior of caret's position that occurs ....
 * 
 * @author Mansur Y.
 */
public class SearchDialog extends JDialog implements FocusListener {
	private static final long serialVersionUID = -3446868398435205807L;
	/**
	 * Unique class's instance
	 */
	private static SearchDialog instance = null;
	/**
	 * Parent JPanel
	 */
	private JPanel mainPanel;
	/**
	 * Search JLabel for Search JTextField
	 */
	private JLabel searchLabel;
	/**
	 * ReplaceSearch JLabel for Replace JTextField
	 */
	private JLabel replaceLabel;
	/**
	 * Search JTextField
	 */
	private JTextField searchTextField;
	/**
	 * Replace JTextField
	 */
	private JTextField replaceTextField;

	private JButton replaceBut;
	/**
	 * Find JButton
	 */
	private JButton findBut;
	/**
	 * Replace All JButton
	 */
	private JButton replaceAllBut;
	/**
	 * String holder for a text in Search JTextField
	 */
	private String searchedText;
	/**
	 * String holder for a text from the JTextArea from a main app's window
	 */
	private String mainText;
	/**
	 * Array which holds caret positions from the main JTextArea
	 */
	ArrayList<Integer> caretPositions;
	/**
	 * JTextAre holder of app's main window text area
	 */
	private JTextPane area;
	/**
	 * Boolean flag for 'find' button which determines was it clicked for 
	 * first time which finds all text occurrences or for second and further
	 * times which switch between occurrences founded in a text.
	 */
	private boolean findButtonFlag = true;
	/** 
	 * Serves as a current position for caretPosition array.
	 */
	private int arrayLenCounter = 1;
	/**
	 * Old string taken from SearchText JTextField. 
	 * Checks before every click of 'next' was the 
	 * text pattern changed or not in a SearchText JTextField
	 */
	private String oldSearchedText;
	/**
	 * Reset replace button after new text to find was entered
	 */
	private int replaceCounter = 0;

	private SearchDialog(JFrame frame) {
		super(frame, false);
		instance = this;
		initGUI();	
	}

	/**
	 * Initialized GUI of the search dialog
	 */
	private void initGUI() {		
		mainPanel = new JPanel();
		mainPanel.setSize(240, 170);		
		Container contentPane = this.getContentPane();
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints con = new GridBagConstraints();
		layout.setConstraints(mainPanel, con);		
		mainPanel.setLayout(layout);
		
		con.insets = new Insets(4, 4, 4, 4);
		con.anchor = GridBagConstraints.CENTER;
		searchLabel = new JLabel("Search for:");
		searchLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
		con.anchor = GridBagConstraints.WEST;
		con.gridx = 0;
		con.gridy = 0;
		layout.setConstraints(searchLabel, con);
		
		Action enterAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if(findButtonFlag) {
					findTextInTextArea();
				} else {
					findNextInText();
				}
				}
		};
		searchTextField = new JTextField(8);
		searchTextField.setFocusable(true);
		searchTextField.setFont(new Font("Serif", Font.PLAIN, 14));
		searchTextField.setBounds(3, 3, 3, 3);
		searchTextField.setPreferredSize(new Dimension(100, 25));
		searchTextField.setMinimumSize(new Dimension(120, 25));
		searchTextField.setMaximumSize(new Dimension(120, 25));
		searchTextField.setDocument(new JTextFieldLimited(200));
		searchTextField.setAction(enterAction);
		searchTextField.addFocusListener(this);
		con.anchor = GridBagConstraints.CENTER;
		con.gridx = 1;
		con.gridy = 0;
		layout.setConstraints(searchTextField, con);
		
		con.anchor = GridBagConstraints.WEST;
		replaceLabel = new JLabel("Replace with:");
		replaceLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
		con.gridx = 0;
		con.gridy = 1;
		layout.setConstraints(replaceLabel, con);
		
		Action replaceAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				replaceTextInTextArea();
			}
		};
		replaceTextField = new JTextField(8);
		replaceTextField.setFocusable(true);
		replaceTextField.setFont(new Font("Serif", Font.PLAIN, 14));
		replaceTextField.setBounds(3, 3, 3, 3);
		replaceTextField.setPreferredSize(new Dimension(120, 25));
		replaceTextField.setMinimumSize(new Dimension(120, 25));
		replaceTextField.setMaximumSize(new Dimension(120, 25));
		replaceTextField.setDocument(new JTextFieldLimited(200)); // character limit is 200
		replaceTextField.setAction(replaceAction);
		replaceTextField.addFocusListener(this);
		con.anchor = GridBagConstraints.CENTER;
		con.gridx = 1;
		con.gridy = 1;
		layout.setConstraints(replaceTextField, con);
		
		findBut = new JButton(enterAction);
		findBut.setText("Find");
		findBut.setFocusable(false);
		findBut.setPreferredSize(new Dimension(98, 25));
		findBut.setMinimumSize(new Dimension(98, 25));
		findBut.setMaximumSize(new Dimension(98, 25));
		findBut.setMnemonic(KeyEvent.VK_ENTER);
		con.insets = new Insets(25, 4, 4, 4);
		con.gridx = 0;
		con.gridy = 2;
		layout.setConstraints(findBut, con);		
		
		replaceBut = new JButton(replaceAction);
		replaceBut.setText("Replace");
		replaceBut.setFocusable(false);
		replaceBut.setPreferredSize(new Dimension(98, 25));
		replaceBut.setMinimumSize(new Dimension(98, 25));
		replaceBut.setMaximumSize(new Dimension(98, 25));
		replaceBut.setMnemonic(KeyEvent.VK_ENTER);
		con.insets = new Insets(25, 4, 4, 4);
		con.gridx = 1;
		con.gridy = 2;
		layout.setConstraints(replaceBut, con);		
		
		replaceAllBut = new JButton("Replace All");
		replaceAllBut.setFocusable(false);
		replaceAllBut.setMargin(new Insets(1, 1, 1, 1));
		replaceAllBut.setPreferredSize(new Dimension(98, 25));
		replaceAllBut.setMinimumSize(new Dimension(98, 25));
		replaceAllBut.setMaximumSize(new Dimension(98, 25));
		replaceAllBut.addActionListener(new ReplaceAllAction());
		con.insets = new Insets(4, 4, 4, 4);
		con.gridx = 1;
		con.gridy = 3;
		//layout.setConstraints(replaceAllBut, con);
		
		mainPanel.add(searchLabel);
		mainPanel.add(searchTextField);
		mainPanel.add(findBut);
		mainPanel.add(replaceBut);
		mainPanel.add(replaceLabel);
		mainPanel.add(replaceTextField);
		//mainPanel.add(replaceAllBut);
		contentPane.add(mainPanel, BorderLayout.CENTER);
		setTitle("Search and Replace");
		setResizable(false);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		//setAutoRequestFocus(true);
		setSize(260, 180);	
	}
	
	/**
	 * Trade safe singleton getInstance() static method
	 * @param frame main frame reference
	 * @return unique SearchDialog's class object
	 */
	public static SearchDialog getInstance(JFrame frame) {
		if(instance == null) {
			synchronized (SearchDialog.class) {
				if(instance == null) {
					instance = new SearchDialog(frame);
				}
			}
		}
		return instance;
	}
			
	/**
	 *  Find text in a text area logic method
	 */
	private void findTextInTextArea() {
		replaceCounter = 0;	
		findButtonFlag = false;
		caretPositions = new ArrayList<Integer>();
		searchedText = searchTextField.getText();
		int lenSearchedText = searchedText.length();
		oldSearchedText = searchedText;
		area = AppFrame.getInstance().getAsmEditor().getMainText();
		area.getCaret().setSelectionVisible(true); // makes a text selection visible when it lost the focus
		Highlighter highlighter = area.getHighlighter(); // text highlighter
		Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(new Color(164, 255, 183, 255));
		highlighter.removeAllHighlights();
		if(!(searchedText.equals(""))) {		
			try {
				mainText = area.getText();
				mainText = mainText.replaceAll("\r\n", "\n");
				int index = mainText.indexOf(searchedText); 
				while(index >= 0) {
					int next = index + lenSearchedText;
					highlighter.addHighlight(index, next, painter);
					caretPositions.add(index);	
					index = mainText.indexOf(searchedText, index + lenSearchedText);					
				}
				
			} catch (BadLocationException e) {
				e.getMessage();
			}
			if(caretPositions.size() > 0) {
				int caretPos0 = caretPositions.get(0);
				area.setCaretPosition(caretPos0);
				area.setSelectionStart(caretPos0);
				area.setSelectionEnd(caretPos0 + lenSearchedText);
			}
		}		
 	}
	
	/**
	 * Replace test in a text area logic
	 */
	private void replaceTextInTextArea() {		
		replaceCounter++;
		if(replaceCounter >= 1 && replaceCounter <= caretPositions.size()
				&& caretPositions.size() > 0) {			
			area.getCaret().setSelectionVisible(true);
			area.replaceSelection(replaceTextField.getText());
			int delta = replaceTextField.getText().length()
					- searchedText.length();
			for(int i = 0; i < caretPositions.size(); i++) {
				caretPositions.set(i, caretPositions.get(i) + delta);				 
			}
			findNextInText();
		}
	}			

	/**
	 * Find next in a text area logic. 
	 * It triggers after 2+ click on 'find' button
	 */
	private void findNextInText() {
		String textFromSearchTextField = searchTextField.getText();
		if(oldSearchedText.equals(textFromSearchTextField) 
				&& caretPositions.size() > 0 
				&& !(textFromSearchTextField.equals(""))) {
			if(arrayLenCounter < caretPositions.size()) {
				int caretPos = caretPositions.get(arrayLenCounter);
				if(caretPos >= 0) {
					area.setCaretPosition(caretPos);
					area.setSelectionStart(caretPos);
					area.setSelectionEnd(caretPos + searchedText.length());
					arrayLenCounter++;
				}
			} else {
				arrayLenCounter = 1;
				findTextInTextArea();
			}			
		} else {
			findTextInTextArea();
		}
	}

	/**
	 * The class makes a limit of characters that user
	 * enters in JTextField
	 */
	class JTextFieldLimited extends PlainDocument {
		
		private int limit;
		
		public JTextFieldLimited(int limit) {
			super();
			this.limit = limit;
		}

		public void insertString(int offs, String str, AttributeSet a)
				throws BadLocationException {
			if(str == null) 
				return;
			if ((getLength() + str.length()) <= limit) {
				super.insertString(offs, str, a);
			}
		}
	}
	
	class ReplaceAllAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			replaceCounter = 1;
			int lenCaretPositions = caretPositions.size();
			if(caretPositions != null) {
				if(replaceCounter >= 1 && replaceCounter <= lenCaretPositions 
						&& lenCaretPositions > 0) {				
					area.getCaret().setSelectionVisible(true);
					area.setText(replaceAllLogic(mainText, 
							searchedText, replaceTextField.getText()));
					repaint();
				}
			}
		}
		
		// StringTokenizer used instead of String.split() because it's faster with one letter split
		/**
		 * Logic of 'replace all' button.
		 * @param text text from app's text area
		 * @param toFind text to find in a text area
		 * @param toReplace new text which replaces toFind text
		 * @return returns new text will all occurrences replaces
		 */
		private String replaceAllLogic(String text, String toFind, String toReplace) {
			StringBuilder textToReturn = new StringBuilder();
			StringTokenizer strTok = new StringTokenizer(text, " ");
			ArrayList<String> words = new ArrayList<String>(strTok.countTokens());
			int i = 0;
			while(strTok.hasMoreTokens()) {
				String nextToken = strTok.nextToken();
				words.add(i, nextToken);
				int index = nextToken.indexOf(toFind);
				while(index != -1) {
					StringBuilder sb = new StringBuilder(nextToken);
					sb.replace(index, toFind.length() + index, toReplace);
					words.add(i, sb.toString());
					index = nextToken.indexOf(toFind, index + 1);
				}
				if(i == words.size() - 1) {
					textToReturn.append(words.get(i));
				} else {
					textToReturn.append(words.get(i));
				}
				i++;
			}
			return textToReturn.toString();
		}
	}	
	
	/**
	 * Selects text of both JTextField on Search Dialog focus gain.
	 */
	public void focusGained(FocusEvent e) {
		searchTextField.select(0, searchTextField.getText().length());
		replaceTextField.select(0, replaceTextField.getText().length());
	}

	public void focusLost(FocusEvent e) {
		// do nothing on focus lose
	}
}

