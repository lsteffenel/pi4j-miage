// Distributed with a free-will license.
// Use it any way you want, profit or free, provided it fits in the licenses of its associated works.
// BMP280
// This code is designed to work with the BMP280_I2CS I2C Mini Module available from ControlEverything.com.
// https://www.controleverything.com/content/Barometer?sku=BMP280_I2CSs#tabs-0-product_tabset-2

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import java.io.IOException;

public class BMP280
{
	private I2CBus bus = null;
	private I2CDevice device = null;

	public BMP280() throws Exception {
		// Create I2C bus
		bus = I2CFactory.getInstance(I2CBus.BUS_1);
		// Get I2C device, BMP280 I2C address is 0x76(108)
		device = bus.getDevice(0x76);	
	}

public void testingLights(Double temperature) {
         /* on essaie de alumer le led */
        try {
          final GpioController gpio = GpioFactory.getInstance();
        
           GpioPinDigitalOutput green = gpio.provisionDigitalOutputPin(
                RaspiPin.GPIO_21, PinState.LOW);
           GpioPinDigitalOutput blue = gpio.provisionDigitalOutputPin(
                RaspiPin.GPIO_22, PinState.LOW);
           GpioPinDigitalOutput red = gpio.provisionDigitalOutputPin(
                RaspiPin.GPIO_23, PinState.LOW);
           
           if(temperature>25) red.high();
           if(temperature<24) blue.high();
           if(temperature>=24 && temperature<=25) green.high();
           gpio.unprovisionPin(blue);
           gpio.unprovisionPin(red);
           gpio.unprovisionPin(green);
           gpio.shutdown();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


	public double getTemp(boolean Fahrenheit) throws Exception {
		// Read 24 bytes of data from address 0x88(136)
		byte[] b1 = new byte[24];
		device.read(0x88, b1, 0, 24);
		
		// Convert the data
		// temp coefficents
		int dig_T1 = (b1[0] & 0xFF) + ((b1[1] & 0xFF) * 256);
		int dig_T2 = (b1[2] & 0xFF) + ((b1[3] & 0xFF) * 256);
		if(dig_T2 > 32767)
		{
			dig_T2 -= 65536;
		}
		int dig_T3 = (b1[4] & 0xFF) + ((b1[5] & 0xFF) * 256);
		if(dig_T3 > 32767)
		{
			dig_T3 -= 65536;
		}
		
		// pressure coefficents
		int dig_P1 = (b1[6] & 0xFF) + ((b1[7] & 0xFF) * 256);
		int dig_P2 = (b1[8] & 0xFF) + ((b1[9] & 0xFF) * 256);
		if(dig_P2 > 32767)
		{
			dig_P2 -= 65536;
		}
		int dig_P3 = (b1[10] & 0xFF) + ((b1[11] & 0xFF) * 256);
		if(dig_P3 > 32767)
		{
			dig_P3 -= 65536;
		}
		int dig_P4 = (b1[12] & 0xFF) + ((b1[13] & 0xFF) * 256);
		if(dig_P4 > 32767)
		{
			dig_P4 -= 65536;
		}
		int dig_P5 = (b1[14] & 0xFF) + ((b1[15] & 0xFF) * 256);
		if(dig_P5 > 32767)
		{
			dig_P5 -= 65536;
		}
		int dig_P6 = (b1[16] & 0xFF) + ((b1[17] & 0xFF) * 256);
		if(dig_P6 > 32767)
		{
			dig_P6 -= 65536;
		}
		int dig_P7 = (b1[18] & 0xFF) + ((b1[19] & 0xFF) * 256);
		if(dig_P7 > 32767)
		{
			dig_P7 -= 65536;
		}
		int dig_P8 = (b1[20] & 0xFF) + ((b1[21] & 0xFF) * 256);
		if(dig_P8 > 32767)
		{
			dig_P8 -= 65536;
		}
		int dig_P9 = (b1[22] & 0xFF) + ((b1[23] & 0xFF) * 256);
		if(dig_P9 > 32767)
		{
			dig_P9 -= 65536;
		}
		// Select control measurement register
		// Normal mode, temp and pressure over sampling rate = 1
		device.write(0xF4 , (byte)0x27);
		// Select config register
		// Stand_by time = 1000 ms
		device.write(0xF5 , (byte)0xA0);
		Thread.sleep(500);
		
		// Read 8 bytes of data from address 0xF7(247)
		// pressure msb1, pressure msb, pressure lsb, temp msb1, temp msb, temp lsb, humidity lsb, humidity msb
		byte[] data = new byte[8];
		device.read(0xF7, data, 0, 8);
		
		// Convert pressure and temperature data to 19-bits
		long adc_p = (((long)(data[0] & 0xFF) * 65536) + ((long)(data[1] & 0xFF) * 256) + (long)(data[2] & 0xF0)) / 16;
		long adc_t = (((long)(data[3] & 0xFF) * 65536) + ((long)(data[4] & 0xFF) * 256) + (long)(data[5] & 0xF0)) / 16;
		
		// Temperature offset calculations
		double var1 = (((double)adc_t) / 16384.0 - ((double)dig_T1) / 1024.0) * ((double)dig_T2);
		double var2 = ((((double)adc_t) / 131072.0 - ((double)dig_T1) / 8192.0) *
						(((double)adc_t)/131072.0 - ((double)dig_T1)/8192.0)) * ((double)dig_T3);
		double t_fine = (long)(var1 + var2);
		double cTemp = (var1 + var2) / 5120.0;
		double fTemp = cTemp * 1.8 + 32;
		if (Fahrenheit == true) {
			return cTemp * 1.8 + 32;
		}
		return cTemp;	
	}


	public double getPressure() throws Exception {
	
		// Read 24 bytes of data from address 0x88(136)
		byte[] b1 = new byte[24];
		device.read(0x88, b1, 0, 24);
		
		// Convert the data
		// temp coefficents
		int dig_T1 = (b1[0] & 0xFF) + ((b1[1] & 0xFF) * 256);
		int dig_T2 = (b1[2] & 0xFF) + ((b1[3] & 0xFF) * 256);
		if(dig_T2 > 32767)
		{
			dig_T2 -= 65536;
		}
		int dig_T3 = (b1[4] & 0xFF) + ((b1[5] & 0xFF) * 256);
		if(dig_T3 > 32767)
		{
			dig_T3 -= 65536;
		}
		
		// pressure coefficents
		int dig_P1 = (b1[6] & 0xFF) + ((b1[7] & 0xFF) * 256);
		int dig_P2 = (b1[8] & 0xFF) + ((b1[9] & 0xFF) * 256);
		if(dig_P2 > 32767)
		{
			dig_P2 -= 65536;
		}
		int dig_P3 = (b1[10] & 0xFF) + ((b1[11] & 0xFF) * 256);
		if(dig_P3 > 32767)
		{
			dig_P3 -= 65536;
		}
		int dig_P4 = (b1[12] & 0xFF) + ((b1[13] & 0xFF) * 256);
		if(dig_P4 > 32767)
		{
			dig_P4 -= 65536;
		}
		int dig_P5 = (b1[14] & 0xFF) + ((b1[15] & 0xFF) * 256);
		if(dig_P5 > 32767)
		{
			dig_P5 -= 65536;
		}
		int dig_P6 = (b1[16] & 0xFF) + ((b1[17] & 0xFF) * 256);
		if(dig_P6 > 32767)
		{
			dig_P6 -= 65536;
		}
		int dig_P7 = (b1[18] & 0xFF) + ((b1[19] & 0xFF) * 256);
		if(dig_P7 > 32767)
		{
			dig_P7 -= 65536;
		}
		int dig_P8 = (b1[20] & 0xFF) + ((b1[21] & 0xFF) * 256);
		if(dig_P8 > 32767)
		{
			dig_P8 -= 65536;
		}
		int dig_P9 = (b1[22] & 0xFF) + ((b1[23] & 0xFF) * 256);
		if(dig_P9 > 32767)
		{
			dig_P9 -= 65536;
		}
		// Select control measurement register
		// Normal mode, temp and pressure over sampling rate = 1
		device.write(0xF4 , (byte)0x27);
		// Select config register
		// Stand_by time = 1000 ms
		device.write(0xF5 , (byte)0xA0);
		Thread.sleep(500);
		
		// Read 8 bytes of data from address 0xF7(247)
		// pressure msb1, pressure msb, pressure lsb, temp msb1, temp msb, temp lsb, humidity lsb, humidity msb
		byte[] data = new byte[8];
		device.read(0xF7, data, 0, 8);
		
		// Convert pressure and temperature data to 19-bits
		long adc_p = (((long)(data[0] & 0xFF) * 65536) + ((long)(data[1] & 0xFF) * 256) + (long)(data[2] & 0xF0)) / 16;
		long adc_t = (((long)(data[3] & 0xFF) * 65536) + ((long)(data[4] & 0xFF) * 256) + (long)(data[5] & 0xF0)) / 16;
		
		// Temperature offset calculations
		double var1 = (((double)adc_t) / 16384.0 - ((double)dig_T1) / 1024.0) * ((double)dig_T2);
		double var2 = ((((double)adc_t) / 131072.0 - ((double)dig_T1) / 8192.0) *
						(((double)adc_t)/131072.0 - ((double)dig_T1)/8192.0)) * ((double)dig_T3);
		double t_fine = (long)(var1 + var2);
		
		// Pressure offset calculations
		var1 = ((double)t_fine / 2.0) - 64000.0;
		var2 = var1 * var1 * ((double)dig_P6) / 32768.0;
		var2 = var2 + var1 * ((double)dig_P5) * 2.0;
		var2 = (var2 / 4.0) + (((double)dig_P4) * 65536.0);
		var1 = (((double) dig_P3) * var1 * var1 / 524288.0 + ((double) dig_P2) * var1) / 524288.0;
		var1 = (1.0 + var1 / 32768.0) * ((double)dig_P1);
		double p = 1048576.0 - (double)adc_p;
		p = (p - (var2 / 4096.0)) * 6250.0 / var1;
		var1 = ((double) dig_P9) * p * p / 2147483648.0;
		var2 = p * ((double) dig_P8) / 32768.0;
		double pressure = (p + (var1 + var2 + ((double)dig_P7)) / 16.0) / 100;

		return pressure;
	}

	public static void main(String args[]) throws Exception {
		BMP280 sensor = new BMP280();
		
		// Output data to screen
		System.out.printf("Pressure : %.2f hPa %n", sensor.getPressure());
		System.out.printf("Temperature in Celsius : %.2f C %n", sensor.getTemp(false));
		System.out.printf("Temperature in Fahrenheit : %.2f F %n", sensor.getTemp(true));
    Double temperature = sensor.getTemp(false);
    sensor.testingLights(temperature);
	}
}
