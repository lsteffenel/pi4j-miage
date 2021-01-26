
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.platform.PlatformManager;
import com.pi4j.system.SystemInfo;
import java.io.IOException;
import java.util.Scanner;


/**
 * permier test avec GPIO 
 * 
 * @author kirsch
 */
public class LEDTest {
    public static void main(String[] args)  {
        
        System.out.println(" ------------ SYSINFO --------------------- ");
        sysInfo();
        
        System.out.println("------- LED --------------- ");
        testingLigths();
        
        
        System.out.println("--------- Mixing up -------------");
        mixing(true,false,true);
        mixing(false,true,true);
        mixing(true,true,false);
        
        
    }

    private static void pause() {
        final Scanner in = new Scanner(System.in);

        // wait for user input
        System.out.println("... press 'c' <ENTER> to continue ...");
        in.next();
        
    }
    
    private static void sysInfo() {
        /* first some system info */
        try{
            System.out.println("Platform Name     :  " + PlatformManager.getPlatform().getLabel());
            System.out.println("Platform ID       :  " + PlatformManager.getPlatform().getId());
        } catch(UnsupportedOperationException ex){ 
                System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
        }
        
        try{System.out.println("CPU Architecture  :  " + SystemInfo.getCpuArchitecture());}
        catch(Exception ex){System.err.println(ex.getClass().getName() + ": " + ex.getMessage());}
        try{System.out.println("CPU Part          :  " + SystemInfo.getCpuPart());}
        catch(Exception ex){System.err.println(ex.getClass().getName() + ": " + ex.getMessage());}
        try{System.out.println("CPU Temperature   :  " + SystemInfo.getCpuTemperature());}
        catch(Exception ex){System.err.println(ex.getClass().getName() + ": " + ex.getMessage());}
        try{System.out.println("CPU Core Voltage  :  " + SystemInfo.getCpuVoltage());}
        catch(Exception ex){System.err.println(ex.getClass().getName() + ": " + ex.getMessage());}
        try{System.out.println("CPU Model Name    :  " + SystemInfo.getModelName());}
        catch(Exception ex){System.err.println(ex.getClass().getName() + ": " + ex.getMessage());}
        
        
        try{System.out.println("ARM Frequency     :  " + SystemInfo.getClockFrequencyArm());}
        catch(Exception ex){System.err.println(ex.getClass().getName() + ": " + ex.getMessage());}
        try{System.out.println("CORE Frequency    :  " + SystemInfo.getClockFrequencyCore());}
        catch(Exception ex){System.err.println(ex.getClass().getName() + ": " + ex.getMessage());}      
    }

    private static void testingLigths() {
         /* on essaie de alumer le led */
        try {
          final GpioController gpio = GpioFactory.getInstance();
        
           GpioPinDigitalOutput green = gpio.provisionDigitalOutputPin(
                RaspiPin.GPIO_21, PinState.LOW);
           GpioPinDigitalOutput yellow = gpio.provisionDigitalOutputPin(
                RaspiPin.GPIO_22, PinState.LOW);
           GpioPinDigitalOutput red = gpio.provisionDigitalOutputPin(
                RaspiPin.GPIO_23, PinState.LOW);
           
           green.high();
           
           pause();
           
           green.low();
           red.high();
           
           pause();
           
           red.low();
           yellow.high();
           
           pause();
           red.high();
           green.high();
           
           pause();
           yellow.low();
           green.low();
           red.low();
            
           gpio.unprovisionPin(yellow);
           gpio.unprovisionPin(red);
           gpio.unprovisionPin(green);
           gpio.shutdown();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private static void mixing(boolean bred, boolean byellow, boolean bgreen) {
        final GpioController gpio = GpioFactory.getInstance();
        
           GpioPinDigitalOutput green = gpio.provisionDigitalOutputPin(
                RaspiPin.GPIO_21, PinState.LOW);
           GpioPinDigitalOutput yellow = gpio.provisionDigitalOutputPin(
                RaspiPin.GPIO_22, PinState.LOW);
           GpioPinDigitalOutput red = gpio.provisionDigitalOutputPin(
                RaspiPin.GPIO_23, PinState.LOW);
           
           if(bred) red.high();
           if(bgreen) green.high();
           if(byellow) yellow.high();
           
           pause();
           
           if(red.isHigh()) red.low();
           if(green.isHigh()) green.low();
           if(yellow.isHigh()) yellow.low();
           
           gpio.unprovisionPin(yellow);
           gpio.unprovisionPin(red);
           gpio.unprovisionPin(green);
           gpio.shutdown();
           
    }
    
}
