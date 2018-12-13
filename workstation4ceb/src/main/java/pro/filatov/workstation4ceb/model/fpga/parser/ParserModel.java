package pro.filatov.workstation4ceb.model.fpga.parser;

import pro.filatov.workstation4ceb.form.editor.ILongProcessEventListener;
import pro.filatov.workstation4ceb.model.Model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuri.filatov on 03.08.2016.
 */
public class ParserModel {

    private Map<BlockCode, Map<String, Variable>> variables = new HashMap<>();

    private Boolean isParsing;
    private List<ILongProcessEventListener> listLongProcessEventListenerList = new ArrayList<>();

    private Boolean generateMifFiles = false;

    private List<File> currentParsingFiles = new ArrayList<>();
    private Map<ModeAttrFile, List<String>>  hexData = new HashMap<>();

    public void parseCurrentFile(){
        setParsing(true);
        currentParsingFiles.clear();
        currentParsingFiles.add(Model.getEditorModel().getCurrentFile());
        new AsmParser().start();
    }

    public void parseAllFiles(){
        setParsing(true);
        currentParsingFiles.clear();
        File parentFile = Model.getEditorModel().getCurrentFile().getParentFile();
        for(File file : parentFile.listFiles()){
            if(file.getName().contains("txt")){
                currentParsingFiles.add(file);
            }
        }
        for(File file : parentFile.listFiles()){
            if(file.getName().contains("asm")){
                currentParsingFiles.add(file);
            }
        }
        new AsmParser().start();
    }





    public Thread parseSelectedFiles(List<File>selectedFiles){
        currentParsingFiles = selectedFiles;
        setParsing(true);
        AsmParser thread = new AsmParser();
        thread.start();
        return thread;
    }

    public List<File> getCurrentParsingFiles() {
        return currentParsingFiles;
    }


    public Variable getVariable( BlockCode blockCode, String name){
        Map<String, Variable> map = variables.get(blockCode);
        if (map != null) {
            Variable variable = map.get(name);
            //if(variable == null) {
            //    System.out.println("Variable: " + name + " not found!");
            //}
            return variable;
        }else
        {
            System.out.println("Variables for block:" + blockCode + " not initialized!");
            return null;
        }
    }

    public Map<String, Variable>    getBlockVariables(BlockCode blockCode){
        return variables.get(blockCode);
    }

    public void setVariables(BlockCode blockCode, Map<String , Variable> newVariables){
        variables.put(blockCode, newVariables);
    }


    public Map<ModeAttrFile, List<String>> getHexData() {
        return hexData;
    }

    public Boolean getParsing() {
        return isParsing;
    }

    public void setParsing(Boolean parsing) {
        isParsing = parsing;
        for(ILongProcessEventListener listener : listLongProcessEventListenerList){
            listener.updateStatusOfParsing();
        }
    }

    public void addLongProcessEventListener(ILongProcessEventListener listener){
        listLongProcessEventListenerList.add(listener);
    }

    public Boolean getGenerateMifFiles() {
        return generateMifFiles;
    }

    public void setGenerateMifFiles(Boolean generateMifFiles) {
        this.generateMifFiles = generateMifFiles;
    }
}
