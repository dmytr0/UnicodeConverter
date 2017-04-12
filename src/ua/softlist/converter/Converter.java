package ua.softlist.converter;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Converter {
    private static String cyrillicApphabet ="АаБбВвГгДдЕеЁёЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщЪъЫыЬьЭэЮюЯя" +
            "АаБбВвГгҐґДдЕеЄєЖжЗзИиІіЇїЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщЬьЮюЯя";

    private static String homeDir = System.getProperty("user.dir");

    public static void main(String[] args) throws IOException {
        String filename;
        Converter converter = new Converter();
        if(args.length>0) {
            filename = args[0];
            File file = new File(filename);
            converter.convertFile(file);
        }else{
            System.out.println(homeDir + " directory will be scanned!");
            Collection<File> all = new ArrayList<File>();
            converter.addTree(new File(homeDir), all);
            for (File file: all){
                converter.convertFile(file);
            }
        }
    }
    private void convertFile(File file){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), "UTF8"));
            String currentLine;
            StringBuilder sb = new StringBuilder();
            while ((currentLine = br.readLine()) != null) {
                sb.append(currentLine + "\r\n");
            }
            br.close();
            String output = convertToASCII(sb.toString());
            String convertedFile = getDestinationPath() + file.getName();
            FileWriter fw = new FileWriter(convertedFile);
            System.out.println("File was converted:\n" + file + " >> " + convertedFile);
            fw.write(output);
            fw.flush();
            fw.close();

        } catch (UnsupportedEncodingException e) {
            System.err.println("Unsupported Encoding! " + e);
        } catch (FileNotFoundException e) {
            System.err.println("File not found! " + e);
        } catch (IOException e) {
            System.err.println("IOException! " + e);
        }

    }

    private String convertToASCII(String input){
        StringBuilder sb = new StringBuilder();

        char[] inputChars = input.toCharArray();
        for (char ch : inputChars) {
            String unicode = ch + "";
            if(getCyrillic().contains(ch)){
                unicode = Integer.toHexString(ch);
                if(unicode.length()<4){
                    unicode = 0 + unicode;
                }
                unicode = "\\u" + unicode;

            }
            sb.append(unicode);
        }
        return sb.toString();
    }

    private List<Character> getCyrillic(){
        char[] cyrillicArray = cyrillicApphabet.toCharArray();
        List<Character> charactersList = new ArrayList<>();
        for (int index = 0; index < cyrillicArray.length; index++)
        {
            charactersList.add(cyrillicArray[index]);
        }

        return charactersList;
    }


    private String getDestinationPath(){
        File file = new File(homeDir+"\\created");
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Failed to create directory!");
            }
        }
        return file.getPath() + "\\";
    }

    private void addTree(File file, Collection<File> all) {
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                if(!child.isDirectory() && child.getName().endsWith(".properties")) {
                    all.add(child);
                }else{
                    if(!child.getName().equalsIgnoreCase("created")) {
                        addTree(child, all);
                    }
                }
            }
        }
    }

}