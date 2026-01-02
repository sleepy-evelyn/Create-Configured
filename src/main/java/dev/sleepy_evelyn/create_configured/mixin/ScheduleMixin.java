package dev.sleepy_evelyn.create_configured.mixin;

import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.ScheduleEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Schedule.class)
public class ScheduleMixin {

    @Inject(method = "<init>(Ljava/util/List;ZI)V", at = @At("RETURN"))
    private void Schedule(List<ScheduleEntry> entries, boolean cyclic, int savedProgress, CallbackInfo ci) {

    }
}
