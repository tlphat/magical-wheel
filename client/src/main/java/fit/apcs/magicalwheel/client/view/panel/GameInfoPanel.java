package fit.apcs.magicalwheel.client.view.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.Serial;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class GameInfoPanel extends JPanel {

    @Serial
    private static final long serialVersionUID = 1113033786957875666L;

    private final JLabel keywordLabel;
    private final JLabel hintLabel;

    public GameInfoPanel(int keywordLength, String hint) {
        keywordLabel = new JLabel();
        hintLabel = new JLabel();
        hintLabel.setText("<html><p style=\"width:350px\">" + hint + "</p></html>");
        setNewKeyword(new String(new char[keywordLength]).replace('\0', '*'));
        intLayout();
    }

    private void intLayout() {
        setOpaque(false);
        add(keywordPanel());
    }

    public void setNewKeyword(String keyword) {
        keywordLabel.setText(keywordFormat(keyword));
    }

    @SuppressWarnings("MethodMayBeStatic")
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
        hintLabel.setFont(new Font("Courier New", Font.PLAIN, 13));
    }

    private void keywordLabelDecor() {
        keywordLabel.setForeground(Color.WHITE);
        keywordLabel.setFont(new Font("Courier New", Font.BOLD, 20));
        keywordLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

}
