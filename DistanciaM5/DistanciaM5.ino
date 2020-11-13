#include <M5Stack.h>

const int EchoPin = 5;
const int TriggerPin = 2;

void setup() { 
  Serial.begin(115200); 
  pinMode(TriggerPin, OUTPUT); 
  pinMode(EchoPin, INPUT);
}

void loop() {
  if (Serial.available() > 0) {
      char command = (char) Serial.read(); 
      switch (command) {
         case 'H':
            Serial.println("Hola Mundo"); 
            break;
         case 'D':
            //Serial.print("Distancia: "); 
            Serial.println(distancia(TriggerPin, EchoPin)); 
            delay(1000);
      } 
   }
   
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
