#include <WiFi.h>
#include <WiFiClientSecure.h>
#include <PubSubClient.h>
#include <DHT.h>
#include <ArduinoJson.h>
#include <ESP32Servo.h>

// 🛜 WiFi Credentials
const char* ssid = "aaa";       
const char* password = "00000000"; 

// 🌐 MQTT Credentials (HiveMQ Cloud)
const char* mqtt_server = "506956350ef1467185352df5050e3aa7.s1.eu.hivemq.cloud";
const int mqtt_port = 8883; // SSL Port
const char* mqtt_username = "biendeptrai";
const char* mqtt_password = "FZ6VQ@hfFYQyTe2";

// 🖥 GPIO Pin Mapping
#define GAS_SENSOR 34      
#define DHTPIN 4           
#define PIR_SENSOR 33      
#define WATER_SENSOR 32    
#define TRIG_PIN 26        
#define ECHO_PIN 27        
#define SERVO_PIN 25       
#define BUZZER 5         
//*****************************************************************************************************************
// 🌟 Biến trạng thái cửa tự động & trạng thái cửa
bool auto_door_mode = false;
bool door_state = false; // false = Đóng, true = Mở

DHT dht(DHTPIN, DHT11);
WiFiClientSecure espClient;
PubSubClient client(espClient);
Servo doorServo;
//*****************************************************************************************************************
// 🔹 Kết nối WiFi
void setup_wifi() {
  Serial.print("🔗 Đang kết nối WiFi...");
  WiFi.begin(ssid, password);
  int attempts = 0;
  
  while (WiFi.status() != WL_CONNECTED && attempts < 15) {
    delay(1000);
    Serial.print(".");
    attempts++;
  }

  if (WiFi.status() == WL_CONNECTED) {
    Serial.println("\n✅ WiFi đã kết nối!");
  } else {
    Serial.println("\n❌ Lỗi: Không thể kết nối WiFi!");
  }
}
//*****************************************************************************************************************
// 🔹 Kết nối MQTT
void reconnect_mqtt() {
  while (!client.connected()) {
    Serial.print("🔄 Đang kết nối MQTT...");
    if (client.connect("ESP32_Client", mqtt_username, mqtt_password)) {
      Serial.println("✅ Kết nối MQTT thành công!");
      client.subscribe("openauto/door");  // 📡 Nhận lệnh bật/tắt auto
      client.subscribe("openclose/door"); // 📡 Nhận lệnh mở/đóng cửa khi auto tắt
      Serial.println("📡 Subscribed to: openauto/door & openclose/door");
    } else {
      Serial.print("❌ Lỗi MQTT: ");
      Serial.println(client.state()); 
      delay(5000);
    }
  }
}
//*****************************************************************************************************************
// 🔹 Đọc cảm biến khoảng cách HC-SR04
int read_distance() {
  if (!auto_door_mode) return 100; // ✅ Nếu auto OFF, giả lập khoảng cách xa để cửa không mở

  digitalWrite(TRIG_PIN, LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG_PIN, LOW);
  long duration = pulseIn(ECHO_PIN, HIGH);
  return duration * 0.034 / 2;
}
//*****************************************************************************************************************
// 🔹 Gửi dữ liệu cảm biến lên MQTT
void send_sensor_data() {
  JsonDocument doc;
  int distance = read_distance();
  int gasValue = analogRead(GAS_SENSOR);
  int motion = digitalRead(PIR_SENSOR);
  int waterDetected = digitalRead(WATER_SENSOR);

  float temp = dht.readTemperature();
  float hum = dht.readHumidity();
  if (isnan(temp) || isnan(hum)) {
    Serial.println("⚠️ Lỗi đọc cảm biến DHT11!");
    return;
  }
  // // 📡 Đọc dữ liệu từ cảm biến khi cảm biến thay đổi dữ liệu
  // static int lastGasValue = -1, lastMotion = -1, lastWater = -1, lastDistance = -1;
  // int distance = read_distance();
  // int gasValue = analogRead(GAS_SENSOR);
  // int motion = digitalRead(PIR_SENSOR);
  // int waterDetected = digitalRead(WATER_SENSOR);

  // if (gasValue == lastGasValue && motion == lastMotion && 
  //     waterDetected == lastWater && distance == lastDistance) {
  //   return; // Không gửi nếu không có thay đổi
  // }

  // lastGasValue = gasValue;
  // lastMotion = motion;
  // lastWater = waterDetected;
  // lastDistance = distance;


  // float temp = dht.readTemperature();
  // float hum = dht.readHumidity();

  // if (isnan(temp) || isnan(hum)) {
  //   Serial.println("⚠️ Lỗi đọc cảm biến DHT11!");
  //   return;
  // }

//*****************************************************************************************************************
  // 🏡 Tạo JSON gửi lên MQTT
  doc["device_id"] = "esp32_smart_home";
  doc["gas"] = gasValue;
  doc["temperature"] = temp;
  doc["humidity"] = hum;
  doc["motion"] = motion;
  doc["water"] = waterDetected;
  doc["distance"] = distance;
  doc["auto_door"] = auto_door_mode ? "on" : "off";
  doc["door_state"] = door_state ? "open" : "closed";

  char buffer[128];
  serializeJson(doc, buffer);

  // Debug MQTT
  Serial.println("\n📤 Đang gửi dữ liệu lên MQTT...");
  Serial.println(buffer);

  bool success = client.publish("smarthome/data", buffer);
  if (success) {
    Serial.println("✅ Dữ liệu đã gửi lên MQTT thành công!");
  } else {
    Serial.println("❌ Lỗi khi gửi dữ liệu lên MQTT!");
  }

  // 🚨 Điều khiển cảnh báo Gas & Nước
  if (gasValue >= 1000 || waterDetected == HIGH) {
    digitalWrite(BUZZER, HIGH);
    Serial.println("🚨 CẢNH BÁO: Phát hiện nguy hiểm!");
  } else {
    digitalWrite(BUZZER, LOW);
  }
}
//*****************************************************************************************************************
// 🔹 Điều khiển cửa thông minh
void auto_door_control(int distance) {
  if (auto_door_mode) {
    if (distance < 10) {
      doorServo.write(90); // Mở cửa
      door_state = true;
    } else {
      doorServo.write(0);  // Đóng cửa
      door_state = false;
    }
  }
}
// void auto_door_control() {
//   if (auto_door_mode) {
//     // 🛑 Đọc cảm biến khoảng cách
//     digitalWrite(TRIG_PIN, LOW);
//     delayMicroseconds(2);
//     digitalWrite(TRIG_PIN, HIGH);
//     delayMicroseconds(10);
//     digitalWrite(TRIG_PIN, LOW);
//     long duration = pulseIn(ECHO_PIN, HIGH);
//     int distance = duration * 0.034 / 2;

//     // 🚪 Mở cửa khi có người đến gần (nếu chế độ tự động đang bật)
//     if (distance < 10) {
//       doorServo.write(90); // Mở cửa
//     } else {
//       doorServo.write(0);  // Đóng cửa
//     }
//   }
// }
//******************************************************************************************************************************************
// 🔹 Callback xử lý tin nhắn từ HiveMQ
void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("📩 Nhận lệnh từ topic: ");
  Serial.println(topic);

  String message = "";
  for (int i = 0; i < length; i++) {
    message += (char)payload[i];
  }
  Serial.println("📜 Nội dung tin nhắn: " + message);
  // 🛠️ Xử lý lệnh từ MQTT
  if (String(topic) == "openauto/door") {
    if (message == "batautocua") {
      auto_door_mode = true;
      Serial.println("🚪 Chế độ cửa tự động đã bật!");
    } else if (message == "tatautocua") {
      auto_door_mode = false;
      Serial.println("🚪 Chế độ cửa tự động đã tắt!");
    }
  } else if (String(topic) == "openclose/door") {
    if (!auto_door_mode) { // Chỉ điều khiển bằng App nếu Auto OFF
      if (message == "mocua") {
        doorServo.write(90); // Mở cửa
        door_state = true;
        Serial.println("🔓 Cửa mở theo lệnh từ app!");
      } else if (message == "dongcua") {
        doorServo.write(0); // Đóng cửa
        door_state = false;
        Serial.println("🔒 Cửa đóng theo lệnh từ app!");
      }
    }
  }
}
//*****************************************************************************************************************
void setup() {
  Serial.begin(115200);
  setup_wifi();

  espClient.setInsecure();  // Bỏ qua kiểm tra SSL (Fix lỗi kết nối MQTT)
  client.setServer(mqtt_server, mqtt_port);
  client.setCallback(callback);

  pinMode(GAS_SENSOR, INPUT);
  pinMode(PIR_SENSOR, INPUT);
  pinMode(WATER_SENSOR, INPUT);
  pinMode(TRIG_PIN, OUTPUT);
  pinMode(ECHO_PIN, INPUT);
  pinMode(BUZZER, OUTPUT);
  
  dht.begin();
  doorServo.attach(SERVO_PIN);
}
//*****************************************************************************************************************
void loop() {
  Serial.println("\n⏳ Chu kỳ gửi dữ liệu...");

  if (WiFi.status() != WL_CONNECTED) {
    Serial.println("⚠️ WiFi mất kết nối! Đang kết nối lại...");
    setup_wifi();
  }

  if (!client.connected()) {
    reconnect_mqtt();
  }

  client.loop();

  int distance = read_distance();
  auto_door_control(distance);
  send_sensor_data();

  delay(5000);
}
