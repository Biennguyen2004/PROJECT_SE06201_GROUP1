package com.user_manager_v1.config;

import com.user_manager_v1.websocket.SensorDoorWebSocketHandler;
import com.user_manager_v1.websocket.SensorGasWebSocketHandler;
import com.user_manager_v1.websocket.SensorClimateWebSocketHandler;
import com.user_manager_v1.websocket.SensorLightWebSocketHandler;
import com.user_manager_v1.websocket.SensorMotionWebSocketHandler;
import com.user_manager_v1.websocket.SensorRelayWebSocketHandler;
import com.user_manager_v1.websocket.SensorWaterWebSocketHandler;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final SensorGasWebSocketHandler sensorGasWebSocketHandler;
    private final SensorClimateWebSocketHandler sensorClimateWebSocketHandler;
    private final SensorDoorWebSocketHandler sensorDoorWebSocketHandler;
    private final SensorLightWebSocketHandler sensorLightWebSocketHandler;
    private final SensorMotionWebSocketHandler sensorMotionWebSocketHandler;
    private final SensorRelayWebSocketHandler sensorRelayWebSocketHandler;
    private final SensorWaterWebSocketHandler sensorWaterWebSocketHandler;

    public WebSocketConfig(SensorGasWebSocketHandler sensorGasWebSocketHandler, SensorClimateWebSocketHandler sensorClimateWebSocketHandler,SensorDoorWebSocketHandler sensorDoorWebSocketHandler,
                           SensorLightWebSocketHandler sensorLightWebSocketHandler,
                           SensorMotionWebSocketHandler sensorMotionWebSocketHandler,
                           SensorRelayWebSocketHandler sensorRelayWebSocketHandler,
                           SensorWaterWebSocketHandler sensorWaterWebSocketHandler) {
        this.sensorGasWebSocketHandler = sensorGasWebSocketHandler;
        this.sensorClimateWebSocketHandler = sensorClimateWebSocketHandler;
        this.sensorDoorWebSocketHandler = sensorDoorWebSocketHandler;
        this.sensorLightWebSocketHandler = sensorLightWebSocketHandler;
        this.sensorMotionWebSocketHandler = sensorMotionWebSocketHandler;
        this.sensorRelayWebSocketHandler = sensorRelayWebSocketHandler;
        this.sensorWaterWebSocketHandler = sensorWaterWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(sensorGasWebSocketHandler, "/ws/sensor/gas").setAllowedOrigins("*");
        registry.addHandler(sensorClimateWebSocketHandler, "/ws/sensor/climate").setAllowedOrigins("*");
        registry.addHandler(sensorDoorWebSocketHandler, "/ws/sensor/door").setAllowedOrigins("*");
        registry.addHandler(sensorLightWebSocketHandler, "/ws/sensor/light").setAllowedOrigins("*");
        registry.addHandler(sensorMotionWebSocketHandler, "/ws/sensor/motion").setAllowedOrigins("*");
        registry.addHandler(sensorRelayWebSocketHandler, "/ws/sensor/relay").setAllowedOrigins("*");
        registry.addHandler(sensorWaterWebSocketHandler, "/ws/sensor/water").setAllowedOrigins("*");
    }
}
