import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;

public class GUI {
    public JTextField geneInput;
    public JTextField tpmInput;
    public JLabel warningLabel;
    public JTextArea output;
    public JComboBox <String> geneIDList;
    private JPanel layout;
    private JButton copyButton;
    private JButton runButton;
    private JRadioButton ctaRadio;
    private JRadioButton neoRadio;
    private JLabel geneLabel;

    public void runMe() {

        runButton.addActionListener(e -> {
            output.setText("");
            clearWarningLabel();
            geneIDList.setVisible(false);
            geneIDList.removeAllItems();

            try {
                if(ctaRadio.isSelected()) {
                    MerMaker.cta();
                } else if (neoRadio.isSelected()) {
                    MerMaker.neo();
                }
            } catch (IOException | IllegalArgumentException ex) {
                warningLabel.setText(ex.getMessage());
            }
        });

        ctaRadio.addActionListener(e -> {
            clearWarningLabel();
            geneLabel.setText("Enter gene name");
        });

        neoRadio.addActionListener(e -> {
            clearWarningLabel();
            geneLabel.setText("Enter gene mutation");
        });

        copyButton.addActionListener(e -> {
            StringSelection selection = new StringSelection(output.getText().trim());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        });

        geneIDList.addActionListener(e -> {
            if(!geneIDList.isVisible()) {
                return;
            }

            try {
                if (ctaRadio.isSelected()) {
                    MerMaker.cta();
                } else if (neoRadio.isSelected()) {
                    MerMaker.neo();
                }
            } catch (IOException | IllegalArgumentException ex) {
                warningLabel.setText(ex.getMessage());
            }
        });

        JFrame frame = new JFrame();
        frame.setSize(600,750);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(layout);
        geneIDList.setVisible(false);
        frame.setVisible(true);
    }

    public void clearWarningLabel() {
        warningLabel.setText("");
    }

}
