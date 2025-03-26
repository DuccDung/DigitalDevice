#include <TinyGPS++.h>
#include <SoftwareSerial.h>
#include <ESP8266WiFi.h>
#include <PubSubClient.h>

// Cấu hình WiFi và MQTT
const char* ssid = "Xuong cafe T1";
const char* password = "cafetuoi";
const char* mqtt_server = "bbb04b2c528242358401e2add20b296a.s1.eu.hivemq.cloud";
const int mqtt_port = 8883;
const char* mqtt_username = "hivemq.webclient.1735043576854";
const char* mqtt_password = "rAhT10mH$8Btos;6I,Z.";

WiFiClientSecure espClient;
PubSubClient client(espClient);

// Cổng kết nối GPS
SoftwareSerial gpsSerial(D1, D2); // RX, TX
#define LEDpin1 D5  // Đèn LED cảnh báo
TinyGPSPlus gps;

void setup() {
  Serial.begin(9600);
  gpsSerial.begin(9600);
  pinMode(LEDpin1, OUTPUT);
  digitalWrite(LEDpin1, LOW);
  Serial.println("Đang chờ tín hiệu GPS...");

  WiFi.begin(ssid, password);
  Serial.print("Đang kết nối WiFi");
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.print(".");
  }
  Serial.println("\nWiFi đã kết nối");

  espClient.setInsecure(); // Không kiểm tra chứng chỉ SSL
  client.setServer(mqtt_server, mqtt_port);
  reconnect();
}

void reconnect() {
  while (!client.connected()) {
    Serial.print("Đang kết nối MQTT...");
    if (client.connect("ESP8266Client", mqtt_username, mqtt_password)) {
      Serial.println(" MQTT đã kết nối");
    } else {
      Serial.print(" thất bại, mã lỗi: ");
      Serial.print(client.state());
      Serial.println(" Thử lại sau 5 giây...");
      delay(5000);
    }
  }
}

void sendGPSData(float lat, float lng, float alt, float speed) {
  char payload[150];
  snprintf(payload, sizeof(payload), 
           "{\"lat\":%.6f, \"lng\":%.6f, \"alt\":%.2f, \"speed\":%.2f}", 
           lat, lng, alt, speed);
  client.publish("d_016", payload);
  Serial.print("Đã gửi: ");
  Serial.println(payload);
}

void loop() {
  if (!client.connected()) {
    reconnect();
  }
  client.loop();

  while (gpsSerial.available() > 0) {
    char c = gpsSerial.read();
    if (gps.encode(c)) {
      if (gps.location.isUpdated()) {
        float lat = gps.location.lat();
        float lng = gps.location.lng();
        float alt = gps.altitude.meters();
        float speed = gps.speed.kmph();  // Đo tốc độ (km/h)

        Serial.print("Vĩ độ: "); Serial.println(lat, 6);
        Serial.print("Kinh độ: "); Serial.println(lng, 6);
        Serial.print("Độ cao: "); Serial.println(alt);
        Serial.print("Tốc độ: "); Serial.print(speed); Serial.println(" km/h");

        sendGPSData(lat, lng, alt, speed);

        // Bật đèn LED cảnh báo nếu tốc độ vượt quá 20 km/h
        if (speed > 20) {
          digitalWrite(LEDpin1, HIGH);
        } else {
          digitalWrite(LEDpin1, LOW);
        }
      }
    }
  }
}
