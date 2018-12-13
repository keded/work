package pro.filatov.workstation4ceb.pgm;

import org.junit.Test;

/**
 * Created by yuri.filatov on 15.09.2016.
 */
public class PgmHelperTest {


    private final Double delta= 0.00000002;


    @Test
    public void calcTest(){
        calcTableValues(new Double(0.003489));


    }



    public void calcTableValues(Double K){

        Double prev =  Math.sqrt(K*6);
        for (int i = 7; i <= 512+6; i++ ){
            Integer res = Long.valueOf(Math.round((Math.sqrt(K*i) - prev)/delta)).intValue();
            res = res | 0x10000;
            String str = Integer.toHexString(res).substring(1);

        }



    }



}
