package dev.sleepy_evelyn.create_configured.mixin_interfaces.client;

import dev.sleepy_evelyn.create_configured.TrainDisassemblyLock;

public interface DisassemblyLockSynced {
    void cc$onSyncDisassemblyLock(boolean canPlayerDisassemble, TrainDisassemblyLock lock);
}
