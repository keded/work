package pro.filatov.workstation4ceb.model.uart;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by yuri.filatov on 08.08.2016.
 */
public class PacketUtilTest {

    @Test
    public void createBoxPacketTest(){

        byte [] data = {0x11, 0x22, 0x33};
        byte []res  = PacketHelper.createBoxPacketToImitator(data);
        Assert.assertEquals(9, res.length);


    }



}
