package pro.filatov.workstation4ceb.model.fpga.Terminal;

/**
 * Created by y.k.filatov on 18.11.2016.
 */
public enum ChannelForPhases {

    A(0, "A", "ALPHA", 0b111, DataRamWord.FIRST, 0, 32768),
    B(1, "B", "BETA", 0b111000, DataRamWord.FIRST, 3, 36864),
    G(2, "G", "GAMMA", 0b111000000, DataRamWord.FIRST, 6, 40960 ),
    F(3, "F", "PHI", 0b111, DataRamWord.SECOND,0, 45056),
    P(4, "P", "PSI", 0b111000, DataRamWord.SECOND,3, 49152),
    T(5, "T", "TETHA", 0b111000000, DataRamWord.SECOND, 6, 53248);

    private Integer number;
    private String shortName;
    private String longName;
    private Integer phaseMask;
    private Integer shiftValue;
    private Integer tableAddress;

    DataRamWord dataRamWord;

    ChannelForPhases(Integer number, String shortName,  String longName, Integer phaseMask, DataRamWord dataRamWord, Integer shiftValue, Integer tableAddress) {
        this.number = number;
        this.shortName = shortName;
        this.longName = longName;
        this.phaseMask = phaseMask;
        this.dataRamWord = dataRamWord;
        this.shiftValue = shiftValue;
        this.tableAddress = tableAddress;
    }

    public  static ChannelForPhases getChannel(Integer number){
        for(ChannelForPhases value :  ChannelForPhases.values() ){
            if(value.getNumber().equals(number)){
                return value;
            }
        }
        return null;
    }

    public DataRamWord getDataRamWord() {
        return dataRamWord;
    }

    public Integer getNumber() {
        return number;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public Integer getShiftValue() {
        return shiftValue;
    }

    public Integer getPhaseMask() {
        return phaseMask;
    }

    public Integer getTableAddress() {
        return tableAddress;
    }
}
