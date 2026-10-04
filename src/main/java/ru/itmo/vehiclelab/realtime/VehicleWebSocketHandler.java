package ru.itmo.vehiclelab.realtime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.itmo.vehiclelab.service.VehicleChangedEvent;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class VehicleWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final ObjectMapper objectMapper;

    public VehicleWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    public void broadcast(VehicleChangedEvent event) {
        TextMessage message = new TextMessage(toJson(event));
        sessions.removeIf(session -> !send(session, message));
    }

    private boolean send(WebSocketSession session, TextMessage message) {
        if (!session.isOpen()) {
            return false;
        }
        try {
            synchronized (session) {
                session.sendMessage(message);
            }
            return true;
        } catch (IOException exception) {
            return false;
        }
    }

    private String toJson(VehicleChangedEvent event) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "operation", event.operation(),
                    "vehicleId", event.vehicleId() == null ? "" : event.vehicleId(),
                    "occurredAt", Instant.now().toString()
            ));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Не удалось сформировать событие WebSocket", exception);
        }
    }
}
