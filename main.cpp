/**
 * @file main.cpp
 * @author Turker Dylan
 * @brief 
 * @version 0.1
 * @date 2022-11-27
 * 
 * @copyright Copyright (c) 2022
 * 
 */

#include <Arduino.h>
#include <DNSServer.h>
#include <WiFi.h>
#include <AsyncTCP.h>
#include <ArduinoJson.h>
#include <ESPAsyncWebServer.h>

float temp=-1;
float hum=-1;
long lastread=-1;

//Netzwerk Referenzen eingeben
const char* ssid = "ESP32-Access-Point-REKT";
const char* password = "123456789";

//AsyncWebServer auf Port 80 erstellen
AsyncWebServer server(80);
//
//Wert auslesen Sensor
String getTemp(){
  return "Temp";
}

String getDichte(){
  return "Dichte";
}
//

void setup() 
{

  Serial2.begin (9600, SERIAL_7E1, 16, 17);
  Serial.begin (115200);

  //öffnet die serielle Schnittstelle und stellt die Datenrate auf 115200 Bit/s ein. Benutzt für Debuggig purposes

  //ESP als Acesspoint setzen
  //Serial.print("Setting AP (Access Point)…");
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
    server.on("/get", HTTP_GET, [](AsyncWebServerRequest *request){
    DynamicJsonDocument configJSON(1024);
    configJSON["temp"] = temp;
    configJSON["hum"] = hum;
    configJSON["timestamp"] = lastread;
    String a;
    serializeJson(configJSON, a); 
    request->send_P(200, "text/plain", a.c_str());
  });

  //start server
  server.begin();

}

void loop() 
{
    String a=Serial2.readString().substring(6);
    temp=a.substring(0,a.length()-2).substring(0,a.lastIndexOf("  ")).toFloat();
    hum=a.substring(0,a.length()-2).substring(a.lastIndexOf("  ")+2).toFloat();
    lastread=millis();
}