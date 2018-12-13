package pro.filatov.workstation4ceb.model.fpga.parser;

/**
 * Created by Администратор on 21.04.2016.
 */
public enum BupCommand {

    NOP     ("00000000", CommandGroup.OTHER),
    ADD     ("00000001", CommandGroup.TRANSFORM),
    SUB     ("00000010", CommandGroup.TRANSFORM),
    MULT    ("00000011", CommandGroup.TRANSFORM),
    LIM     ("00000100", CommandGroup.TRANSFORM),
    CROSS0  ("00000100", CommandGroup.TRANSFORM),
    ATAN    ("00000101", CommandGroup.TRANSFORM),
    MOVE    ("00000110", CommandGroup.TRANSFORM),
    CFIX    ("00000111000", CommandGroup.OTHER),
    SIN     ("00000111001", CommandGroup.TRANSFORM),
    COS     ("00000111010", CommandGroup.TRANSFORM),
    INC     ("00000111011", CommandGroup.TRANSFORM),
    DEC     ("00000111100", CommandGroup.TRANSFORM),
    ABS     ("00000111110", CommandGroup.TRANSFORM),
    CLR     ("00000111111", CommandGroup.TRANSFORM),
    STP_DEBUG     ("00000111101000",CommandGroup.OTHER),
    JUMP    ("0011", CommandGroup.BRANCH),
    LSL     ("000100", CommandGroup.TRANSFORM),
    RSR     ("000101", CommandGroup.TRANSFORM),
    ASR     ("000101", CommandGroup.TRANSFORM),
    ENC     ("00011000", CommandGroup.TRANSFORM),
    ORL     ("00011001", CommandGroup.TRANSFORM),
    ANL     ("00011010", CommandGroup.TRANSFORM),
    SETB    ("0001110", CommandGroup.TRANSFORM),
    CLRB    ("0001111", CommandGroup.TRANSFORM),
    EQUAL0  ("0010", CommandGroup.BRANCH),
    MORE0   ("00001", CommandGroup.BRANCH),
    DJNZ   ("0100", CommandGroup.BRANCH),
    EQUAL   ("0101", CommandGroup.BRANCH),
    MORE    ("0110", CommandGroup.BRANCH),
    ISBIT   ("0111", CommandGroup.BRANCH),
    STORE   ("100", CommandGroup.MEMORY),
    CSTORE  ("101", CommandGroup.MEMORY),
    LOAD    ("110", CommandGroup.MEMORY),
    CLOAD   ("111", CommandGroup.MEMORY)
    ;

    private String opCode;
    private CommandGroup group;

    BupCommand(String opCode, CommandGroup group){
        this.opCode = opCode;
        this.group = group;
    }

    public String getOpCode() {
        return opCode;
    }

    public void setOpCode(String opCode) {
        this.opCode = opCode;
    }

    public static BupCommand getCommand(String command){
        for(BupCommand item  : BupCommand.values()){
            if(item.toString().equals(command)){
                return item;
            }
        }
        return null;
    }

    public CommandGroup getGroup() {
        return group;
    }
}
