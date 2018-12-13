package pro.filatov.workstation4ceb.model.uart;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 20.01.2017.
 */
public class StorageBind {

    List<Integer> values;

    public StorageBind() {
        values = new ArrayList<>();
    }

    public void addValue(Integer value){
        values.add(value);
    }

    public String getRow(){
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for(Integer value : values){
            if(i != 0){
                sb.append(", ");
            }
            sb.append(String.valueOf(value));
            i++;
        }
        return sb.toString();
    }

}
