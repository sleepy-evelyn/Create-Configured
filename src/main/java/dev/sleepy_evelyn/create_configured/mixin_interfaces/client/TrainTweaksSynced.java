package dev.sleepy_evelyn.create_configured.mixin_interfaces.client;

import dev.sleepy_evelyn.create_configured.TrainDisassemblyLock;
import dev.sleepy_evelyn.create_configured.TrainMotionProfile;
import dev.sleepy_evelyn.create_configured.permissions.TrainTweakPermissions;

public interface TrainTweaksSynced {
    void cc$onSync(TrainTweakPermissions permissions, TrainDisassemblyLock lock, TrainMotionProfile topSpeed, TrainMotionProfile acceleration);
}
