package PDH;

import org.docx4j.openpackaging.exceptions.Docx4JException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;

import static javax.swing.JOptionPane.showMessageDialog;

public class PDHmain extends JDialog {

    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem exitMenuItem, helpMenuItem;
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField provField;
    private JTextField pmanField;
    private JTextField pmanemailField;
    private JButton chooseAFileButton;
    private JLabel directoryField;
    private JButton genButton;
    private JFileChooser fileChooser = new JFileChooser();
    private int result = JFileChooser.CANCEL_OPTION;

    public PDHmain(){
        this.setLocationRelativeTo(null);
        setContentPane(mainPanel);
        setModal(true);

        menuBar = new JMenuBar();
        menu = new JMenu("File");
        helpMenuItem = new JMenuItem("Help");
        menu.add(helpMenuItem);
        exitMenuItem = new JMenuItem("Exit");
        menu.add(exitMenuItem);
        menuBar.add(menu);
        this.setJMenuBar(menuBar);

        helpMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMessageDialog(mainPanel, "<html>" +
                        "<center>" +
                        "Welcome in the IPC Project Documentation Helper app!<br><br>" +
                        "<br><br>" +
                        "If you interested in please visit GitHub via this link:<br>" +
                        "<a href='https://www.github.com/'>https://github.com/</a>" +
                        "</center>" +
                        "" +
                        "</html>");
            }
        });

        chooseAFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                result = fileChooser.showDialog(mainPanel, "Select");
                if(JFileChooser.APPROVE_OPTION != result){
                    showMessageDialog(mainPanel, "Please select a directory!", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        mainPanel.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        genButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(usernameField.getText() !="" && passwordField.getText() !="" && provField.getText() !="" && directoryField.getText() !=""){
                    /*pmanField;
                    pmanemailField*/
                    try {
                        GenerateDoc.genDoc(new String[]{usernameField.getText(), passwordField.getText(), provField.getText(), directoryField.getText()});
                    } catch (Docx4JException ex) {
                        ex.printStackTrace();
                    }
                }else{
                    showMessageDialog(mainPanel, "Please fill the necessary fields!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) throws Docx4JException {
        PDHmain frame = new PDHmain();
        frame.pack();
        frame.setVisible(true);
    }


}
