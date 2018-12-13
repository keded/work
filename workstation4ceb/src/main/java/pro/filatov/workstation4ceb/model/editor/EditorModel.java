package pro.filatov.workstation4ceb.model.editor;

import pro.filatov.workstation4ceb.config.ConfProp;
import pro.filatov.workstation4ceb.config.WorkstationConfig;
import pro.filatov.workstation4ceb.form.editor.IAppFrameEventListener;
import pro.filatov.workstation4ceb.form.editor.IDataEditorListener;
import pro.filatov.workstation4ceb.form.editor.IModelEditorEventListener;
import pro.filatov.workstation4ceb.model.Model;
import pro.filatov.workstation4ceb.model.fpga.parser.ModeAttrFile;
import pro.filatov.workstation4ceb.model.fpga.parser.BlockCode;
import pro.filatov.workstation4ceb.model.fpga.parser.FileType;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuri.filatov on 15.07.2016.
 */
public class EditorModel {

    private IDataEditorListener dataEditorListener;
    private List<IAppFrameEventListener>  appFrameEventListeners = new ArrayList<IAppFrameEventListener>();

    private File currentFile;
    private ModeAttrFile currentAttr;
    private String MODE_FILE_NAME = "MODE.CONFIG";
    private final int BUFFER_SIZE = 8912;
    private Map<File, OutputCodes>  outputCodes = new HashMap<>();
    private String pathHexFiles;
    private String pathMifFiles;
    private IModelEditorEventListener modelEditorEventListener;
    private Integer activeTab = 0;
    private Map<File, ModeAttrFile> fileAttrs = new HashMap<>();




    public EditorModel() {
        setPathHexFiles(WorkstationConfig.getProperty(ConfProp.FILE_PATH_HEX_CODES));
        setPathMifFiles(WorkstationConfig.getProperty(ConfProp.FILE_PATH_MIF_CODES));
        initModeConfigAttrs();

    }

    public void setModelEditorEventListener(IModelEditorEventListener modelEditorEventListener) {
        this.modelEditorEventListener = modelEditorEventListener;
    }

    public void saveCurrentFile(){
        dataEditorListener.saveFile();
    }

    public void cleanTextErrors(){
        modelEditorEventListener.cleanTextErorrs();
    }


    public void addDataEditorListener(IDataEditorListener listener){
        dataEditorListener = listener;
    }

    public void setOutputCodes(File file, String clearAssemblerCodes, String textHexCodes, String binaryCodes ){
        outputCodes.put(file, new OutputCodes(clearAssemblerCodes, textHexCodes, binaryCodes));
        //dataEditorListener.updateOutputCodes();
    }

    public String getCurrentText(){
        return dataEditorListener.getCurrentText();
    }


    public void addAppFrameEventListener(IAppFrameEventListener listener){
        appFrameEventListeners.add(listener);
    }


    public void  notifyAllOfUpdateCurrentFile(File newFile, File oldFile ){
        for(IAppFrameEventListener listener : appFrameEventListeners){
            listener.updateCurrentFile(newFile, oldFile);
        }
    }

    public Integer getActiveTab() {
        return activeTab;
    }

    public void openActiveTab(Integer activeTab) {
        this.activeTab = activeTab;
        modelEditorEventListener.updateCurrentTab();
    }

    public void setActiveTab(Integer activeTab) {
        this.activeTab = activeTab;
    }

    public File getCurrentFile() {
        if(currentFile == null){
            String path = WorkstationConfig.getProperty(ConfProp.FILE_PATH_CURRENT_FILE);
            if(path != null){
                currentFile = new File(Paths.get(path).toUri());
                if(currentFile.exists()){
                    return currentFile;
                }
            }
            path = System.getProperty("user.home") + File.separator + "Documents";
            currentFile = new File(path);
            if(!currentFile.exists()){
                (new FileNotFoundException(" Don't create any start file!")).printStackTrace();
            }
        }
        return currentFile;
    }

    public void setCurrentFile(File currentFile) {
        currentAttr = Model.getEditorModel().getFileAttr(currentFile);
        notifyAllOfUpdateCurrentFile (currentFile, this.currentFile);
        WorkstationConfig.setProperty(ConfProp.FILE_PATH_CURRENT_FILE, currentFile.getPath());
        this.currentFile = currentFile;
    }

    public String getPathHexFiles() {
        return pathHexFiles;
    }


    public void setPathHexFiles(String pathHexFiles) {

        this.pathHexFiles = pathHexFiles;
    }

    public String getPathMifFiles() {

        return pathMifFiles;
    }

    public void setPathMifFiles(String pathMifFiles) {

        this.pathMifFiles = pathMifFiles;
    }

    public void updateOutputCodes(){
        dataEditorListener.updateOutputCodes();
    }

    public OutputCodes getOutputTextCodes(File newFile) {
        return outputCodes.get(newFile);
    }

    public ModeAttrFile getCurrentAttr() {
        return currentAttr;
    }

    public void updateColors(){
        dataEditorListener.updateOutputCodes();
    };


    public void initModeConfigAttrs(){
        File file = getCurrentFile();
        String path = file.getParent() + File.separator;
        fileAttrs.clear();
        try{
            FileInputStream in = new FileInputStream(path  + MODE_FILE_NAME );
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"), BUFFER_SIZE);
            String line;
            br.readLine();
            while((line = br.readLine())!= null) {
                String[] data = line.split("\t");
                ModeAttrFile attr = new ModeAttrFile();
                int i = 0;
                for(String word : data){
                    if(word != null & !word.equals("")){
                        switch(i){
                            case 0:
                                attr.setName(word);
                                break;
                            case 1:
                                FileType type = FileType.valueOf(word);
                                if(type != null) {
                                    attr.setFileType(type);
                                }else{
                                    System.out.println("FileType attr for file:" + attr.getName() + "not recognized");
                                }
                                break;
                            case 2:
                                Integer size = Integer.parseInt(word);
                                attr.setSize(size);
                                break;
                            case 3:
                                Integer numBits = Integer.parseInt(word);
                                attr.setNumBits(numBits);
                                break;
                            case 4:
                                BlockCode blockCode = BlockCode.getName(word);
                                if(blockCode != null){
                                    attr.setBlockCode(blockCode);
                                }else {
                                    System.out.println("Block Code attr for file:" + attr.getName() + "not recognized");
                                }
                                break;
                            case 5:
                                Boolean program = Boolean.parseBoolean(word);
                                if(program != null){
                                    attr.setProgram(program);
                                }else {
                                    System.out.println("Program attr for file:" + attr.getName() + " not recognized [true, false]");
                                }
                                break;
                            case 6:
                                Integer address = Integer.parseInt(word);
                                attr.setAddress(address);
                                break;
                            case 7:
                                Integer hexSize = Integer.parseInt(word);
                                attr.setHexSize(hexSize);
                                break;
                        }
                        i++;
                    }else{
                        System.out.println("Not define attr for file:" + attr.getName());
                    }

                }
                fileAttrs.put(new File(path + attr.getName()), attr);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public ModeAttrFile getFileAttr(File file ){
        ModeAttrFile attr = fileAttrs.get(file);
        if(attr != null) {
            return fileAttrs.get(file);
        }else {
            System.out.println("Not found attributes for file: "+file.getName() +" in mode.config ");
            return null;
        }
    }

    public ModeAttrFile getCurrentFileAttr(){
        return getFileAttr(currentFile);
    }




}
