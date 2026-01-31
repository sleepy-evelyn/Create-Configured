package dev.sleepy_evelyn.create_configured.mixin_interfaces.server;

import dev.sleepy_evelyn.create_configured.trains.TrainDisassemblyLock;
import dev.sleepy_evelyn.create_configured.trains.TrainMotionProfile;

public interface TrainTweaks {

    void cc$setMotionProfile(TrainMotionProfile motionProfile);
    void cc$setLock(TrainDisassemblyLock lock);

    TrainDisassemblyLock cc$getLock();
    TrainMotionProfile cc$getTopSpeed();
    TrainMotionProfile cc$getAcceleration();
    int cc$getFuelMultiplier();
}
