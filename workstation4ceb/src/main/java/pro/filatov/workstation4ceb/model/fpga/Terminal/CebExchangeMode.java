package pro.filatov.workstation4ceb.model.fpga.Terminal;

import pro.filatov.workstation4ceb.form.terminal.ExampleFace;

/**
 * Created by yuri.filatov on 02.09.2016.
 */
public enum CebExchangeMode {

    NOT_WORK(null),
    ENGINE_MODE("Engine Mode"),
    MAIN_MODE("Main Mode"),
    USTP_MODE("Ustp config"),
    STEP_MODE_1("Step Mode 1"),
    STEP_MODE_2("Step Mode 2"),
    SENSOR_CALIBRATION("Sensor Calibration"),
    REDUCTION("Speed Reduction"),
    PRECISE_REDUCTION("Precise Reduction"),
    EXAMPLE_FACE("Example");

    private String name;

    CebExchangeMode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    /*
    public String getName() {
        switch(this){
            case NOT_WORK: return null;
            case  MAIN_MODE: return "Main Mode";
            case  USTP_MODE: return "Ustp config";
            case  STEP_MODE_1: return "Step Mode 1";
            case  STEP_MODE_2: return "Step Mode 2";
            case  SENSOR_CALIBRATION: return "Sensor Calibration";
        }
    }
    */
}
