package dev.sleepy_evelyn.create_configured.gui;

import com.simibubi.create.content.trains.station.StationScreen;
import dev.sleepy_evelyn.create_configured.CreateConfiguredClient;
import dev.sleepy_evelyn.create_configured.TrainDisassemblyLock;
import dev.sleepy_evelyn.create_configured.mixin_interfaces.client.DisassemblyLockSynced;
import dev.sleepy_evelyn.create_configured.network.s2c.StationScreenSyncPayload;
import dev.sleepy_evelyn.create_configured.utils.ScreenHelper;

public class StationScreenSynced {

    private final boolean canPlayerDisassemble;
    private TrainDisassemblyLock lock;

    public StationScreenSynced() {
        canPlayerDisassemble = true;
        lock = TrainDisassemblyLock.NOT_LOCKED;
    }

    public StationScreenSynced(boolean canPlayerDisassemble, TrainDisassemblyLock lock) {
        this.canPlayerDisassemble = canPlayerDisassemble;
        this.lock = lock;
    }

    public void cycleLock() {
        boolean hasGroupProvider = !CreateConfiguredClient.groupsProviderId.contains("none");

        lock = switch(lock) {
            case NOT_LOCKED -> hasGroupProvider ? TrainDisassemblyLock.PARTY_MEMBERS : TrainDisassemblyLock.LOCKED;
            case PARTY_MEMBERS -> TrainDisassemblyLock.LOCKED;
            case LOCKED -> TrainDisassemblyLock.NOT_LOCKED;
        };
    }

    public boolean canPlayerDisassemble() { return canPlayerDisassemble; }
    public TrainDisassemblyLock getLock() { return lock; }

    public static void syncScreen(StationScreenSyncPayload payload) {
        ScreenHelper.getIfInstance(StationScreen.class).ifPresent(stationScreen ->
                ((DisassemblyLockSynced) stationScreen).cc$onSyncDisassemblyLock(
                        payload.canPlayerDisassemble(),
                        payload.lock()));
    }
}
