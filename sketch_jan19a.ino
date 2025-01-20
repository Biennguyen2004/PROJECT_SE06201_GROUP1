#include <WiFi.h>
#include <WiFiClientSecure.h>
#include <ArduinoJson.h>
#include <PubSubClient.h>

// Định nghĩa GPIO cho MQ-5
#define GAS_SENSOR_PIN 34  // Chân GPIO 34 kết nối với MQ-5 (Analog Output)

// Thông tin WiFi
const char* ssid = "aaa";          // Tên WiFi
const char* password = "00000000"; // Mật khẩu WiFi

// Thông tin MQTT Broker
const char* mqtt_server = "ece68ed686b04d8e8f20ce3cdb8bdaf0.s1.eu.hivemq.cloud"; // HiveMQ Broker
const int mqtt_port = 8883;                     // SSL Port
const char* mqtt_username = "cuong";           // Username của HiveMQ Cloud
const char* mqtt_password = "Cuong2603";       // Password của HiveMQ Cloud
const char* mqtt_topic = "home/fire_alert";    // Topic gửi dữ liệu

WiFiClientSecure espClient;
PubSubClient client(espClient);

void setup_wifi() {
  delay(3000);
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);

  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.print(".");
  }

  Serial.println();
  Serial.println("WiFi connected");
}

void reconnect() {
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    // Sử dụng username/password để kết nối MQTT
    if (client.connect("ESP32_Client", mqtt_username, mqtt_password)) {
      Serial.println("Connected to MQTT Broker!");
    } else {
      Serial.print("Failed, rc=");
      Serial.println(client.state());
      delay(5000); // Thử lại sau 5 giây
    }
  }
}

void setup() {
  Serial.begin(115200);

  setup_wifi();

  // Cấu hình chứng chỉ SSL (hoặc dùng espClient.setInsecure() nếu cần)
  espClient.setInsecure(); // Bỏ qua kiểm tra chứng chỉ SSL

  // Cấu hình MQTT Broker
  client.setServer(mqtt_server, mqtt_port);
}

void loop() {
  if (!client.connected()) {
    reconnect();
  }
  client.loop();

  // Tạo payload JSON đơn giản
  StaticJsonDocument<200> jsonDoc;
  jsonDoc["device_id"] = "esp32_001";
  jsonDoc["gas_value"] = analogRead(GAS_SENSOR_PIN); // Đọc giá trị MQ-5
  jsonDoc["timestamp"] = millis();

  char payload[256];
  serializeJson(jsonDoc, payload);

  // Gửi dữ liệu qua MQTT
  client.publish(mqtt_topic, payload);

  // Hiển thị dữ liệu lên Serial Monitor
  Serial.print("Payload sent: ");
  Serial.println(payload);

  delay(2000); // Đợi 2 giây trước khi gửi lần tiếp theo
}
