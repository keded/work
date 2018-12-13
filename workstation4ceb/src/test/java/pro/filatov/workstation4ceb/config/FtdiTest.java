package pro.filatov.workstation4ceb.config;

import com.ftdichip.usb.FTDI;
import com.ftdichip.usb.FTDIUtility;
import com.ftdichip.usb.enumerated.EFlowControl;
import com.ftdichip.usb.enumerated.ELineDatabits;
import com.ftdichip.usb.enumerated.ELineParity;
import com.ftdichip.usb.enumerated.ELineStopbits;
import org.junit.Test;
import org.usb4java.Context;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;

import javax.usb3.IUsbDevice;
import javax.usb3.exception.UsbException;
import javax.usb3.ri.UsbDeviceId;
import javax.usb3.utility.ByteUtility;
import javax.usb3.utility.JNINativeLibraryLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by yuri.filatov on 15.07.2016.
 */
public class FtdiTest {

    @Test
    public void sendPacketTest() throws Exception {
/*
        try {

            JNINativeLibraryLoader.load();
            Context jniContext = new Context();
            final int init = LibUsb.init(jniContext);

            final List<UsbDeviceId> current = new ArrayList<>();

            // Get device list from libusb and abort if it failed
            final DeviceList deviceList = new DeviceList();
            Collection<IUsbDevice> devices = FTDIUtility.findFTDIDevices();
            for (IUsbDevice iUsbDevice : devices) {
                System.out.println("FOUND FTDI device:  " + iUsbDevice);
            }

            IUsbDevice usbDevice = devices.iterator().next();
            FTDI ftdiDevice = FTDI.getInstance(usbDevice);
            // Read data from the FTDI device output buffer
            System.out.println("Read any data from the FTDI device output buffer");
            byte[]data = {34, 23, 34};

            ftdiDevice.configureSerialPort(9600, ELineDatabits.BITS_8, ELineStopbits.STOP_BIT_1, ELineParity.NONE, EFlowControl.DTR_DSR_HS);
            for (int i = 0; i<10; i++) {
                ftdiDevice.write(data);

            }
            byte[] usbFrame = ftdiDevice.read();
            while (usbFrame.length > 0) {
                System.out.println("   READ " + usbFrame.length + " bytes: " + ByteUtility.toString(usbFrame));
                usbFrame = ftdiDevice.read();
            }


        } catch (UsbException e){
            e.printStackTrace();
        }
*/

    }

}
