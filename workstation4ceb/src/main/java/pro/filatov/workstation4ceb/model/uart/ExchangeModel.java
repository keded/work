package pro.filatov.workstation4ceb.model.uart;

import pro.filatov.workstation4ceb.form.terminal.IModeFace;
import pro.filatov.workstation4ceb.form.terminal.CurrentLetter;
import pro.filatov.workstation4ceb.form.terminal.LetterValues;
import pro.filatov.workstation4ceb.model.Model;
import pro.filatov.workstation4ceb.model.fpga.Terminal.BoxExchangeMode;
import pro.filatov.workstation4ceb.model.fpga.Terminal.CebExchangeMode;
import pro.filatov.workstation4ceb.model.fpga.Terminal.ImitatorModel;
import pro.filatov.workstation4ceb.model.fpga.Terminal.TerminalModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by yuri.filatov on 02.09.2016.
 */
public class ExchangeModel {





    Map<CebExchangeMode, IModeFace> cebModeEventListeners = new HashMap<>();

    TerminalModel terminalModel;
    ImitatorModel imitatorModel;

    private Boolean busy = false;
    private byte[]response;



    public void justDoIt(){
        if(!busy) {
            if(terminalModel.getCurrentExchangeMode() == CebExchangeMode.EXAMPLE_FACE){
                new ExampleSendDataProcess().start();
            }else {
                new SendDataProcess().start();
            }
        }
    }


    public ExchangeModel() {
        terminalModel = Model.getTerminalModel();
        imitatorModel = Model.getImitatorModel();
    }


    private class ExampleSendDataProcess extends Thread {

        @Override
        public void run() {

            CebExchangeMode cebExchangeMode = terminalModel.getCurrentExchangeMode();

            try {
                while (!cebExchangeMode.equals(CebExchangeMode.NOT_WORK)) {


                    if(terminalModel.getRunCurrentMode()){

                        byte []request = terminalModel.getRequestCurrentMode();
                        UartModel.EchangePacketAction t = Model.getUartModel().doExchangePacket(request);
                        t.join(3000);
                        if (t.isAlive()) {
                            t.interrupt();
                            t.join();
                            System.out.println("Error for sending packet. This packet will be ignored! ");
                            return;
                        }
                        response = Model.getUartModel().getResponse();
                        terminalModel.refreshCurrentFace();
                        if (!terminalModel.getRepeatingCebExchange()) {
                            terminalModel.setRunCurrentMode(false);
                            cebExchangeMode = CebExchangeMode.NOT_WORK;
                        }
                    }
                    if (!terminalModel.getRepeatingCebExchange()) {
                        cebExchangeMode = CebExchangeMode.NOT_WORK;
                    }
                }


                busy = false;
            } catch (InterruptedException e) {
                busy = false;
                e.printStackTrace();
                return;
            }
        }


    }



    private class SendDataProcess extends Thread {

        @Override
        public void run() {

            CebExchangeMode cebExchangeMode = terminalModel.getCurrentExchangeMode();
            BoxExchangeMode boxExchangeMode = imitatorModel.getBoxExchangeMode();

            try {
                while (!boxExchangeMode.equals(BoxExchangeMode.NOT_WORK) | !cebExchangeMode.equals(CebExchangeMode.NOT_WORK)) {

                    if(!boxExchangeMode.equals(BoxExchangeMode.NOT_WORK)) {
                        Integer factor = Integer.parseInt(Model.getImitatorModel().getFactor());
                        for (int i = 1; i <= factor; i++) {
                            byte[] dataToBox = imitatorModel.getDataToBox();
                            byte[] packetToBox = PacketHelper.createBoxPacketToImitator(dataToBox);
                            UartModel.EchangePacketAction t = Model.getUartModel().doExchangePacket(packetToBox);
                            t.join(3000);
                            if (t.isAlive()) {
                                t.interrupt();
                                t.join();
                                System.out.println("Error for sending packet. This packet will be ignored! ");
                                return;
                            }
                            if (!imitatorModel.getRepeatingBoxExchange()) {
                                boxExchangeMode = BoxExchangeMode.NOT_WORK;
                                imitatorModel.setBoxExchangeMode(BoxExchangeMode.NOT_WORK);
                                break;
                            }
                        }
                    }
                    if(terminalModel.getRunCurrentMode()){

                        byte []request = terminalModel.getRequestCurrentMode();
                        byte[] cebPacket = PacketHelper.createCebPacket(request);
                        byte[] packetToBox = PacketHelper.createBoxPacketToCeb(cebPacket);
                        UartModel.EchangePacketAction t = Model.getUartModel().doExchangePacket(packetToBox);
                        t.join(3000);
                        if (t.isAlive()) {
                            t.interrupt();
                            t.join();
                            System.out.println("Error for sending packet. This packet will be ignored! ");
                            return;
                        }
                        response = Model.getUartModel().getResponse();
                        terminalModel.refreshCurrentFace();
                        if (!terminalModel.getRepeatingCebExchange()) {
                            terminalModel.setRunCurrentMode(false);
                            cebExchangeMode = CebExchangeMode.NOT_WORK;
                        }
                    }
                    if (!imitatorModel.getRepeatingBoxExchange()) {
                        boxExchangeMode = BoxExchangeMode.NOT_WORK;
                    }
                    if (!terminalModel.getRepeatingCebExchange()) {
                        cebExchangeMode = CebExchangeMode.NOT_WORK;
                    }
                }


                busy = false;
            } catch (InterruptedException e) {
                busy = false;
                e.printStackTrace();
                return;
            }
        }


    }


    public void addCebModeEventListener(CebExchangeMode mode, IModeFace listener){
        cebModeEventListeners.put(mode, listener);
    }

    public byte[] getResponse() {
        return response;
    }
}
