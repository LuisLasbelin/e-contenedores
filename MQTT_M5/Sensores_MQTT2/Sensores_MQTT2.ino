#include <ArduinoMqttClient.h>
#if defined(ARDUINO_SAMD_MKRWIFI1010) || defined(ARDUINO_SAMD_NANO_33_IOT) || defined(ARDUINO_AVR_UNO_WIFI_REV2)
  #include <WiFiNINA.h>
#elif defined(ARDUINO_SAMD_MKR1000)
  #include <WiFi101.h>
#elif defined(ARDUINO_ESP8266_ESP12)
  #include <ESP8266WiFi.h>
#endif
#include <WiFiServer.h>
#include <WiFiClient.h>
#include <WiFi.h>

///////please enter your sensitive data in the Secret tab/arduino_secrets.h
char ssid[] = "vodafoneD812";              //"DESKTOP-PCG8QG8 6144";        // your network SSID (name)
char pass[] = "V2e9rT4y5jL";              //"12345678";    // your network password (use for WPA, or use as key for WEP)

// To connect with SSL/TLS:
// 1) Change WiFiClient to WiFiSSLClient.
// 2) Change port value from 1883 to 8883.
// 3) Change broker value to a server with a known SSL/TLS root certificate 
//    flashed in the WiFi module.

WiFiClient wifiClient;
MqttClient mqttClient(wifiClient);

const char broker[] = "mqtt.eclipse.org";
int        port     = 1883;
const char topic[]  = "recycle/practica/distancia";
const char topic2[] = "recycle/practica/POWER";

const long interval = 1000;
unsigned long previousMillis = 0;
const int EchoPin = 5;
const int TriggerPin = 2;
const int EchoPin2 = 16;
const int TriggerPin2 = 17;




void setup() {
  pinMode(TriggerPin, OUTPUT); 
  pinMode(EchoPin, INPUT);
   pinMode(TriggerPin2, OUTPUT); 
  pinMode(EchoPin2, INPUT);
  //Initialize serial and wait for port to open:
  Serial.begin(9600);
  while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB port only
  }

  // attempt to connect to Wifi network:
  Serial.print("Attempting to connect to WPA SSID: ");
  Serial.println(ssid);
  while (WiFi.begin(ssid, pass) != WL_CONNECTED) {
    // failed, retry
    Serial.print(".");
    delay(5000);
  }


  Serial.println("You're connected to the network");
  Serial.println();

  // You can provide a unique client ID, if not set the library uses Arduino-millis()
  // Each client must have a unique client ID
  // mqttClient.setId("clientId");

  // You can provide a username and password for authentication
  // mqttClient.setUsernamePassword("username", "password");

  Serial.print("Attempting to connect to the MQTT broker: ");
  Serial.println(broker);

  if (!mqttClient.connect(broker, port)) {
    Serial.print("MQTT connection failed! Error code = ");
    Serial.println(mqttClient.connectError());

    while (1);
  }

  Serial.println("You're connected to the MQTT broker!");
  Serial.println();

  // set the message receive callback
  mqttClient.onMessage(onMqttMessage);

  Serial.print("Subscribing to topic: ");
  Serial.println(topic);
  Serial.println();

  // subscribe to a topic
  mqttClient.subscribe(topic);
  mqttClient.subscribe(topic2);

  // topics can be unsubscribed using:
  // mqttClient.unsubscribe(topic);

  Serial.print("Waiting for messages on topic: ");
  Serial.println(topic);
  Serial.println(topic2);
}

void loop() {
  // call poll() regularly to allow the library to receive MQTT messages and
  // send MQTT keep alives which avoids being disconnected by the broker
  mqttClient.poll();

  // avoid having delays in loop, we'll use the strategy from BlinkWithoutDelay
  // see: File -> Examples -> 02.Digital -> BlinkWithoutDelay for more info
  

  
}

void onMqttMessage(int messageSize) {
  // we received a message, print out the topic and contents
 /* Serial.print("Received a message with topic '");
  Serial.print("ESto es el Topic ");
  Serial.print(mqttClient.messageTopic());
  Serial.println("--------------------");
  Serial.print("', length ");
  Serial.print(messageSize);
  Serial.println(" bytes:");
  */
  if (mqttClient.messageTopic() == "recycle/practica/POWER"){
    contar();
  }

  // use the Stream interface to print the contents
  while (mqttClient.available()) {
    Serial.print((char)mqttClient.read());
  }
  Serial.println();
  Serial.println();
}


void contar(){
  
    // save the last time a message was sent
    Serial.print("Sending message to topic: ");
    Serial.println(topic);
    Serial.print("distancia1: ");
    Serial.println(String(distancia(TriggerPin, EchoPin)));

    // send message, the Print interface can be used to set the message contents
    if (distancia(TriggerPin, EchoPin) < 52){
    mqttClient.beginMessage(topic);
    mqttClient.print("M52-CuboVidrio-" + String(map(distancia(TriggerPin, EchoPin),0,44,100,0)));
    mqttClient.endMessage();
    }
     delay(1000);
     if(distancia(TriggerPin2, EchoPin2) < 52) {
    mqttClient.beginMessage(topic);
    mqttClient.print("M52-CuboOrganico-" + String(map(distancia(TriggerPin2, EchoPin2),0,44,100,0)));
    mqttClient.endMessage();
    }
    Serial.println();
    delay(1000);
  
}
int distancia(int TriggerPin, int EchoPin) {
  long duracion, distanciaCm;
  digitalWrite(TriggerPin, LOW); //nos aseguramos señal baja al principio
  delayMicroseconds(10);
  digitalWrite(TriggerPin, HIGH); //generamos pulso de 10us 
  delayMicroseconds(10);
  digitalWrite(TriggerPin, LOW);
  duracion = pulseIn(EchoPin, HIGH); //medimos el tiempo del pulso 
  distanciaCm = duracion * 10 / 292 / 2; //convertimos a distancia 
  return distanciaCm;
}
