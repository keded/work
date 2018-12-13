package pro.filatov.workstation4ceb.model.uart.rom;

/**
 * Created by yuri.filatov on 22.08.2016.
 */
public enum CurrentBlock {


    TRADE(0x0000),
    BUP(0x0100),
    BASE(0x0200),
    KPU(0x0300),
    ANY(0x0000);

    private Integer mask;

    CurrentBlock(Integer mask) {
        this.mask = mask;
    }

    public Integer getMask() {
        return mask;
    }
}
