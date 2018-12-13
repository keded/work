package pro.filatov.workstation4ceb.model.uart.rom;

import pro.filatov.workstation4ceb.model.uart.PacketHelper;

/**
 * Created by user on 16.01.2017.
 */
public class PacketToStorageHelper {


    private static final Integer id = 0x4000;

    public static byte [] createPacket(Integer address){
        byte [] packet = PacketHelper.i2b(id);
        packet = PacketHelper.addDataToPacket(packet, PacketHelper.i2b(address));
        return packet;


    }


}
