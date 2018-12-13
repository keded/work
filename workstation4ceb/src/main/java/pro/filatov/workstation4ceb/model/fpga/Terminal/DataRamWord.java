package pro.filatov.workstation4ceb.model.fpga.Terminal;

import javax.xml.crypto.Data;

/**
 * Created by y.k.filatov on 18.11.2016.
 */
public enum DataRamWord {

    FIRST(0),
    SECOND(1);


    private Integer number;

    DataRamWord(Integer number) {
        this.number = number;
    }

    public Integer getNumber() {
        return number;
    }

    public static DataRamWord getDataRamWord (Integer number){
        for(DataRamWord word : DataRamWord.values()){
            if(word.getNumber().equals(number)){
                return word;
            }
        }
        return null;
    }
}
