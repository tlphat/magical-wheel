package fit.apcs.magicalwheel.client.view.panel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
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

        initLayout();
        disableSubmission(false);
    }

    private void initLayout() {
        final var gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        setLayout(new GridBagLayout());
        setOpaque(false);
        setBackground(Color.BLUE);
        add(characterPanel(), gbc);
        add(keywordPanel(), gbc);
    }

    private JButton characterButton(char c) {
        final var button = new JButton();
        button.setText(Character.toString(c));
        button.setMargin(new Insets(2, 2, 2, 2));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
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
        final var label = new JLabel("Keyword (optional): ");
        keywordFieldDecor();
        submitButton.setText("Submit");
        submitButton.addActionListener(event -> onSubmitButtonListener());
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Calibri", Font.PLAIN, 15));
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(label, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(keywordField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTH;
        panel.add(submitButton, gbc);
        panel.setOpaque(false);
        return panel;
    }

    private void keywordFieldDecor() {
        keywordField.setColumns(10);
        keywordField.setBackground(Color.BLACK);
        keywordField.setForeground(Color.WHITE);
        keywordField.setCaretColor(Color.WHITE);
        keywordField.setFont(new Font("Calibri", Font.PLAIN, 15));
    }

    private void onSubmitButtonListener() {
        if (curChoice == null) {
            return;
        }
        final String character = curChoice.getText();
        final String keyword = keywordField.getText();
        Client.getInstance().submitGuess(character, keyword);
        disableSubmission(false);
        gamePanel.cancelTimer();
        mainFrame.refresh();
    }

    private JPanel characterPanel() {
        final var panel = new JPanel();
        final var layout = new GridLayout(2, 13);
        layout.setHgap(3);
        layout.setVgap(3);
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
