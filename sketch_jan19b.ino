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
#define LIGHT_SENSOR 18
#define FAN_PIN 19
#define LED_LIVING_ROOM 15
#define LED_ROOM_1 14
#define LED_ROOM_2 12
#define LED_ROOM_3 13  


//*****************************************************************************************************************
// 🌟 Biến trạng thái cửa tự động & trạng thái cửa
bool auto_door_mode = false;
bool auto_light_mode = false;
bool auto_fan_mode = false;

bool door_state = false; // false = Đóng, true = Mở
bool living_room_light = false;
bool room_states[3] = {false, false, false};
unsigned long last_motion_time = 0;
int fan_speed = 0;
const int AUTO_OFF_DELAY = 10000; // 10 giây

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
    delay(3000);
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
void callback(char* topic, byte* payload, unsigned int length) {
    String message = "";
    for (int i = 0; i < length; i++) message += (char)payload[i];

    Serial.print("📩 Nhận lệnh từ MQTT: ");
    Serial.println(message);

    // Xử lý chế độ tự động
    if (String(topic) == "openauto/door") {
        if (message == "batautocua") {
            auto_door_mode = true;
            Serial.println("🚪 Chế độ tự động cửa: BẬT");
        } 
        else if (message == "tatautocua") {
            auto_door_mode = false;
            Serial.println("🚪 Chế độ tự động cửa: TẮT");
            
            // 🔴 Đóng cửa ngay khi tắt chế độ tự động, nếu đang mở
            if (door_state) {
                doorServo.write(0);
                door_state = false;
                Serial.println("🔒 Cửa đóng do tắt chế độ tự động!");
                send_sensor_data(); // Gửi trạng thái cửa lên MQTT
            }
        }
        else if (message == "batautoden") auto_light_mode = true;
        else if (message == "tatautoden") auto_light_mode = false;
        else if (message == "batautoquat") auto_fan_mode = true;
        else if (message == "tatautoquat") auto_fan_mode = false;
    }
    // Xử lý cửa, quạt và đèn
    else if (String(topic) == "openclose/door") {
        // Kiểm tra chế độ cửa tự động trước khi điều khiển thủ công
        if (!auto_door_mode) { 
            if (message == "mocua" && !door_state) {
                doorServo.write(90);
                door_state = true;
                Serial.println("🔓 Cửa mở theo lệnh từ app!");
            } 
            else if (message == "dongcua" && door_state) {
                doorServo.write(0);
                door_state = false;
                Serial.println("🔒 Cửa đóng theo lệnh từ app!");
            }
            send_sensor_data(); // Gửi trạng thái cửa về MQTT
        }

        // Điều khiển quạt
        if (message.startsWith("batquat")) {
            int speed = message.substring(8).toInt();
            fan_speed = constrain(speed, 0, 255); // Giới hạn tốc độ quạt từ 0-255
            analogWrite(FAN_PIN, fan_speed);
            Serial.print("🌀 Quạt bật với tốc độ: ");
            Serial.println(fan_speed);
        } 
        else if (message == "tatquat") {
            fan_speed = 0;
            analogWrite(FAN_PIN, 0);
            Serial.println("🌀 Quạt đã tắt!");
        } 

        // Điều khiển đèn
        if (message == "batden1") digitalWrite(LED_LIVING_ROOM, HIGH);
        else if (message == "tatden1") digitalWrite(LED_LIVING_ROOM, LOW);
        else if (message == "batden2") digitalWrite(LED_ROOM_1, HIGH);
        else if (message == "tatden2") digitalWrite(LED_ROOM_1, LOW);
        else if (message == "batden3") digitalWrite(LED_ROOM_2, HIGH);
        else if (message == "tatden3") digitalWrite(LED_ROOM_2, LOW);
        else if (message == "batden4") digitalWrite(LED_ROOM_3, HIGH);
        else if (message == "tatden4") digitalWrite(LED_ROOM_3, LOW);
    }
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
        Serial.println("⚠️ Lỗi cảm biến DHT11, không gửi dữ liệu!");
        return;
    }

    // 🏡 Tạo JSON gửi lên MQTT

    doc["gas"] = gasValue;
    doc["temperature"] = temp;
    doc["humidity"] = hum;
    doc["motion"] = motion;
    doc["water"] = waterDetected;
    doc["distance"] = distance;
    doc["auto_door"] = auto_door_mode ? "on" : "off";
    doc["door_state"] = door_state ? "open" : "closed";

    // 🔹 Trạng thái đèn
    doc["light_living_room"] = digitalRead(LED_LIVING_ROOM) == HIGH ? "on" : "off";
    doc["light_living_room_state"] = digitalRead(LED_LIVING_ROOM) == HIGH ? "on" : "off";
    doc["light_room_1"] = digitalRead(LED_ROOM_1) == HIGH ? "on" : "off";
    doc["light_room_2"] = digitalRead(LED_ROOM_2) == HIGH ? "on" : "off";
    doc["light_room_3"] = digitalRead(LED_ROOM_3) == HIGH ? "on" : "off";

    // 🔹 Trạng thái quạt
    doc["fan_state"] = fan_speed > 0 ? "on" : "off";
    doc["fan_speed"] = fan_speed; // Gửi cả tốc độ quạt

    // Đo kích thước JSON
    size_t jsonSize = measureJson(doc);
    Serial.print("📏 Kích thước JSON: ");
    Serial.println(jsonSize);

  
    char buffer[512]; 
    serializeJson(doc, buffer);

    // Kiểm tra MQTT trước khi gửi
    if (!client.connected()) {
        Serial.println("⚠️ MQTT mất kết nối! Đang kết nối lại...");
        reconnect_mqtt();
    }

    // Debug MQTT
    Serial.println("\n📤 Đang gửi dữ liệu lên MQTT...");
    Serial.println(buffer);

    bool success = client.publish("smarthome/data", buffer);
    if (success) {
        Serial.println("✅ Dữ liệu đã gửi lên MQTT thành công!");
    } else {
        Serial.println("❌ Lỗi khi gửi dữ liệu lên MQTT! Kiểm tra lại kết nối.");
    }

    // 🚨 Cảnh báo còi nếu phát hiện khí gas quá cao
    if (gasValue >= 2000 || waterDetected == HIGH) {
        for (int i = 0; i < 5; i++) {
            digitalWrite(BUZZER, HIGH);
            delay(300);
            digitalWrite(BUZZER, LOW);
            delay(300);
        }
        Serial.println("🚨 CẢNH BÁO: MỨC KHÍ GAS CAO!");
    }
}
//*****************************************************************************************************************
// 🔹 Điều khiển cửa auto
void auto_door_control(int distance) {
    if (auto_door_mode) {
        if (distance < 10) {
            doorServo.write(90);
            door_state = true;
        } else {
            doorServo.write(0);
            door_state = false;
        }
    }
}
//*****************************************************************************************************************
void handle_living_room_light() {
    if (auto_light_mode) {
        int motion = digitalRead(PIR_SENSOR);
        int light_level = digitalRead(LIGHT_SENSOR);

        if (motion == HIGH && light_level == LOW) {
            digitalWrite(LED_LIVING_ROOM, HIGH);
            last_motion_time = millis();
        } else if (millis() - last_motion_time > AUTO_OFF_DELAY) {
            digitalWrite(LED_LIVING_ROOM, LOW);
        }
    }
}
//*****************************************************************************************************************
void handle_fan() {
    if (auto_fan_mode) {
        float temp = dht.readTemperature();
        if (temp > 30) fan_speed = 255;
        else if (temp > 25) fan_speed = 180;
        else if (temp > 21.4) fan_speed = 100;
        else fan_speed = 0;

        analogWrite(FAN_PIN, fan_speed);
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
void setup() {
  Serial.begin(115200);
  setup_wifi();

  espClient.setInsecure();  // Bỏ qua kiểm tra SSL (Fix lỗi kết nối MQTT)
  client.setServer(mqtt_server, mqtt_port);
  client.setCallback(callback);
  client.setBufferSize(1024); // Tăng bộ đệm MQTT

  pinMode(LED_LIVING_ROOM, OUTPUT);
  pinMode(LED_ROOM_1, OUTPUT);
  pinMode(LED_ROOM_2, OUTPUT);
  pinMode(LED_ROOM_3, OUTPUT);
  pinMode(FAN_PIN, OUTPUT);

  pinMode(GAS_SENSOR, INPUT);
  pinMode(PIR_SENSOR, INPUT);
  pinMode(WATER_SENSOR, INPUT);
  pinMode(TRIG_PIN, OUTPUT);
  pinMode(ECHO_PIN, INPUT);
  pinMode(BUZZER, OUTPUT);

  digitalWrite(LED_LIVING_ROOM, LOW);
  digitalWrite(LED_ROOM_1, LOW);
  digitalWrite(LED_ROOM_2, LOW);
  digitalWrite(LED_ROOM_3, LOW);
  
  dht.begin();
  doorServo.attach(SERVO_PIN);
}
//*****************************************************************************************************************
void loop() {
    static unsigned long last_sensor_update = 0;
    unsigned long now = millis();

    // Đảm bảo MQTT luôn chạy ngay lập tức
    if (WiFi.status() != WL_CONNECTED) setup_wifi();
    if (!client.connected()) reconnect_mqtt();
    client.loop(); // Kiểm tra MQTT liên tục để nhận lệnh ngay lập tức

    // 🟢 Đọc khoảng cách liên tục và điều khiển cửa ngay lập tức
    int distance = read_distance();
    auto_door_control(distance);

    // 🔵 Chỉ gửi dữ liệu lên MQTT mỗi 5 giây
    if (now - last_sensor_update >= 5000) {
        last_sensor_update = now;
        send_sensor_data();
        handle_living_room_light();
        handle_fan();
    }
}