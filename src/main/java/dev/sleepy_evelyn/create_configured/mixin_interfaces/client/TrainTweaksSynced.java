package dev.sleepy_evelyn.create_configured.mixin_interfaces.client;

import dev.sleepy_evelyn.create_configured.trains.TrainDisassemblyLock;
import dev.sleepy_evelyn.create_configured.trains.TrainMotionProfile;
import dev.sleepy_evelyn.create_configured.permissions.TrainTweakPermissions;

public interface TrainTweaksSynced {
    void cc$onSync(TrainTweakPermissions permissions, TrainDisassemblyLock lock, TrainMotionProfile topSpeed, TrainMotionProfile acceleration);
}
