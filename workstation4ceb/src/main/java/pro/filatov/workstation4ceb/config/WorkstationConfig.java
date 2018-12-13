package pro.filatov.workstation4ceb.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Created by yuri.filatov on 15.07.2016.
 */
public class WorkstationConfig {

    private static final String CONFIG_FILE_NAME = "./config.properties";
    private Properties properties;

    private static WorkstationConfig instance;

    public WorkstationConfig() {
        try{
            properties = new Properties();
            properties.load(new FileInputStream(CONFIG_FILE_NAME));
        } catch ( Exception e){
            e.printStackTrace();
        }
    }

    private static WorkstationConfig getInstance() {
        if(instance == null){
            instance = new WorkstationConfig();
        }
        return  WorkstationConfig.instance;
    }


    public static String getProperty(ConfProp property){
        return  getInstance().getProperties().getProperty(property.name());
    }

    public static String getProperty(String property){
        return  getInstance().getProperties().getProperty(property);
    }

    public static void setProperty(ConfProp property, String newValue ){
        getInstance().getProperties().setProperty(property.name(), newValue);
        storeProperties();
    }
    public static void setProperty(String property, String newValue ){
        getInstance().getProperties().setProperty(property, newValue);
        storeProperties();
    }


    public static void storeProperties(){
        try {
            getInstance().getProperties().store(new FileOutputStream(new File(CONFIG_FILE_NAME)), "");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private Properties getProperties() {
        return properties;
    }


}
