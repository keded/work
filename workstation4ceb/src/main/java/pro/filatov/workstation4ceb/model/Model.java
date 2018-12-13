package pro.filatov.workstation4ceb.model;

import pro.filatov.workstation4ceb.form.terminal.graph.PointData;
import pro.filatov.workstation4ceb.model.editor.EditorModel;
import pro.filatov.workstation4ceb.model.fpga.Terminal.ImitatorModel;
import pro.filatov.workstation4ceb.model.editor.TreeViewModel;
import pro.filatov.workstation4ceb.model.fpga.Terminal.TerminalModel;
import pro.filatov.workstation4ceb.model.fpga.parser.ParserModel;
import pro.filatov.workstation4ceb.model.uart.ExchangeModel;
import pro.filatov.workstation4ceb.model.uart.MemoryModel;
import pro.filatov.workstation4ceb.model.uart.UartModel;

/**
 * Created by yuri.filatov on 01.08.2016.
 */
public class Model {



 //   private static EditorModel editorModel = new EditorModel();
    private static UartModel uartModel;
  //  private static TreeViewModel treeViewModel = new TreeViewModel();
 //   private static ParserModel parserModel = new ParserModel();
   // public static MemoryModel memoryModel = new MemoryModel();
   // public static ImitatorModel imitatorModel = new ImitatorModel();
    public static TerminalModel terminalModel = new TerminalModel();
    public static ExchangeModel exchangeModel = new ExchangeModel();
    public static PointData pointData = new PointData(400);
    public static boolean flagQueue = false;



    public static void init(){
       // editorModel.addAppFrameEventListener(treeViewModel);
    }

    public static UartModel getUartModel() {
        if(uartModel == null) {
            uartModel = new UartModel();
        }
        return uartModel;
    }



    public static TreeViewModel getTreeViewModel() {
        return null;
        //return treeViewModel;
    }

    public static ParserModel getParserModel() {     return null;
        //return parserModel;
    }



    public static EditorModel getEditorModel() {
        return null;
        //return editorModel;
    }

    public static MemoryModel getMemoryModel() {
           return null;
        //return memoryModel;
    }

    public static ImitatorModel getImitatorModel() {
        return null;
        //return imitatorModel;
    }

    public static ExchangeModel getExchangeModel() {
        return exchangeModel;
    }

    public static TerminalModel getTerminalModel() {
        return terminalModel;
    }
}
