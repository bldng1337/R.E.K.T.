//https://randomnerdtutorials.com/esp32-client-server-wi-fi/ 
//https://makesmart.net/arduino-ide-arbeiten-mit-json-objekten-fur-einsteiger/
//for references
//Include required libraries for wifi connection
#include "WiFi.h"
#include "ESPAsyncWebServer.h"
#include <ArduinoJson.h>

//Netzwerk Referenzen eingeben
const char* ssid = "ESP32-Access-Point-REKT";
const char* password = "123456789";

//AsyncWebServer auf Port 80 erstellen
AsyncWebServer server(80);

//-------------------------------------------------
//Wert auslesen Sensor
String getTemp(){
  return 1;
}

String getDichte(){
  return 1;
}
//-------------------------------------------------

void setup() {
  //öffnet die serielle Schnittstelle und stellt die Datenrate auf 115200 Bit/s ein. Benutzt für Debuggig purposes
  Serial.begin(115200); 

  //ESP als Acesspoint setzen
  Serial.print("Setting AP (Access Point)…");
  // kein password Parameter -> open Access Point
  WiFi.softAP(ssid, password);

  //AcessPointIP Adresse herausfinden um sich damit verbinden zu können
  IPAddress IP = WiFi.softAPIP();
  Serial.print("AP IP address: ");
  Serial.println(IP);

/* eventuelle Datenübertragung mit JSON?
    DynamicJsonDocument configJSON(1024);
    configJSON["temperatur"] = getTemp();
    configJSON["Dichte"] = getDichte();
  
    serializeJson(configJSON, Serial); 
*/


  //requests werden gehandelt, Werte werden als Cahr übergeben --> JSON??
    server.on("/Temperatur", HTTP_GET, [](AsyncWebServerRequest *request){
    request->send_P(200, "text/plain", getTemp().c_str());
  });
  server.on("/humidity", HTTP_GET, [](AsyncWebServerRequest *request){
    request->send_P(200, "text/plain", readHumi().c_str());
  });

  //start server
  server.begin();

}

void loop() {
  // put your main code here, to run repeatedly:

}
