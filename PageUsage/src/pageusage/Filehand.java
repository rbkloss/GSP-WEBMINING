package pageusage;

import java.io.*;

public class Filehand {

    static int i = 0;

    static void createFile(String filename) throws IOException {
        File f;
        f = new File(filename);
        if (!f.exists()) {
            f.createNewFile();
            //System.out.println("New file " + filename + " has been created to the current directory");
        } else {
            //System.out.println("file already exists");
        }

    }

    static void deleteFile(String filename) {
        File f;
        f = new File(filename);
        if (f.exists()) {
            f.delete();
            System.out.println("file " + filename + " has been deleted");
        } else {
            return;
        }
    }

    static int getLinesonFile(String filename) throws IOException {

        int numberofLines = 0;

        BufferedReader br = null;
        FileReader fr = null;

        fr = new FileReader(filename);
        br = new BufferedReader(fr);

        String strLine;

        while ((strLine = br.readLine()) != null) {
            // Print the content on the console
            numberofLines++;
        }

        return numberofLines;
    }

    static void fileRead(String filename) throws IOException {

        BufferedReader br = null;
        FileReader fr = null;

        try {
            fr = new FileReader(filename);
            br = new BufferedReader(fr);

            String strLine;

            while ((strLine = br.readLine()) != null) {
                // Print the content on the console
                System.out.println(strLine);
            }


        } catch (IOException e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        } finally {
            if (br != null) {
                br.close();
            }
            if (fr != null) {
                fr.close();
            }
        }
    }

    static void fileWriteln(String filename, String ln, boolean append) throws IOException {

        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter pw = null;

        try {

            fw = new FileWriter(filename, append);

            bw = new BufferedWriter(fw);

            pw = new PrintWriter(bw);


            pw.println(ln);



        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            pw.close();
            bw.close();
            fw.close();
        }


    }

    static void fileSpliting(String filename) throws IOException {

        FileReader fr = null;
        BufferedReader fbr = null;

        String Line = null;

        try {
            fr = new FileReader(filename);
            fbr = new BufferedReader(fr);




            while ((Line = fbr.readLine()) != null) {
                String splited[];
                splited = Line.split(";");

                for (i = 0; i < splited.length; i++) {
                    System.out.println(splited[i]);
                }

            }



        } catch (IOException e) {
            System.out.println("ERROR : " + e.getMessage());
        } finally {
            fbr.close();
            fr.close();
        }
    }

    static void fileNumberReading(String filename) throws IOException {
        InputStream fin;
        try {
            fin = new FileInputStream(filename);

            BufferedReader br = new BufferedReader(new InputStreamReader(fin));

            String Line;

            while ((Line = br.readLine()) != null) {
                String splited[];
                splited = Line.split(";");


                for (i = 0; i < splited.length; i++) {
                    splited[i] = splited[i].trim();
                    try {
                        Integer.parseInt(splited[i]);
                        System.out.println("numero : " + splited[i]);
                    } catch (NumberFormatException nfe) {
                        System.out.println("não número: " + splited[i]);
                    }
                }

            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    static void generateRandomFile() throws IOException {
        String fn = "Randomfile.txt";
        Filehand.createFile(fn);
        int j;
        int sessionSize;
        int page;
        int numberofLines = 100 + (int) (Math.random() * (Integer.MAX_VALUE / 1000));
        System.out.println(Integer.toString(numberofLines));
        String output = new String();

        for (i = 0; i < numberofLines; i++) {//define quantas linhas de arquivo
            sessionSize = 1 + ((int) (Math.random() * 10));//define o tamanho da sessão
            for (j = 0; j < sessionSize; j++) {
                page = 1 + (int) (Math.random() * 55);
                System.out.print(Integer.toString(page));
                output += Integer.toString(page);//define a página a ser escrita
                if (j != sessionSize - 1) {
                    output += ",";
                    System.out.print(",");
                }
            }

            System.out.println();
            Filehand.fileWriteln(fn, output, true);
            output = "";
        }
    }
}