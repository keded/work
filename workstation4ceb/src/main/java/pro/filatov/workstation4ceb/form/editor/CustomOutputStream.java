package pro.filatov.workstation4ceb.form.editor;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Администратор on 29.04.2016.
 */
public class CustomOutputStream extends OutputStream {
    private JTextArea textArea;

    public CustomOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {
        // redirects data to the text area
        textArea.append(String.valueOf((char)b));
        // scrolls the text area to the end of data
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
}