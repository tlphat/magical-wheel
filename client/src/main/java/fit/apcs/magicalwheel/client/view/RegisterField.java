package fit.apcs.magicalwheel.client.view;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterField extends JFrame implements ActionListener{
    JButton button;
    JTextField textField;

    RegisterField() {
        addTextField();
        addButton();
        this.pack();
        this.setVisible(true);
    }

    private void addTextField() {
        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(250, 40));
        this.add(textField);
    }

    private void addButton() {
        JButton button = new JButton("Register");
        button.addActionListener(this);
        this.add(button);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            textField.getText();
        }
    }
}
