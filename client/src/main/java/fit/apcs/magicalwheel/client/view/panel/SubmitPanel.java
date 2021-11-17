package fit.apcs.magicalwheel.client.view.panel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fit.apcs.magicalwheel.client.connection.Client;
import fit.apcs.magicalwheel.client.view.MainFrame;

public class SubmitPanel extends JPanel {

    @Serial
    private static final long serialVersionUID = -8293038273986925047L;

    private final GamePanel gamePanel;
    private final List<JButton> characterButtons;
    private final JTextField keywordField;
    private final JButton submitButton;
    private final MainFrame mainFrame;

    @Nullable
    private JButton curChoice;
    
    public SubmitPanel(GamePanel gamePanel, MainFrame mainFrame) {
        this.gamePanel = gamePanel;
        this.mainFrame = mainFrame;
        characterButtons = new ArrayList<>();
        keywordField = new JTextField();
        submitButton = new JButton();
        curChoice = null;

        for (var c = 'A'; c <= 'Z'; c++) {
            characterButtons.add(characterButton(c));
        }

        setLayout(new GridLayout(2, 1));
        setOpaque(false);
        add(characterPanel());
        add(keywordPanel());
        disableSubmission(false);
    }

    private JButton characterButton(char c) {
        final var button = new JButton();
        button.setText(Character.toString(c));
        button.setMargin(new Insets(5, 5, 5, 5));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        button.addActionListener(this::onCharButtonClickListener);
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
        final String character = curChoice.getText();
        final String keyword = keywordField.getText();
        Client.getInstance().submitGuess(gamePanel, character, keyword);
        disableSubmission(false);
        mainFrame.refresh();
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

    public void disableSubmission(boolean disable) {
        for (JButton button: characterButtons) {
            button.setEnabled(disable);
        }
        submitButton.setEnabled(disable);
        keywordField.setEnabled(disable);
    }

    public void disableKeywordField() {
        keywordField.setEnabled(false);
    }
}
