package pro.filatov.workstation4ceb.model.editor;

import pro.filatov.workstation4ceb.config.ConfProp;
import pro.filatov.workstation4ceb.config.WorkstationConfig;
import pro.filatov.workstation4ceb.form.AppFrameHelper;
import pro.filatov.workstation4ceb.model.Model;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuri.filatov on 08.08.2016.
 */
public class FileHelper {

    private static final int BUFFER_SIZE = 8912;



    public static void createOutputFile(ConfProp prop, String fileName, List<String> list, String ext){
        File path = new File(WorkstationConfig.getProperty(prop)) ;
        if(!path.isDirectory()) {
            path = path.getParentFile();
        }
        int indexPoint =fileName.indexOf(".");
        String name =fileName.substring(0, indexPoint);

        Path outFile = Paths.get(path.getPath()+ File.separator  + name + ext);
        try {
            Files.write(outFile, list, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static List<Integer> getValuesFromFile(String filename, Integer count, String strPath){

        File path;
        List<Integer> res = new ArrayList<>();

        if(strPath == null || strPath == "") {
            if (!filename.contains(File.separator)) {
                File file = new File(WorkstationConfig.getProperty(ConfProp.FILE_PATH_HEX_CODES));
                if (!file.isDirectory()) {
                    file = file.getParentFile();
                }
                path = Paths.get(file.getPath() + File.separator + filename).toFile();
            } else {
                path = Paths.get(filename).toFile();
            }
        }else {
            path = Paths.get(strPath + File.separator + filename).toFile();
        }
        BufferedReader br = null;
        InputStream in = null;
        try {
            in = new FileInputStream(path);
            br = new BufferedReader(new InputStreamReader(in, "UTF-8"), BUFFER_SIZE);
            String line;
            int k =1;
            while((line = br.readLine())!= null && k <= count ){
                res.add(Integer.parseInt(line));
                k++;
            }
            if(k < count){
                System.out.println(""+  filename +  " file consist less values than "+ count.toString() +" !");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }



    public static void createFile(ConfProp prop, String fileName, List<String> list){
        File path = new File(WorkstationConfig.getProperty(prop)) ;
        if(!path.isDirectory()) {
            path = path.getParentFile();
        }
        Path outFile = Paths.get(path.getPath()+ File.separator  + fileName);
        try {
            Files.write(outFile, list, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void createFile(Path Path, List<String> list){
        try {
            Files.write(Path, list, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public static void createFile(String path,  List<String> text){


        Path outFile = Paths.get(path);
        try {
            Files.write(outFile, text, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
