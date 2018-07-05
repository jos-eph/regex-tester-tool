package com.vgrazi.regextester.component;

import com.vgrazi.regextester.action.Colorizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.regex.Pattern;

import static com.vgrazi.regextester.component.Constants.DEFAULT_LABEL_FONT;
import static com.vgrazi.regextester.component.Constants.DEFAULT_PANE_FONT;

public class RegexTester {

    private static int flags;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Regex Test Tool");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(50);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        JLabel patternJlabel = new JLabel("Pattern  ");
        patternJlabel.setBackground(Color.LIGHT_GRAY);
        patternJlabel.setFont(DEFAULT_LABEL_FONT);
        topPanel.add(patternJlabel, BorderLayout.WEST);

        splitPane.add(topPanel);

        JSplitPane bottomPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(.8d);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        JTextPane characterPane = new JTextPane();
        formatCharacterPane(characterPane);
        bottomPanel.add(characterPane, BorderLayout.CENTER);
        JTextPane auxiliaryPanel = new JTextPane();
        auxiliaryPanel.setFont(DEFAULT_PANE_FONT);

        PatternPane patternPane = new PatternPane(characterPane, auxiliaryPanel);

        ButtonGroup buttonGroup = new ButtonGroup();
        JPanel buttonPanel = createButtonPanel(patternPane, characterPane, auxiliaryPanel, buttonGroup);
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);

        formatPatternPane(patternPane);

        topPanel.add(patternPane, BorderLayout.CENTER);

        KeyAdapter keyListener = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                renderCharacterPane(characterPane, patternPane, auxiliaryPanel, buttonGroup);
            }
        };
        characterPane.addKeyListener(keyListener);
        patternPane.addKeyListener(keyListener);

        bottomPane.add(bottomPanel);
        bottomPane.add(auxiliaryPanel);
        splitPane.add(bottomPane);

        frame.getContentPane().add(splitPane);

        frame.setBounds(100, 100, 1000, 600);
        frame.setVisible(true);
    }

    private static void formatPatternPane(PatternPane patternPane) {
        patternPane.setFont(Constants.DEFAULT_PANE_FONT);
        patternPane.setForeground(Constants.FONT_COLOR);
        patternPane.setBackground(Constants.BACKGROUND_COLOR);
    }

    private static void formatCharacterPane(JTextPane characterPane) {
        characterPane.setForeground(Constants.FONT_COLOR);
        characterPane.setBackground(Constants.BACKGROUND_COLOR);
        characterPane.setFont(Constants.DEFAULT_PANE_FONT);

        characterPane.getStyledDocument().addStyle("highlights", null);
    }

    private static JPanel createButtonPanel(PatternPane patternPane, JTextPane characterPane, JTextPane auxiliaryPanel, ButtonGroup buttonGroup) {
        JRadioButton matchButton = new JRadioButton("Matches");
        JRadioButton lookingAtButton = new JRadioButton("Looking at");
        JRadioButton splitButton = new JRadioButton("Split");
        JRadioButton replaceButton = new JRadioButton("Replace");
        JRadioButton findButton = new JRadioButton("Find");
        findButton.setSelected(true);

        findButton.setActionCommand("find");
        matchButton.setActionCommand("matches");
        lookingAtButton.setActionCommand("looking-at");
        splitButton.setActionCommand("split");
        replaceButton.setActionCommand("replace");

        buttonGroup.add(findButton);
        buttonGroup.add(matchButton);
        buttonGroup.add(lookingAtButton);
        buttonGroup.add(splitButton);
        buttonGroup.add(replaceButton);

        findButton.setFont(DEFAULT_LABEL_FONT);
        matchButton.setFont(DEFAULT_LABEL_FONT);
        lookingAtButton.setFont(DEFAULT_LABEL_FONT);
        splitButton.setFont(DEFAULT_LABEL_FONT);
        replaceButton.setFont(DEFAULT_LABEL_FONT);

        JPanel buttonPanel = new JPanel();

        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(findButton);
        buttonPanel.add(matchButton);
        buttonPanel.add(lookingAtButton);
        buttonPanel.add(splitButton);
        buttonPanel.add(replaceButton);
        buttonPanel.add(Box.createHorizontalGlue());
        JCheckBox caseButton = new JCheckBox("Case Insensitive");
        JCheckBox commentsButton = new JCheckBox("Comments");
        JCheckBox dotallButton = new JCheckBox("Dot All");
        JCheckBox literalButton = new JCheckBox("Literal");
        JCheckBox multilineButton = new JCheckBox("Multiline");
        buttonPanel.add(caseButton);
        buttonPanel.add(commentsButton);
        buttonPanel.add(dotallButton);
        buttonPanel.add(literalButton);
        buttonPanel.add(multilineButton);

        caseButton.setFont(DEFAULT_LABEL_FONT);
        commentsButton.setFont(DEFAULT_LABEL_FONT);
        dotallButton.setFont(DEFAULT_LABEL_FONT);
        literalButton.setFont(DEFAULT_LABEL_FONT);
        multilineButton.setFont(DEFAULT_LABEL_FONT);

        ActionListener recalcFlagListener = e -> {
            flags = recalculateFlags(caseButton, commentsButton, dotallButton, literalButton, multilineButton);
            renderCharacterPane(characterPane, patternPane, auxiliaryPanel, buttonGroup);
            patternPane.setFlags(flags);
        };
        caseButton.addActionListener(recalcFlagListener);
        commentsButton.addActionListener(recalcFlagListener);
        dotallButton.addActionListener(recalcFlagListener);
        literalButton.addActionListener(recalcFlagListener);
        multilineButton.addActionListener(recalcFlagListener);
        ActionListener actionListener = e -> renderCharacterPane(characterPane, patternPane, auxiliaryPanel, buttonGroup);
        findButton.addActionListener(actionListener);
        lookingAtButton.addActionListener(actionListener);
        matchButton.addActionListener(actionListener);
        replaceButton.addActionListener(actionListener);
        splitButton.addActionListener(actionListener);
        return buttonPanel;
    }

    private static int recalculateFlags(JCheckBox caseButton, JCheckBox commentsButton, JCheckBox dotallButton, JCheckBox literalButton, JCheckBox multilineButton) {
        int flags = 0;
        flags |= caseButton.isSelected()? Pattern.CASE_INSENSITIVE:0;
        flags |= commentsButton.isSelected()? Pattern.COMMENTS:0;
        flags |= dotallButton.isSelected()? Pattern.DOTALL:0;
        flags |= literalButton.isSelected()? Pattern.LITERAL:0;
        flags |= multilineButton.isSelected()? Pattern.MULTILINE:0;
        return flags;
    }

    private static void renderCharacterPane(JTextPane characterPane, PatternPane patternPane, JTextPane auxiliaryPanel, ButtonGroup buttonGroup) {
        try {
            int caret = characterPane.getCaretPosition();
            // replace \n\r with \r to prevent upsetting count
            if(characterPane.getText().contains("\\n\\r")) {
                String text = characterPane.getText().replaceAll("\\n\\r", "\\r");
                characterPane.setText(text);
                characterPane.setCaretPosition(caret);
            }
            Colorizer.renderCharacterPane(characterPane, auxiliaryPanel, patternPane.getText(), buttonGroup.getSelection().getActionCommand(), flags);
            patternPane.setBorder(Constants.WHITE_BORDER);
        } catch (Exception e) {
            patternPane.setBorder(Constants.RED_BORDER);

        }
    }

}
