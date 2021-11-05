package fit.apcs.magicalwheel.client.view;

import java.io.Serial;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

class JTextFieldLimit extends PlainDocument {

    @Serial
    private static final long serialVersionUID = -3832086433540107585L;

    private final int limit;

    JTextFieldLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
       if (str != null && (getLength() + str.length()) <= limit) {
          super.insertString(offset, str, attr);
       }
    }

}
