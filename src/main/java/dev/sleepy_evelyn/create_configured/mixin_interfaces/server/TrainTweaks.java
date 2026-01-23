package dev.sleepy_evelyn.create_configured.mixin_interfaces.server;

import dev.sleepy_evelyn.create_configured.TrainDisassemblyLock;
import dev.sleepy_evelyn.create_configured.TrainMotionProfile;

public interface TrainTweaks {

    void cc$setMotionProfile(TrainMotionProfile topSpeed, TrainMotionProfile acceleration);
    void cc$setLock(TrainDisassemblyLock lock);

    TrainDisassemblyLock cc$getLock();
    TrainMotionProfile cc$getTopSpeed();
    TrainMotionProfile cc$getAcceleration();
}
