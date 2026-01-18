package dev.sleepy_evelyn.create_configured.gui;

import com.simibubi.create.content.trains.station.StationScreen;
import dev.sleepy_evelyn.create_configured.CreateConfiguredClient;
import dev.sleepy_evelyn.create_configured.TrainDisassemblyLock;
import dev.sleepy_evelyn.create_configured.TrainSpeed;
import dev.sleepy_evelyn.create_configured.mixin_interfaces.client.DisassemblyLockSynced;
import dev.sleepy_evelyn.create_configured.network.s2c.StationScreenSyncPayload;
import dev.sleepy_evelyn.create_configured.utils.ScreenUtils;

public class StationScreenSynced {

    private final boolean canPlayerDisassemble;
    private TrainDisassemblyLock lock;
    private TrainSpeed trainSpeed;

    public StationScreenSynced() {
        canPlayerDisassemble = true;
        lock = TrainDisassemblyLock.NOT_LOCKED;
        trainSpeed = TrainSpeed.DEFAULT;
    }

    public StationScreenSynced(boolean canPlayerDisassemble, TrainDisassemblyLock lock) {
        this.canPlayerDisassemble = canPlayerDisassemble;
        this.lock = lock;
        this.trainSpeed = TrainSpeed.DEFAULT; // Placeholder
    }

    public void cycleLock() {
        boolean hasGroupProvider = !CreateConfiguredClient.groupsProviderId.contains("none");

        lock = switch(lock) {
            case NOT_LOCKED -> hasGroupProvider ? TrainDisassemblyLock.PARTY_MEMBERS : TrainDisassemblyLock.LOCKED;
            case PARTY_MEMBERS -> TrainDisassemblyLock.LOCKED;
            case LOCKED -> TrainDisassemblyLock.NOT_LOCKED;
        };
    }

    public void cycleTrainSpeed() {
        trainSpeed = switch(trainSpeed) {
            case FAST -> TrainSpeed.SLOW;
            case DEFAULT -> TrainSpeed.FAST;
            case SLOW -> TrainSpeed.DEFAULT;
        };
    }

    public boolean canPlayerDisassemble() { return canPlayerDisassemble; }
    public TrainDisassemblyLock getLock() { return lock; }
    public TrainSpeed getTrainSpeed() { return trainSpeed; }

    public static void syncScreen(StationScreenSyncPayload payload) {
        ScreenUtils.getIfInstance(StationScreen.class).ifPresent(stationScreen ->
                ((DisassemblyLockSynced) stationScreen).cc$onSyncDisassemblyLock(
                        payload.canPlayerDisassemble(),
                        payload.lock()));
    }
}
