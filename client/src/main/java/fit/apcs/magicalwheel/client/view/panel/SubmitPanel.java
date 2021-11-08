package fit.apcs.magicalwheel.client.view.panel;

import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SubmitPanel extends JPanel {

    private final List<JButton> characterButtons;
    private final JTextField keywordField;
    private final JButton submitButton;
    
    public SubmitPanel() {
        characterButtons = new ArrayList<>();
        keywordField = new JTextField();
        submitButton = new JButton();

        for (char c = 'A'; c <= 'Z'; c++) {
            final var button = new JButton();
            button.setText(Character.toString(c));
            button.setMargin(new Insets(5, 5, 5, 5));
            characterButtons.add(button);
        }

        setLayout(new GridLayout(2, 1));
        setOpaque(false);
        add(characterPanel());
        add(keywordPanel());
    }

    private JPanel keywordPanel() {
        final var panel = new JPanel(new GridBagLayout());
        final var gbc = new GridBagConstraints();
        final var label = new JLabel("Keyword guess (optional): ");
        keywordField.setColumns(10);
        submitButton.setText("Submit");
        label.setForeground(Color.WHITE);
        gbc.insets = new Insets(10, 0, 0, 0);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(label, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(keywordField, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(submitButton, gbc);
        panel.setOpaque(false);
        return panel;
    }

    private JPanel characterPanel() {
        final var panel = new JPanel();
        final var layout = new GridLayout(2, 13);
        layout.setHgap(1);
        layout.setVgap(1);
        panel.setLayout(layout);
        panel.setOpaque(false);
        for (JButton button: characterButtons) {
            panel.add(button);
        }
        return panel;
    }

    public void disableSubmission() {
        for (JButton button: characterButtons) {
            button.setEnabled(false);
        }
        keywordField.setEnabled(false);
        submitButton.setEnabled(false);
    }
}
