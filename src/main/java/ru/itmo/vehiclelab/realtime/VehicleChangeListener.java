package ru.itmo.vehiclelab.realtime;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.itmo.vehiclelab.service.VehicleChangedEvent;

@Component
public class VehicleChangeListener {

    private final VehicleWebSocketHandler handler;

    public VehicleChangeListener(VehicleWebSocketHandler handler) {
        this.handler = handler;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onVehicleChanged(VehicleChangedEvent event) {
        handler.broadcast(event);
    }
}
