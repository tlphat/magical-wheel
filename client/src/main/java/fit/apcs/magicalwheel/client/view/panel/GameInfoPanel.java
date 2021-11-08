package fit.apcs.magicalwheel.client.view.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class GameInfoPanel extends JPanel {

    private final JLabel keywordLabel;
    private final JLabel hintLabel;

    public GameInfoPanel(int keywordLength, String hint) {
        keywordLabel = new JLabel();
        hintLabel = new JLabel();
        hintLabel.setText("<html><p style=\"width:350px\">" + hint + "</p></html>");
        setNewKeyword(new String(new char[10]).replace('\0', '*'));
        setOpaque(false);
        setLayout(new BorderLayout());
        add(keywordPanel(), BorderLayout.WEST);
    }

    public void setNewKeyword(String keyword) {
        keywordLabel.setText(keywordFormat(keyword));
    }

    private String keywordFormat(String keyword) {
        return keyword;
    }

    private JPanel keywordPanel() {
        final var keywordPanel = new JPanel(new GridBagLayout());
        final var gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(10,0,0,0);
        keywordLabelDecor();
        hintLabelDecor();
        keywordPanel.setOpaque(false);
        keywordPanel.add(keywordLabel, gbc);
        gbc.anchor = GridBagConstraints.LINE_START;
        keywordPanel.add(hintLabel, gbc);
        return keywordPanel;
    }

    private void hintLabelDecor() {
        hintLabel.setForeground(Color.WHITE);
        hintLabel.setFont(new Font("Courier New", Font.PLAIN, 12));
    }

    private void keywordLabelDecor() {
        keywordLabel.setForeground(Color.WHITE);
        keywordLabel.setFont(new Font("Courier New", Font.BOLD, 20));
        keywordLabel.setHorizontalAlignment(JLabel.CENTER);
    }
}
