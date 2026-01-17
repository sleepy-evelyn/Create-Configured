package dev.sleepy_evelyn.create_configured.mixin_interfaces.server;

import dev.sleepy_evelyn.create_configured.TrainDisassemblyLock;

public interface DisassemblyLockable {
    TrainDisassemblyLock cc$getLock();
    void cc$setLock(TrainDisassemblyLock lock);
}
