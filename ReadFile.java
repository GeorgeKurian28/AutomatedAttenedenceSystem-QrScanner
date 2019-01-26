package com.george.softwareenginnering;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ReadFile {
    //private static final String FILENAME = "C:\\Users\\George\\Documents\\software engineering\\images\\file1.txt";

    public  String readFile(String path) {

        String read = "";

        BufferedReader br = null;
        FileReader fr = null;

        try {

            //br = new BufferedReader(new FileReader(FILENAME));
            fr = new FileReader(path);
            br = new BufferedReader(fr);

            String sCurrentLine;

            if ((sCurrentLine = br.readLine()) != null) {

                System.out.println(sCurrentLine);
                read = sCurrentLine;

            }

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
        return read;

    }
}
