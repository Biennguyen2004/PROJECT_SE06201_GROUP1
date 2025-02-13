#include <WiFi.h>
#include <WiFiClientSecure.h>
#include <PubSubClient.h>
#include <DHT.h>
#include <ArduinoJson.h>
#include <ESP32Servo.h>

//  Thông tin WiFi
const char* ssid = "aaa";       
const char* password = "00000000"; 

//  Thông tin MQTT Broker (HiveMQ Cloud)
const char* mqtt_server = "b2ab94290f9846df90474d2bb8772308.s1.eu.hivemq.cloud";
const int mqtt_port = 8883; // SSL Port
const char* mqtt_username = "nguyenbien";
const char* mqtt_password = "4!xH7QbgfmUXhge";

//  Định nghĩa GPIO cho từng cảm biến
#define GAS_SENSOR 34       // MQ-5 (Analog)
#define DHTPIN 4            // Cảm biến DHT11
#define PIR_SENSOR 33       // PIR AM312
#define WATER_SENSOR 32     // Cảm biến nước
#define LIGHT_SENSOR 18     // LM393 Light Sensor
#define TRIG_PIN 26         // HC-SR04 (Khoảng cách)
#define ECHO_PIN 27
#define SERVO_PIN 25        // Servo Motor (Mở cửa)
#define RELAY_PIN 19        // Điều khiển Relay bật/tắt đèn
#define LED_R 21            // LED RGB (Màu đỏ)
#define LED_G 22            // LED RGB (Màu xanh lá)
#define LED_B 23            // LED RGB (Màu xanh dương)
#define BUZZER 5            // Còi báo động

DHT dht(DHTPIN, DHT11);
WiFiClientSecure espClient;
PubSubClient client(espClient);
Servo doorServo;

void setup_wifi() {
  Serial.println(" Đang kết nối WiFi...");
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.print(".");
  }
  Serial.println(" WiFi đã kết nối!");
}

void reconnect_mqtt() {
  while (!client.connected()) {
    Serial.print(" Đang kết nối MQTT...");
    if (client.connect("ESP32_Client", mqtt_username, mqtt_password)) {
      Serial.println(" Kết nối MQTT thành công!");
    } else {
      Serial.print(" Kết nối thất bại, mã lỗi = ");
      Serial.println(client.state());
      delay(5000);
    }
  }
}

void publish_data(const char* topic, JsonDocument& doc) {
  char buffer[512];
  serializeJson(doc, buffer);
  client.publish(topic, buffer);
}

void send_sensor_data() {
  JsonDocument doc;

  //  Cảm biến khí gas MQ-5
  int gasValue = analogRead(GAS_SENSOR);
  doc["device_id"] = "esp32_01";
  doc["type"] = "gas_sensor";
  doc["location"] = "kitchen";
  doc["value"] = gasValue;
  doc["status"] = (gasValue >= 1000) ? "warning" : "normal";
  publish_data("smarthome/kitchen/gas/esp32_01", doc);
  
  //  Cảm biến nhiệt độ & độ ẩm DHT11
  float temp = dht.readTemperature();
  float hum = dht.readHumidity();
  doc.clear();
  doc["device_id"] = "esp32_02";
  doc["type"] = "temperature_humidity";
  doc["location"] = "kitchen";
  doc["temperature"] = temp;
  doc["humidity"] = hum;
  publish_data("smarthome/kitchen/climate/esp32_02", doc);

  //  Cảm biến chuyển động PIR
  int motion = digitalRead(PIR_SENSOR);
  doc.clear();
  doc["device_id"] = "esp32_03";
  doc["type"] = "motion_sensor";
  doc["location"] = "living_room";
  doc["value"] = motion;
  doc["status"] = (motion == HIGH) ? "motion_detected" : "no_motion";
  publish_data("smarthome/living_room/motion/esp32_03", doc);

  //  Cảm biến nước
  int waterDetected = digitalRead(WATER_SENSOR);
  doc.clear();
  doc["device_id"] = "esp32_04";
  doc["type"] = "water_sensor";
  doc["location"] = "bathroom";
  doc["value"] = waterDetected;
  doc["status"] = (waterDetected == HIGH) ? "water_detected" : "dry";
  publish_data("smarthome/bathroom/water/esp32_04", doc);

  //  Cảm biến ánh sáng LM393
  int lightLevel = analogRead(LIGHT_SENSOR);
  doc.clear();
  doc["device_id"] = "esp32_05";
  doc["type"] = "light_sensor";
  doc["location"] = "living_room";
  doc["value"] = lightLevel;
  doc["status"] = (lightLevel < 200) ? "dark" : "bright";
  publish_data("smarthome/living_room/light/esp32_05", doc);

  //  Cảm biến khoảng cách HC-SR04 + Servo cửa thông minh
  digitalWrite(TRIG_PIN, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG_PIN, LOW);
  long duration = pulseIn(ECHO_PIN, HIGH);
  int distance = duration * 0.034 / 2;

  doc.clear();
  doc["device_id"] = "esp32_06";
  doc["type"] = "door";
  doc["location"] = "main_door";
  doc["distance"] = distance;
  doc["status"] = (distance < 10) ? "open" : "closed";
  publish_data("smarthome/main_door/door/esp32_06", doc);

  //  Trạng thái Relay (đèn thông minh)
  bool relayState = digitalRead(RELAY_PIN);
  doc.clear();
  doc["device_id"] = "esp32_07";
  doc["type"] = "relay";
  doc["location"] = "living_room";
  doc["status"] = (relayState == HIGH) ? "on" : "off";
  publish_data("smarthome/living_room/relay/esp32_07", doc);
}

void setup() {
  Serial.begin(115200);
  setup_wifi();
  espClient.setInsecure();
  client.setServer(mqtt_server, mqtt_port);

  pinMode(GAS_SENSOR, INPUT);
  pinMode(PIR_SENSOR, INPUT);
  pinMode(WATER_SENSOR, INPUT);
  pinMode(LIGHT_SENSOR, INPUT);
  pinMode(TRIG_PIN, OUTPUT);
  pinMode(ECHO_PIN, INPUT);
  pinMode(BUZZER, OUTPUT);
  pinMode(RELAY_PIN, OUTPUT);

  dht.begin();
  doorServo.attach(SERVO_PIN);
}

void loop() {
  if (!client.connected()) {
    reconnect_mqtt();
  }
  client.loop();
  
  send_sensor_data();
  delay(5000);
}
