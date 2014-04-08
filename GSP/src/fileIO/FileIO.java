/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fileIO;

import java.io.*;

/**
 *
 * @author ricardo
 */
public class FileIO {

    File f;

    public FileIO(String filename) {
        f = new File(filename);
    }

    public void filePrint(String toPrint, boolean append) throws IOException {
        f.createNewFile();
        FileWriter fw = new FileWriter(f, append);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write(toPrint);
        bw.newLine();

        bw.close();
        fw.close();
    }

    public void delete() {
        if (f.exists()) {
            f.delete();
        }
    }

    public void dispose() {
        f = null;
    }
}
