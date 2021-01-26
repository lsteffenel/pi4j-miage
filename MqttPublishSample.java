        import org.eclipse.paho.client.mqttv3.MqttClient;
        import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
        import org.eclipse.paho.client.mqttv3.MqttException;
        import org.eclipse.paho.client.mqttv3.MqttMessage;
        import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
	
	import com.pi4j.io.i2c.I2CBus;
	import com.pi4j.io.i2c.I2CDevice;
	import com.pi4j.io.i2c.I2CFactory;
	import java.io.IOException;

        public class MqttPublishSample {

        public static void main(String[] args) throws Exception {
	    BMP280 sensor = new BMP280();
            String topic        = "v1/devices/me/telemetry";
	    String static_value = "10"; // Static value. To replace by the BMP280 temperature reading
            String content      = "{\"temperature\":"+valeur_statique+"}"; // Json formatted entry
	    int qos             = 2;
            String site = "127.0.0.1"; // the dashbboard site address. Replace as described in the course
            String broker       = "tcp://"+site+":1883";
            String clientId     = "Pi1"; // this is the "TokenId" from your device. Replace as described in the course
            MemoryPersistence persistence = new MemoryPersistence();

            try {
                MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setUserName(clientId);
                sampleClient.connect(connOpts);
                System.out.println("Connected");
                System.out.println("Publishing message: "+content);
                MqttMessage message = new MqttMessage(content.getBytes());
                message.setQos(qos);
                sampleClient.publish(topic, message);
                System.out.println("Message published");
                sampleClient.disconnect();
                System.out.println("Disconnected");
                System.exit(0);
            } catch(MqttException me) {
                System.out.println("reason "+me.getReasonCode());
                System.out.println("msg "+me.getMessage());
                System.out.println("loc "+me.getLocalizedMessage());
                System.out.println("cause "+me.getCause());
                System.out.println("excep "+me);
                me.printStackTrace();
            }
        }
    }
