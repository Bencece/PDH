package PDH;

import com.jcraft.jsch.*;
import org.apache.commons.io.IOUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.io3.Load3;
import org.docx4j.openpackaging.io3.Save;
import org.docx4j.openpackaging.io3.stores.UnzippedPartStore;
import org.docx4j.openpackaging.io3.stores.ZipPartStore;
import org.docx4j.openpackaging.packages.OpcPackage;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Scanner;

public class GenerateDoc {

    public static void genDoc(String directory, String[] data) throws Docx4JException {
        File document = new File(System.getProperty("user.dir") + "/data/document.xml");
        File newDocument = new File(System.getProperty("user.dir") + "/unzipped/word/document.xml");
        String content = "", newcontent = "";
        Scanner scanner = null;
        FileWriter writer = null;
        String old[] = {
                "nwName",
                "vrfName",
                "ipcName",
                "pmName"
        };
        try {
            scanner = new Scanner(document);
            //Reading all the lines of input text file into oldContent
            while (scanner.hasNextLine()) {
                content += scanner.nextLine();
            }
            //Replacing oldString with newString in the oldContent
            //String newContent = oldContent.replaceAll(oldString, newString);
            newcontent = content;
            for (int i=0; i<data.length; i++) {
                newcontent = newcontent.replace(old[i], data[i]);
                //System.out.println("<w:t>"+old[i]+"</w:t>\n<w:t>"+data[i]+"</w:t>");
            }
            //Rewriting the input text file with newContent
            writer = new FileWriter(newDocument);
            writer.write(newcontent);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //Closing the resources
                scanner.close();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String inputfilepath = System.getProperty("user.dir") + "/unzipped";
        System.out.println(inputfilepath);

        // Load the docx
        File baseDir = new File(inputfilepath);
        UnzippedPartStore partLoader = new UnzippedPartStore(baseDir);
        final Load3 loader = new Load3(partLoader);
        OpcPackage opc = loader.get();

        // Save it zipped
        File docxFile = new File(directory+"/Project_description_"+data[0]+".docx");

        ZipPartStore zps = new ZipPartStore();
        zps.setSourcePartStore(opc.getSourcePartStore());

        Save saver = new Save(opc, zps);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(docxFile);
            saver.save(fos);
        } catch (FileNotFoundException e) {
            throw new Docx4JException("Couldn't save " + docxFile.getPath(), e);
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    public static void runCommands(){
        try{
            JSch jsch=new JSch();

            String host = JOptionPane.showInputDialog("Enter username@hostname", "zeusz@192.168.0.172");

            String user=host.substring(0, host.indexOf('@'));
            host=host.substring(host.indexOf('@')+1);

            Session session=jsch.getSession(user, host, 22);

            // username and password will be given via UserInfo interface.
            UserInfo ui=new MyUserInfo();
            session.setUserInfo(ui);

            session.connect();

            Channel channel=session.openChannel("shell");

            /*channel.setInputStream(System.in);
            channel.setOutputStream(System.out);*/
            try{
                FileInputStream fin = new FileInputStream(System.getProperty("user.dir") + "/data/commands.txt");
                channel.setInputStream(fin);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try{
                FileOutputStream fout = new FileOutputStream(System.getProperty("user.dir") + "/data/rawoutput.txt");
                channel.setOutputStream(fout);
                channel.connect();
                //fout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*channel.disconnect();
            session.disconnect();*/
        }
        catch(Exception e){
            System.out.println(e);
        }

    }

    public static class MyUserInfo implements UserInfo, UIKeyboardInteractive {
        public String getPassword(){ return passwd; }
        public boolean promptYesNo(String str){
            Object[] options={ "yes", "no" };
            int foo=JOptionPane.showOptionDialog(null,
                    str,
                    "Warning",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null, options, options[0]);
            return foo==0;
        }

        String passwd;
        JTextField passwordField=(JTextField)new JPasswordField(20);

        public String getPassphrase(){ return null; }
        public boolean promptPassphrase(String message){ return true; }
        public boolean promptPassword(String message){
            Object[] ob={passwordField};
            int result=
                    JOptionPane.showConfirmDialog(null, ob, message,
                            JOptionPane.OK_CANCEL_OPTION);
            if(result==JOptionPane.OK_OPTION){
                passwd=passwordField.getText();
                return true;
            }
            else{ return false; }
        }
        public void showMessage(String message){
            JOptionPane.showMessageDialog(null, message);
        }
        final GridBagConstraints gbc =
                new GridBagConstraints(0,0,1,1,1,1,
                        GridBagConstraints.NORTHWEST,
                        GridBagConstraints.NONE,
                        new Insets(0,0,0,0),0,0);
        private Container panel;
        public String[] promptKeyboardInteractive(String destination,
                                                  String name,
                                                  String instruction,
                                                  String[] prompt,
                                                  boolean[] echo){
            panel = new JPanel();
            panel.setLayout(new GridBagLayout());

            gbc.weightx = 1.0;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.gridx = 0;
            panel.add(new JLabel(instruction), gbc);
            gbc.gridy++;

            gbc.gridwidth = GridBagConstraints.RELATIVE;

            JTextField[] texts=new JTextField[prompt.length];
            for(int i=0; i<prompt.length; i++){
                gbc.fill = GridBagConstraints.NONE;
                gbc.gridx = 0;
                gbc.weightx = 1;
                panel.add(new JLabel(prompt[i]),gbc);

                gbc.gridx = 1;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.weighty = 1;
                if(echo[i]){
                    texts[i]=new JTextField(20);
                }
                else{
                    texts[i]=new JPasswordField(20);
                }
                panel.add(texts[i], gbc);
                gbc.gridy++;
            }

            if(JOptionPane.showConfirmDialog(null, panel,
                    destination+": "+name,
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE)
                    ==JOptionPane.OK_OPTION){
                String[] response=new String[prompt.length];
                for(int i=0; i<prompt.length; i++){
                    response[i]=texts[i].getText();
                }
                return response;
            }
            else{
                return null;  // cancel
            }
        }
    }

    public static void main(String[] args) throws Docx4JException {
        //genDoc("C:\\Users\\zahor\\Desktop\\PDH",new String[]{"111","222","333","444"});
        runCommands();
    }

}
