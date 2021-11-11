package fit.apcs.magicalwheel.client.view.panel;

import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.w3c.dom.events.Event;

public class SubmitPanel extends JPanel {

    private final List<JButton> characterButtons;
    private final JTextField keywordField;
    private final JButton submitButton;
    private JButton curChoice;
    
    public SubmitPanel() {
        characterButtons = new ArrayList<>();
        keywordField = new JTextField();
        submitButton = new JButton();
        curChoice = null;

        for (char c = 'A'; c <= 'Z'; c++) {
            characterButtons.add(characterButton(c));
        }

        setLayout(new GridLayout(2, 1));
        setOpaque(false);
        add(characterPanel());
        add(keywordPanel());
    }

    private JLabel messageLabel() {
        final var message = new JLabel("Please choose a character");
        message.setForeground(Color.WHITE);
        return message;
    }

    private JButton characterButton(char c) {
        final var button = new JButton();
        button.setText(Character.toString(c));
        button.setMargin(new Insets(5, 5, 5, 5));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        button.addActionListener(event -> onCharButtonClickListener(event));
        button.setFocusPainted(false);
        return button;
    }

    private void onCharButtonClickListener(ActionEvent event) {
        final var chosenButton = event.getSource();
        if (curChoice == chosenButton) {
            return;
        }
        for (JButton button: characterButtons) {
            if (button == chosenButton) {
                button.setForeground(Color.BLACK);
                button.setBackground(Color.WHITE);
                if (curChoice != null) {
                    curChoice.setForeground(Color.WHITE);
                    curChoice.setBackground(Color.BLACK);
                }
                curChoice = button;
                break;
            }
        }
    }

    private JPanel keywordPanel() {
        final var panel = new JPanel(new GridBagLayout());
        final var gbc = new GridBagConstraints();
        final var label = new JLabel("Keyword guess (optional): ");
        keywordField.setColumns(10);
        submitButton.setText("Submit");
        submitButton.addActionListener(event -> onSubmitButtonListener());
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

    private void onSubmitButtonListener() {
        if (curChoice == null) {
            return;
        }
        String character = curChoice.getText();
        String keyword = keywordField.getText();
        //TODO: call a finish turn function
        System.out.println(character);
        System.out.println(keyword);
    }

    private JPanel characterPanel() {
        final var panel = new JPanel();
        final var layout = new GridLayout(2, 13);
        layout.setHgap(2);
        layout.setVgap(2);
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
