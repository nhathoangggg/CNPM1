//#include <Arduino.h>
#include <WiFi.h>
#include <Firebase_ESP_Client.h>
#include <DHTesp.h>

#define DHTPIN 26     
#define DHTTYPE DHT11

//Provide the token generation process info.
#include "addons/TokenHelper.h"
//Provide the RTDB payload printing info and other helper functions.
#include "addons/RTDBHelper.h"

// Insert your RTDB URL
#define DATABASE_URL "https://my1stproject-90f1d-default-rtdb.asia-southeast1.firebasedatabase.app/"    
// Insert Firebase Database secret
#define API_KEY "AIzaSyA1nEnctqYTCbBIuVKwtR-tGKQeGJFpGp8"
// Insert your network credentials
#define WIFI_SSID "hoang123"                        
#define WIFI_PASSWORD "hoangvodich"  

//Declares Variables
FirebaseData fbdo;

FirebaseAuth auth;
FirebaseConfig config;
bool signupOK = false;
unsigned long sendDataPrevMillis = 0;
unsigned long sensorReadInterval = 2000; // Interval for reading sensors (2 seconds)
unsigned long firebaseUpdateInterval = 2000;
int fireStatus;       // led status received from firebase
int led = 5;  // Number of GPIO that is connected to LED
int earth = 36;  // vp ,+vin
DHTesp dht;
String path = "led/"; // path to LED on firebase Database 
String path1 = "Earth/";
String path2 = "Humi/";
String path3 = "Temp/";
void setup() 
{
  Serial.begin(9600);
  delay(2000);    
  pinMode(led, OUTPUT); 
  pinMode(earth, INPUT_PULLUP);
  dht.setup(DHTPIN, DHTesp::DHTTYPE);
  digitalWrite(led, HIGH);     // make external led OFF
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);            
  Serial.print("Connecting to ");
  Serial.print(WIFI_SSID);
  while (WiFi.status() != WL_CONNECTED) 
  {
    Serial.print(".");
    delay(500);
  }

  Serial.println();
  Serial.print("Connected to ");
  Serial.println(WIFI_SSID);
  config.api_key = API_KEY;

  /* Assign the RTDB URL (required) */
  config.database_url = DATABASE_URL;
  
  /* Sign up */
  if (Firebase.signUp(&config, &auth, "", "")){
    Serial.println("ok");
    signupOK = true;
  }
  else{
    Serial.printf("%s\n", config.signer.signupError.message.c_str());
  }

  /* Assign the callback function for the long running token generation task */
  config.token_status_callback = tokenStatusCallback; //see addons/TokenHelper.h

  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);
}
 
void loop() 
{
  static unsigned long lastSensorReadTime = 0;
  static unsigned long lastFirebaseUpdateTime = 0;
  if (millis() - lastSensorReadTime > sensorReadInterval) {
    lastSensorReadTime = millis();
    int t = dht.getTemperature();
    int h = dht.getHumidity();
    if(t>200){
      t=200;
      h=200;
      Serial.println("Lỗi đọc dữ liệu dht11");
    }
    else{
      Serial.print("Nhiệt độ: ");
      Serial.print(t);
      Serial.println("độ C");
      Serial.print("Độ ẩm: ");
      Serial.print(h);
      Serial.println("%");
    }
    
    int valueAm = analogRead(earth);
    int percent = (100-((valueAm/4095.00)*100));
    if(isnan(valueAm)){
      percent=200;
      Serial.println("Lỗi đọc cảm biến độ ẩm đất.");
    }
    else{
      Serial.print("Do am: ");
      Serial.print(percent);
      Serial.println("%");
    }
    
    if (Firebase.ready() && signupOK && (millis() - lastFirebaseUpdateTime > firebaseUpdateInterval || lastFirebaseUpdateTime == 0)) {
      lastFirebaseUpdateTime = millis();
      if(Firebase.RTDB.setInt(&fbdo,path1,percent)){
        Serial.println("Đã gửi giá trị độ ẩm đất lên.");
      }
      else{
        Serial.println("Lỗi ghi dữ liệu độ ẩm đất.");
        Serial.println(fbdo.errorReason());
      }
      if(Firebase.RTDB.setInt(&fbdo,path3,t)){
        Serial.println("Đã gửi giá trị nhiệt độ lên.");
      }
      else{
        Serial.println("Lỗi ghi dữ liệu nhiệt độ.");
        Serial.println(fbdo.errorReason());
      }
      if(Firebase.RTDB.setInt(&fbdo,path2,h)){
        Serial.println("Đã gửi giá trị độ ẩm lên.");
      }
      else{
        Serial.println("Lỗi ghi dữ liệu độ ẩm.");
        Serial.println(fbdo.errorReason());
      }
      // Write an Int number on the database path test/int
      if (Firebase.RTDB.getInt(&fbdo, path)) {
        int fireStatus = fbdo.intData(); // Lấy giá trị trạng thái
        Serial.print("Trạng thái LED từ Firebase: ");
        Serial.println(fireStatus);

        // Thay đổi trạng thái LED dựa trên Firebase
        if (fireStatus == 0) {
          digitalWrite(led, LOW); // Bật LED
          Serial.println("LED tắt");
        } else if (fireStatus == 1) {
          digitalWrite(led, HIGH); // Tắt LED
          Serial.println("LED bật");
        }
      } else {
        Serial.print("Lỗi đọc dữ liệu: ");
        Serial.println(fbdo.errorReason());
      }
    }
  }
}