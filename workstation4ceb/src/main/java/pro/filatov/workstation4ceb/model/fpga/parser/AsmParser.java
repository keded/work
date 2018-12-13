package pro.filatov.workstation4ceb.model.fpga.parser;

import de.erichseifert.vectorgraphics2d.intermediate.commands.Command;
import pro.filatov.workstation4ceb.config.ConfProp;
import pro.filatov.workstation4ceb.config.WorkstationConfig;
import pro.filatov.workstation4ceb.model.Model;
import pro.filatov.workstation4ceb.model.editor.FileHelper;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yuri.filatov on 21.04.2016.
 */
public class AsmParser extends Thread {


    private Map<String,Integer> arrayMetok = new HashMap<>();
    private File currentFile;
    private ModeAttrFile currentAttr;
    private Map<Integer, Variable> memoryValues = new HashMap<>();
    private List<String> lines = new ArrayList<>();
    private List<String> linesBinaryCode = new ArrayList<>();
    private List<String> hexCodes = new ArrayList<>();



    @Override
    public void run() {
        try {
            Model.getParserModel().getHexData().clear();
            List<File> selectedFiles = Model.getParserModel().getCurrentParsingFiles();
            for(File file : selectedFiles){
                currentFile = file;
                parse();
            }
            Model.getEditorModel().updateOutputCodes();
            Model.getParserModel().setParsing(false);
            Model.getParserModel().setGenerateMifFiles(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void parse(){
        currentAttr = Model.getEditorModel().getFileAttr(currentFile);
        if(currentAttr == null){
            System.out.println("File "+ currentFile.getName() + "not registered in mode.config. This file will be ignored!");
            return;
        }
        InputStream in;
        BufferedReader br;
        lines = new ArrayList<>();
        try {
            in = new FileInputStream(currentFile);
            br = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8192);
            String line;

            while((line = br.readLine())!= null){
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(currentAttr.getFileType().equals(FileType.ASM)) {
            parseAssembly();
        }else  if(currentAttr.getFileType().equals(FileType.HEX)) {
            parseHexFile();
        } else {
            parseTxt();
        }
        String textAssemblyCode = createTextFromLines(currentAttr.getFileType() == FileType.ASM ? lines : hexCodes);
        String textHexCode = createTextFromLines(hexCodes);
        String textBinaryCode = createTextFromLines(linesBinaryCode);

        List<String> hexCodesToParserModel = new ArrayList<>();
        hexCodesToParserModel.addAll(hexCodes);
        Model.getEditorModel().setOutputCodes(currentFile, textAssemblyCode, textHexCode, textBinaryCode);
        Model.getParserModel().getHexData().put(currentAttr, hexCodesToParserModel);
        createOutputFiles();


    }


    private void parseTxt(){
        Map<String, Variable> res = new HashMap<>();
        memoryValues.clear();
        String line = null;
        try {
            for(int i = 0; i < lines.size(); i++) {
                line = lines.get(i);
                Integer index;
                String value = "0", comment = null;
                String[] params = line.split("\t");
                if (params.length >= 1) {
                    if(!params[0].isEmpty()){
                        index = i;
                        if (params.length >= 2 && params[1] != null && !params[1].isEmpty()) {
                            value = params[1];
                        }
                        if (params.length >= 3 && params[2] != null && !params[2].isEmpty()) {
                            comment = params[2];
                        }
                        Variable resVar = new Variable(value,index, comment);
                        memoryValues.put(index, resVar);
                        for(int k=3; k <= params.length-1; k++){
                            if (params[k] != null && !params[k].isEmpty()) {
                                String name = params[k].replaceAll("\\s+", "");
                                res.put(name, resVar);
                            }
                        }
                    } else {
                        System.out.println("Empty index in line [" + line + "]. This line is ignored");
                    }
                } else {
                    System.out.println("Line [" + line + "] don't consist index column. Parsing this line not allowed.");
                }

            }
        }catch (NumberFormatException e){
                System.out.println("Unrecognized index in line [" + line + "]. This line is ignored");
                e.printStackTrace();
        }
        if(!currentFile.getName().contains("ceb_link_control_trade_ram") && ! currentFile.getName().contains("ceb_link_ao_ram")) {
            Model.getParserModel().setVariables(currentAttr.getBlockCode(), res);
        }
        createOutputCodesFromVariables();
    }



    private void createOutputFiles(){

        WorkstationConfig.setProperty(ConfProp.FILE_PATH_HEX_CODES, Model.getEditorModel().getPathHexFiles());
        FileHelper.createOutputFile(ConfProp.FILE_PATH_HEX_CODES, currentFile.getName(), hexCodes, ".hex");

        if(Model.getParserModel().getGenerateMifFiles()) {
            WorkstationConfig.setProperty(ConfProp.FILE_PATH_MIF_CODES, Model.getEditorModel().getPathMifFiles());
            FileHelper.createOutputFile(ConfProp.FILE_PATH_MIF_CODES, currentFile.getName(), hexCodes, ".hex");
            FileHelper.createOutputFile(ConfProp.FILE_PATH_MIF_CODES, currentFile.getName(), getLinesMifCode(), ".mif");
        }
    }


    private List<String> getLinesMifCode(){
        List<String> mifCodeLines = new ArrayList<String>();
        mifCodeLines.add("WIDTH="+currentAttr.getNumBits().toString()+";");

        mifCodeLines.add("DEPTH="+currentAttr.getSize().toString()+";");
        mifCodeLines.add("");
        mifCodeLines.add("");
        mifCodeLines.add("ADDRESS_RADIX=UNS;");
        mifCodeLines.add("DATA_RADIX=HEX;");
        mifCodeLines.add("");
        mifCodeLines.add("CONTENT BEGIN");
        Integer i = 0;
        for(String code : hexCodes) {
            mifCodeLines.add(i.toString() + "\t:\t" + code + ";");
            i++;
        }
        mifCodeLines.add("END;");
        mifCodeLines.add("");

        return mifCodeLines;
    }







    public void createHexCodes(){
        hexCodes.clear();
        int i = 1;
        for (String s : linesBinaryCode){
            Integer command;
            try {
                command = Integer.parseInt(s, 2);
            }catch (NumberFormatException e){
                e.printStackTrace();
                continue;
            }
            String hex;
            hex = Integer.toHexString(command | 0x10000).substring(1);
            Integer hexSize = currentAttr.getNumBits() > 16 ? Math.round(currentAttr.getHexSize()/2) : currentAttr.getHexSize();
            if(i <=  hexSize ) {
                hexCodes.add(hex);
            }else{
                //System.out.println("Line number " + i + " is overflow max count lines: " +currentAttr.getHexSize() + " for block: "+ currentAttr.getBlockCode().name() );
            }
            i++;
        }
    }

    public String createTextFromLines(List<String> list){
        String text = "";
        for (String hex : list){
            text += hex + "\n";
        }
        return text;
    }



    private void parseAssembly(){
        arrayMetok.clear();
        deleteAsmComments();
        getArrayMetok();
        parseCommands();
        Double d =  Math.pow(2, currentAttr.getNumBits());
        int mask = d.intValue();
        if(linesBinaryCode.size() < currentAttr.getSize()){
            int count = currentAttr.getSize() - linesBinaryCode.size()-1;
            for(int i = 0 ; i <= count; i++){
                linesBinaryCode.add(Integer.toBinaryString(mask).substring(1));
            }
        }
        createHexCodes();
    }

    private void parseHexFile(){
        deleteComments();
        Double d =  Math.pow(2, currentAttr.getNumBits());
        BigInteger mask = BigDecimal.valueOf(d).toBigInteger();
        for(int i = 0 ; i < lines.size(); i++){
            String line = lines.get(i).replaceAll("\\s+","");
            BigInteger number =new BigInteger(line, 16).or(mask);
            hexCodes.add(number.toString(16).substring(1));
            linesBinaryCode.add(number.toString(2).substring(1));
        }
        String zero = mask.toString(16).substring(1);
        if(hexCodes.size() < currentAttr.getSize()){
            int count = currentAttr.getSize() - hexCodes.size()-1;
            for(int i = 0 ; i <= count; i++){
                hexCodes.add(zero);
            }
        }

    }


    private void createOutputCodesFromVariables(){
        hexCodes.clear();
        linesBinaryCode.clear();
        Double d =  Math.pow(2, currentAttr.getNumBits());
        BigInteger mask = BigDecimal.valueOf(d).toBigInteger();
        int remainder =  (int)(Math.pow(2, currentAttr.getNumBits() % 4)-1);

        String zero16 = mask.toString(16).substring(1);
        String zero2 = mask.toString(2).substring(1);
        for(int i=0; i < currentAttr.getHexSize(); i++) {
            Variable variable = memoryValues.get(i);
            if (variable != null ) {
                BigInteger number;
                try{
                    number = new BigInteger(variable.getValue(), 16).or(mask);
                }catch (NumberFormatException e){
                    hexCodes.add(zero16);
                    linesBinaryCode.add(zero2);
                    System.out.println("Variable with index:" + String.valueOf(i)+ " has incorrect value!(File: "+ currentFile.getName()+")");
                    continue;
                }
                String hexString = number.toString(16);
                if(remainder == 0) {
                    hexString = hexString.substring(1);
                }else {
                    String high = hexString.substring(0, 1);
                    String newHigh = String.valueOf(Integer.parseInt(high, 16) & remainder);
                    hexString = newHigh + hexString.substring(1,hexString.length());
                }
                hexCodes.add(hexString);
                linesBinaryCode.add(number.toString(2).substring(1));
            }
            else {
                hexCodes.add(zero16);
                    linesBinaryCode.add(zero2);
            }
        }
    }


    private void fixArrayMetok(int i){
            for(Map.Entry<String, Integer> metka : arrayMetok.entrySet()){
            if(metka.getValue() > i ){
                arrayMetok.put(metka.getKey(), metka.getValue() - 1);
            }
        }



    }


    private void parseCommands(){
        linesBinaryCode.clear();
        Pattern patternAnyWord = Pattern.compile("[\\w|$|-]+");
        for(int i = 0 ; i < lines.size(); i++){
            String line = lines.get(i);
            Matcher matcher = patternAnyWord.matcher(line);
            if(matcher.find()){
                String commandStr = matcher.group();
                BupCommand bupCommand= null;
                TradeCommand tradeCommand = null;

                try {
                    if (currentAttr.getBlockCode().equals(BlockCode.TRADE))
                        tradeCommand = TradeCommand.getCommand(commandStr);
                    else {
                        bupCommand = BupCommand.getCommand(commandStr);
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                if(bupCommand!=null | tradeCommand !=null){
                    String binaryCode;
                    if(currentAttr.getBlockCode().equals(BlockCode.TRADE)) {
                        binaryCode = parseArguments(line, matcher, tradeCommand, i);
                    }else{
                        binaryCode = parseArguments(line, matcher, bupCommand, i);
                    }
                    if(binaryCode!=null) {
                        linesBinaryCode.add(binaryCode);
                    }else{
                        System.out.println("Line assembly["+line+"] is rejected!");
                        fixArrayMetok(i);
                        lines.remove(i);
                        i--;
                    }
                }else{
                    System.out.println("Invalid command["+ commandStr+"] in line:["+line+"]");
                    System.out.println("Line assembly["+line+"] is rejected!");
                    fixArrayMetok(i);
                    lines.remove(i);
                    i--;
                }
            }else{
                System.out.println("Error in line:[" + line + "]");
                System.out.println("Line assembly["+line+"] is rejected!");
                fixArrayMetok(i);
                lines.remove(i);
                i--;
            }
        }
    }

    private String parseArguments(String line, Matcher matcher, final BupCommand command, Integer i){
        StringBuilder binaryCommand = new StringBuilder();

        if(command.equals(BupCommand.MORE0)){
            if(currentAttr.getName().equals("ceb_kpu_commands.asm")){
                binaryCommand.append("00001");
            }else {
                binaryCommand.append("0011");
            }
        }else if(command.equals(BupCommand.JUMP)) {
            if(currentAttr.getName().equals("ceb_kpu_commands.asm")){
                binaryCommand.append("0011");
            }else {
                binaryCommand.append("00001");
            }
        }else {
            binaryCommand.append(command.getOpCode());
        }

        switch (command) {
            case STP_DEBUG:
                if(matcher.find()) {
                    System.out.println("For line["+ line +"]Any argument is ignored!");
                }
                break;
            case SETB:
            case CLRB:
                if(matcher.find()) {
                    String rel = argToBinary(matcher.group(), 0b10000,  line);
                    if(rel != null){
                        binaryCommand.append(rel);
                    }else{
                        return null;
                    }
                }else {
                    return null;
                }
                if(matcher.find()) {
                    String binArg1 =  getBinaryRON( matcher.group(),line);
                    if(binArg1!=null) {
                        binaryCommand.append(binArg1);
                    }else {
                        return null;
                    }
                }else {
                    return null;
                }
                break;
            case SIN:
            case COS:
            case CFIX:
            case INC:
            case CLR:
            case DEC:
            case ABS:
                if(matcher.find()) {
                    String binArg1 =  getBinaryRON( matcher.group(),line);
                    if(binArg1!=null) {
                        binaryCommand.append(binArg1);
                    }else {
                        return null;
                    }
                }else {
                    return null;
                }
                break;
            case ADD:
            case SUB:
            case MOVE:
            case MULT:
            case ORL:
            case ANL:
            case CROSS0:
            case LIM:
            case ENC:
            case ATAN:
                if(matcher.find()) {
                    String binArg1 =  getBinaryRON( matcher.group(),line);
                    if(binArg1!=null) {
                        binaryCommand.append(binArg1);
                    }else {
                        return null;
                    }
                }else {
                    return null;
                }
                if(matcher.find()) {
                    String binArg2 =  getBinaryRON( matcher.group(), line);
                    if(binArg2!=null) {
                        binaryCommand.append(binArg2);
                    }else{
                        return null;
                    }
                }else{
                    return null;
                }
                break;
            case STORE:
            case CSTORE:
            case LOAD:
            case CLOAD:
                if(matcher.find()) {
                    String arg =matcher.group();
                    Variable perem = Model.getParserModel().getVariable(currentAttr.getBlockCode(), arg);
                    if (perem != null) {
                        arg = perem.getIndex().toString();
                    }
                    /*else{
                        try {
                            if(arg.contains("h")) {
                                Integer.parseInt(arg, 16);
                            }else {
                                Integer.parseInt(arg, 10);
                            }
                        }catch (NumberFormatException e) {
                            System.out.println("Variable in line[" + line +"] not recognized!" );
                        }
                    }
                    */
                    String binaryAddressRAM = argToBinary(arg, 0b100000000,  line);
                    if(binaryAddressRAM != null){
                        binaryCommand.append(binaryAddressRAM );
                    }else{
                        return null;
                    }
                }else {
                    return null;
                }
                if(matcher.find()) {
                    String binArg =    getBinaryRON( matcher.group(), line);
                    if(binArg!=null) {
                        binaryCommand.append(binArg);
                    }else {
                        return null;
                    }
                }else {
                    return null;
                }
                break;
            case JUMP:
                if(matcher.find()){
                    String metka = matcher.group();
                    String metka2 = metka;
                    if(metka.contains("$")){
                        metka2 = metka.replace("$","");
                    }
                    Integer numberROW = arrayMetok.get(metka2);
                    if(numberROW != null){

                        String binaryAddressROM;
                        if(currentAttr.getName().equals("ceb_kpu_commands.asm")){//command.equals(BupCommand.MORE0
                            binaryAddressROM  = argToBinary(numberROW.toString(), 0b10000000000,  line);
                        }else {
                            binaryAddressROM  = argToBinary(numberROW.toString(), 0b1000000000,  line);
                        }
                        if(binaryAddressROM != null){
                           lines.set(i, lines.get(i).replace(metka, numberROW.toString()));
                           binaryCommand.append(binaryAddressROM);
                        }else{
                            return null;
                        }
                    }else{
                        System.out.println("Error! Invalid metka in line:["+line+"]");
                        return null;

                    }
                }else{
                    return null;
                }
                break;
            /*case CONST:
                if(matcher.find()){
                    String binaryREL  = argToBinary(matcher.group(), 0b100,  line);
                    if(binaryREL != null){
                        binaryCommand.append(binaryREL);
                    }else{
                        return null;
                    }
                }else{
                    return null;
                }
                if(matcher.find()){
                    String binaryConst  = argToBinary(matcher.group(), 0b100000000,  line);
                    if(binaryConst != null){
                        binaryCommand.append(binaryConst);
                    }else{
                        return null;
                    }
                }else{
                    return null;
                }
                if(matcher.find()){
                    String binaryRON  = getBinaryRON(matcher.group(),  line);
                    if(binaryRON != null){
                        binaryCommand.append(binaryRON);
                    }else{
                        return null;
                    }
                }else{
                    return null;
                }
                break;
             */
            case NOP:
                if(matcher.find()){
                    String numberNOP  = argToBinary(matcher.group(), 0b1000000,  line);
                    if(numberNOP != null){
                        binaryCommand.append(numberNOP);
                    }else{
                        return null;
                    }
                }else{
                    binaryCommand.append("000000");
                }
                break;
			case RSR:	
            case ASR:
            case LSL:
                if(matcher.find()) {
                    String rel = argToBinary(matcher.group(), 0b100000,  line);
                    if(rel != null){
                        binaryCommand.append(rel );
                    }else{
                        return null;
                    }
                }else {
                    return null;
                }
                if(matcher.find()) {
                    String binaryRON  = getBinaryRON(matcher.group(),  line);
                    if(binaryRON != null){
                        binaryCommand.append(binaryRON);
                    }else{
                        return null;
                    }
                }else {
                    return null;
                }
                break;
            case MORE0:
            case EQUAL0:
            case DJNZ:
                if(matcher.find()) {
                    String metka = matcher.group();
                    String metka2 = metka;
                    if(metka.contains("$")){
                        metka2 = metka.replace("$","");
                    }
                    Integer numberROW = arrayMetok.get(metka2);
                    if(numberROW != null) {
                        Integer  relInt;
                        if(i > numberROW){
                            relInt = numberROW - i;
                        }else{
                            relInt = numberROW - i ;
                        }
                        metka2 = String.valueOf(relInt);
                    }
                    String rel = null;

                        if (command.equals(BupCommand.MORE0) & currentAttr.getName().equals("ceb_kpu_commands.asm")) {//command.equals(BupCommand.MORE0
                            rel = getREL(metka2, 0b1000000, line, 5);
                        } else {
                            rel = getREL(metka2, 0b10000000, line, 6);
                        }

                    if(rel != null){
                        binaryCommand.append(rel );
                    }else{
                        return null;
                    }
                }else {
                    return null;
                }
                if(matcher.find()) {
                    String binaryRON  = getBinaryRON(matcher.group(),  line);
                    if(binaryRON != null){
                        binaryCommand.append(binaryRON);
                    }else{
                        return null;
                    }
                }else {
                    return null;
                }
                break;
            case MORE:
            case EQUAL:
                if(matcher.find()) {
                    String metka = matcher.group();
                    String metka2 = metka;
                    if(metka.contains("$")){
                        metka2 = metka.replace("$","");
                    }
                    Integer numberROW = arrayMetok.get(metka2);
                    if(numberROW != null) {
                        Integer  relInt;
                        if(i > numberROW){
                            relInt = numberROW - i;
                        }else{
                            relInt = numberROW - i;
                        }
                        metka2 = String.valueOf(relInt);
                    }
                    String rel =  getRelModulus(metka2, 0b10000,  line, 3);
                    if(rel != null){
                        binaryCommand.append(rel );
                    }else{
                        return null;
                    }
                }else {
                    return null;
                }
                if(matcher.find()) {
                    String binaryRON  = getBinaryRON(matcher.group(),  line);
                    if(binaryRON != null){
                        binaryCommand.append(binaryRON);
                    }else{
                        return null;
                    }
                }else {
                    return null;
                }
                if(matcher.find()) {
                    String binaryRON  = getBinaryRON(matcher.group(),  line);
                    if(binaryRON != null){
                        binaryCommand.append(binaryRON);
                    }else{
                        return null;
                    }
                }else {
                    return null;
                }
                break;
            case ISBIT:
                if(matcher.find()) {
                    String metka = matcher.group();
                    String metka2 = metka;
                    if(metka.contains("$")){
                        metka2 = metka.replace("$","");
                    }
                    Integer numberROW = arrayMetok.get(metka2);
                    if(numberROW != null) {
                        Integer  relInt;
                        if(i > numberROW){
                            relInt = numberROW - i;
                        }else{
                            relInt = numberROW - i;
                        }
                        metka2 = String.valueOf(relInt);
                    }

                    String rel =  getRelModulus(metka2, 0b10000,  line, 3);
                    if(rel != null){
                        binaryCommand.append(rel );
                    }else{
                        return null;
                    }
                }else {
                    return null;
                }
                if(matcher.find()) {
                    String rel = argToBinary(matcher.group(), 0b1000,  line);
                    if(rel != null){
                        binaryCommand.append(rel );
                    }else{
                        return null;
                    }
                }else {
                    return null;
                }
                if(matcher.find()) {
                    String binaryRON  = getBinaryRON(matcher.group(),  line);
                    if(binaryRON != null){
                        binaryCommand.append(binaryRON);
                    }else{
                        return null;
                    }
                }else {
                    return null;
                }
                break;
        }

        return binaryCommand.toString();
    }




    private String parseArguments(String line, Matcher matcher, final TradeCommand command, Integer i){
        StringBuilder binaryCommand = new StringBuilder();
        binaryCommand.append(command.getOpCode());
        switch (command) {
            case STP_DEBUG:
                if(matcher.find()) {
                    System.out.println("For line["+ line +"]Any argument is ignored!");
                }
                break;
            case SETB:
            case CLRB:
                if(matcher.find()) {
                    String rel = argToBinary(matcher.group(), 0b10000,  line);
                    if(rel != null){
                        binaryCommand.append(rel);
                    }else{
                        return null;
                    }
                }else {
                    return null;
                }
                if(matcher.find()) {
                    String binArg1 =  getBinaryRON( matcher.group(),line);
                    if(binArg1!=null) {
                        binaryCommand.append(binArg1);
                    }else {
                        return null;
                    }
                }else {
                    return null;
                }
                break;
            case ABS:
            case CFIX:
            case RJUMP:
            case STOREPC:
            case INC:
            case CLR:
            case DEC:
                if(matcher.find()) {
                    String binArg1 =  getBinaryRON( matcher.group(),line);
                    if(binArg1!=null) {
                        binaryCommand.append(binArg1);
                    }else {
                        return null;
                    }
                }else {
                    return null;
                }
                break;
            case ADD:
            case SUB:
            case MULT:
            case MOVE:
            case ANL:
            case ORL:
                if(matcher.find()) {
                    String binArg1 =  getBinaryRON( matcher.group(),line);
                    if(binArg1!=null) {
                        binaryCommand.append(binArg1);
                    }else {
                        return null;
                    }
                }else {
                    return null;
                }
                if(matcher.find()) {
                    String binArg2 =  getBinaryRON( matcher.group(), line);
                    if(binArg2!=null) {
                        binaryCommand.append(binArg2);
                    }else{
                        return null;
                    }
                }else{
                    return null;
                }
                break;

            case RSTORE:
                String reg1=null, reg2 = null, reg3 = null;
                if(matcher.find()) {
                    reg3 = getBinaryRON( matcher.group(), line);
                }else {
                    return null;
                }
                if(matcher.find()) {
                    reg2 = getBinaryRON( matcher.group(), line);
                }else {
                    return null;
                }
                if(matcher.find()) {
                    reg1 = getBinaryRON( matcher.group(), line);
                }
                if(reg1 != null){
                    binaryCommand.append(reg3);
                    binaryCommand.append(reg2);
                    binaryCommand.append(reg1);
                }else{
                    binaryCommand.append(reg3);
                    binaryCommand.append("000");
                    binaryCommand.append(reg2);
                }
                break;
            case STORE:
            case LOAD:
            case CSTORE:
            case CLOAD:
                BlockCode block;
                if(matcher.find()) {
                    block  = BlockCode.getName(matcher.group());
                    if(block  != null){
                        binaryCommand.append(block.getOpCode());
                    }else{
                        return null;
                    }
                }else {
                    return null;
                }
                if(matcher.find()) {
                    String arg =matcher.group();
                    Integer addr;
                    try {
                        Integer.parseInt(arg, 16);
                    }catch (NumberFormatException   e){
                        Variable perem = Model.getParserModel().getVariable(block, arg);

                        if (perem != null) {
                            addr  =  perem.getIndex() ;
                            if(block.equals(BlockCode.BUP) && (command.equals(TradeCommand.CLOAD) ||  command.equals(TradeCommand.LOAD))){
                                  addr = addr + 32;
                            }
                            arg = addr.toString();
                        }else{
                            System.out.println("Variable in line[" + line +"] not recognized!" );
                        }
                    }



                    String binaryAddressRAM = argToBinary(arg, block.getMask(),  line);
                    if(binaryAddressRAM != null){
                        binaryCommand.append(binaryAddressRAM );
                    }else{
                        return null;
                    }
                }else {
                    return null;
                }
                if(matcher.find()) {
                    String binArg =    getBinaryRON( matcher.group(), line);
                    if(binArg!=null) {
                        binaryCommand.append(binArg);
                    }else {
                        return null;
                    }
                }else {
                    return null;
                }
                if(matcher.find()) {
                    String binArg = getBinaryRON(matcher.group(), line);
                    if (binArg != null) {
                        binaryCommand.append(binArg);
                    } else {
                        return null;
                    }
                }
                break;
            case JUMP:
                if(matcher.find()){
                    String metka = matcher.group();
                    String metka2 = metka;
                    if(metka.contains("$")){
                        metka2 = metka.replace("$","");
                    }
                    Integer numberROW = arrayMetok.get(metka2);
                    if(numberROW != null){
                        String binaryAddressROM  = argToBinary(numberROW.toString(), 0b10000000000,  line);
                        if(binaryAddressROM != null){
                            lines.set(i, lines.get(i).replace(metka, numberROW.toString()));
                            binaryCommand.append(binaryAddressROM);
                        }else{
                            return null;
                        }
                    }else{
                        System.out.println("Error! Invalid metka in line:["+line+"]");
                        return null;

                    }
                }else{
                    return null;
                }
                break;
            case CONST:
                if(matcher.find()){
                    String binaryREL  = argToBinary(matcher.group(), 0b10,  line);
                    if(binaryREL != null){
                        binaryCommand.append(binaryREL);
                    }else{
                        return null;
                    }
                }else{
                    return null;
                }
                if(matcher.find()){
                    String binaryConst  = argToBinary(matcher.group(), 0b100000000,  line);
                    if(binaryConst != null){
                        binaryCommand.append(binaryConst);
                    }else{
                        return null;
                    }
                }else{
                    return null;
                }
                if(matcher.find()){
                    String binaryRON  = getBinaryRON(matcher.group(),  line);
                    if(binaryRON != null){
                        binaryCommand.append(binaryRON);
                    }else{
                        return null;
                    }
                }else{
                    return null;
                }
                break;

            case NOP:
                if(matcher.find()){
                    String numberNOP  = argToBinary(matcher.group(), 0b1000000,  line);
                    if(numberNOP != null){
                        binaryCommand.append(numberNOP);
                    }else{
                        return null;
                    }
                }else{
                    binaryCommand.append("000000");
                }
                break;
            case RSR:
            case ASR:
            case LSL:
                if(matcher.find()) {
                    String rel = argToBinary(matcher.group(), 0b10000,  line);
                    if(rel != null){
                        binaryCommand.append(rel );
                    }else{
                        return null;
                    }
                }else {
                    return null;
                }
                if(matcher.find()) {
                    String binaryRON  = getBinaryRON(matcher.group(),  line);
                    if(binaryRON != null){
                        binaryCommand.append(binaryRON);
                    }else{
                        return null;
                    }
                }else {
                    return null;
                }
                break;
            case MORE0:
            case LESS0:
            case EQUAL0:
            case DJNZ:
                if(matcher.find()) {
                    String metka = matcher.group();
                    String metka2 = metka;
                    if(metka.contains("$")){
                        metka2 = metka.replace("$","");
                    }
                    Integer numberROW = arrayMetok.get(metka2);
                    if(numberROW != null) {
                        int relInt;
                        if(i > numberROW){
                            relInt = numberROW - i;
                        }else{
                            relInt = numberROW - i;
                        }
                        metka2 = String.valueOf(relInt);
                    }
                    String rel =  getREL(metka2, 0b100000000, line,7);
                    if(rel != null){
                        binaryCommand.append(rel );
                    }else{
                        return null;
                    }
                }else {
                    return null;
                }
                if(matcher.find()) {
                    String binaryRON  = getBinaryRON(matcher.group(),  line);
                    if(binaryRON != null){
                        binaryCommand.append(binaryRON);
                    }else{
                        return null;
                    }
                }else {
                    return null;
                }
                break;
            case MORE:
            case EQUAL:
                if(matcher.find()) {
                    String metka = matcher.group();
                    String metka2 = metka;
                    if(metka.contains("$")){
                        metka2 = metka.replace("$","");
                    }
                    Integer numberROW = arrayMetok.get(metka2);
                    if(numberROW != null) {
                        Integer  relInt;
                        if(i > numberROW){
                            relInt = numberROW - i;
                        }else{
                            relInt = numberROW - i;
                        }
                        metka2 = String.valueOf(relInt);
                    }
                    String rel =  getRelModulus(metka2, 0b1000000,  line, 5);
                    if(rel != null){
                        binaryCommand.append(rel );
                    }else{
                        return null;
                    }
                }else {
                    return null;
                }
                if(matcher.find()) {
                    String binaryRON  = getBinaryRON(matcher.group(),  line);
                    if(binaryRON != null){
                        binaryCommand.append(binaryRON);
                    }else{
                        return null;
                    }
                }else {
                    return null;
                }
                if(matcher.find()) {
                    String binaryRON  = getBinaryRON(matcher.group(),  line);
                    if(binaryRON != null){
                        binaryCommand.append(binaryRON);
                    }else{
                        return null;
                    }
                }else {
                    return null;
                }
                break;
            case ISBIT:
                if(matcher.find()) {
                    String metka = matcher.group();
                    String metka2 = metka;
                    if(metka.contains("$")){
                        metka2 = metka.replace("$","");
                    }
                    Integer numberROW = arrayMetok.get(metka2);
                    if(numberROW != null) {
                        Integer  relInt;
                        if(i > numberROW){
                            relInt = numberROW - i;
                        }else{
                            relInt = numberROW - i;
                        }
                        metka2 = String.valueOf(relInt);
                    }

                    String rel =    getRelModulus(metka2, 0b1000000,  line, 6);
                    if(rel != null){
                        binaryCommand.append(rel );
                    }else{
                        return null;
                    }
                }else {
                    return null;
                }
                if(matcher.find()) {
                    String rel = argToBinary(matcher.group(), 0b1000,  line);
                    if(rel != null){
                        binaryCommand.append(rel );
                    }else{
                        return null;
                    }
                }else {
                    return null;
                }
                if(matcher.find()) {
                    String binaryRON  = getBinaryRON(matcher.group(),  line);
                    if(binaryRON != null){
                        binaryCommand.append(binaryRON);
                    }else{
                        return null;
                    }
                }else {
                    return null;
                }
                break;
        }

        return binaryCommand.toString();
    }


    private String getRelModulus(String arg, int mask, String line, Integer module){
        try {
            Double  rang = Math.pow(2, module);
            int ran = rang.intValue();
            Integer mModule = 0;
            Integer pModule =  ran -1;
            Integer  argInt = Integer.parseInt( arg, 10);
            if(argInt < -ran  | argInt > ran -1){
                System.out.println("Error! Argument ["+arg+"] out of range ["+mModule.toString()+";"+pModule.toString()+"] in line:["+line+"]");
                return null;
            }
            if(argInt >= 0) {
                return Integer.toBinaryString(mask | argInt).substring(1);
            }else {

                String s = Integer.toBinaryString(argInt);
                return   s.substring(32-module-1, 32);
            }
        }     catch (NumberFormatException ex){
            System.out.println("Error! Invalid argument in line:["+line+"]");
            return null;
        }



    }


    private String getREL(String arg, int mask, String line, Integer module){
        try {
            //Double a2 = new Double(module.toString());
            //Double mod = new Double(module) ;
            Double  rang = Math.pow(2, module);
            int ran = rang.intValue();
            Integer mModule = -ran;
            Integer pModule =  ran -1;
            Integer  argInt = Integer.parseInt( arg, 10);
            if(argInt < -ran  | argInt > ran -1){
                System.out.println("Error! Argument ["+arg+"] out of range ["+mModule.toString()+";"+pModule.toString()+"] in line:["+line+"]");
                return null;
            }
            if(argInt >= 0) {
                return Integer.toBinaryString(mask | argInt).substring(1);
            }else {

                String s = Integer.toBinaryString(argInt);
                return   s.substring(32-module-1, 32);
            }
        }     catch (NumberFormatException ex){
            System.out.println("Error! Invalid argument in line:["+line+"]");
            return null;
        }



    }


    public String argToBinary(String arg, int mask,  String line){
        String argUC = arg.toUpperCase();
        if( argUC.contains("H")){
            argUC = argUC.replace("H","");
            return argToBinary(argUC, mask, line, 16);
        }else if(argUC.contains("X")) {
            argUC = argUC.replace("X","");
            return argToBinary(argUC, mask, line, 8);
        }else if(argUC.contains("B")) {
            argUC = argUC.replace("B","");
            return argToBinary(argUC, mask, line, 2);
        }else {
            return argToBinary(argUC, mask, line, 10);
        }
    }



    private String argToBinary(String arg, int mask,  String line, Integer sys){
        try {
            Integer  argInt = Integer.parseInt( arg, sys);
          //  if(argInt >= mask){
         //       System.out.println("Warning! Value const  in line:["+line+"] is more then "+String.valueOf(mask));
         //       argInt = mask -1;
          //  }
            return Integer.toBinaryString(mask | argInt ).substring(1);
        }     catch (NumberFormatException ex){
            System.out.println("Error! Invalid argument in line:["+line+"]");
            return null;
        }

    }



    private String getBinaryRON(String arg, String line ){
        try {
            if (arg.length() == 2 & arg.contains("R")) {
                Integer numRON = Integer.parseInt(arg.replaceAll("R", ""));
                if(numRON >= 0  & numRON < 8) {
                    return Integer.toBinaryString(0b1000 | numRON).substring(1);
                }
            }
            System.out.println("Error! Invalid argument in line:["+line+"]");
            return null;

        }
        catch (NumberFormatException ex){
            System.out.println("Error! Invalid number in line:["+line+"]");
            return null;
        }
    }

    private void deleteComments(){
        Pattern patternAnyWord = Pattern.compile("[\\w|$|/*]+");

        Pattern patternComment = Pattern.compile("//.*");
        Pattern patternLineComment = Pattern.compile("^[\\s]*//.*");
        for(int i = 0; i< lines.size();i++){
            String line = lines.get(i);
            Matcher matcher = patternComment.matcher(line);
            Matcher matcher2 = patternAnyWord.matcher(line);
            if(matcher.find()) {
                if(patternLineComment.matcher(line).find()){
                    lines.remove(i);
                    i--;
                }else{
                    String comment = matcher.group(0);
                    lines.set(i, lines.get(i).replace(comment, "").replaceAll("\t",""));
                }
            }else if(!matcher2.find()){
                lines.remove(i);
                i--;
            }
        }


    }


    private void deleteAsmComments(){

        deleteComments();

        String text = "";
       // Pattern patternAnyWord = Pattern.compile("[\\w|$]+");

        for(String line : lines){
       //     if(patternAnyWord .matcher(line).find()){
                text = text + line + "\n";

        }

        String REGEX = "(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)";
        Pattern patternComment= Pattern.compile(REGEX);
        Matcher matcher = patternComment.matcher(text);


        while(matcher.find()){
            String comment = matcher.group(0);
            text = text.replace(comment, "");
        }
        String[] clearLinesArray = text.split("\n");
        List<String> clearlinesList = new ArrayList<String>(Arrays.asList(clearLinesArray));
        lines.clear();
        for(String line : clearlinesList){
            if(!line.equals("")){
                lines.add(line);
            }
        }


    }



    private void getArrayMetok(){
        Pattern patternMetka = Pattern.compile("^[\\w]+:");
        for(int i = 0; i< lines.size();i++){
            Matcher matcher = patternMetka.matcher(lines.get(i));
            if(matcher.find()) {
                String metka = matcher.group(0);
                String metka2 = metka.replace(":","");
                if (arrayMetok.get(metka2) != null) {
                    System.out.println("Warning! Metka [" + metka + "] in line:" + i + " already exist in line:" + arrayMetok.get(metka) + ". Metka in line:" + i + " is ignored!");
                } else {

                    arrayMetok.put(metka2, i);
                }
                lines.set(i, lines.get(i).replace(metka, ""));
            }
        }
    }




}
