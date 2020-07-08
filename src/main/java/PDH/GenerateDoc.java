package PDH;

import org.docx4j.openpackaging.exceptions.Docx4JException;

import java.io.*;
import java.util.Scanner;

public class GenerateDoc {

    public static void genDoc(String[] data) throws Docx4JException {
        File document = new File(System.getProperty("user.dir") + "/unzipped/word/document.xml");
        File newDocument = new File(System.getProperty("user.dir") + "/test.xml");
        String oldContent = null, newContent = null;
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
                oldContent += "\n"+scanner.nextLine();
            }
            //Replacing oldString with newString in the oldContent
            //String newContent = oldContent.replaceAll(oldString, newString);
            for (int i=0; i<data.length; i++) {
                newContent = oldContent.replaceAll("<w:t>"+old[i]+"</w:t>", "<w:t>"+data[i]+"</w:t>");
            }
            //Rewriting the input text file with newContent
            writer = new FileWriter(newDocument);
            writer.write(newContent);
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

        /*String inputfilepath = System.getProperty("user.dir") + "/unzipped";
        System.out.println(inputfilepath);

        // Load the docx
        File baseDir = new File(inputfilepath);
        UnzippedPartStore partLoader = new UnzippedPartStore(baseDir);
        final Load3 loader = new Load3(partLoader);
        OpcPackage opc = loader.get();

        // Save it zipped
        File docxFile = new File(System.getProperty("user.dir") + "/zip.docx");

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
        }*/
    }

    public static void main(String[] args) throws Docx4JException {
        genDoc(new String[]{"111","222","333","444"});
    }

}
