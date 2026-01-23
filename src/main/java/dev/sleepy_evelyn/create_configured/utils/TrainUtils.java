package dev.sleepy_evelyn.create_configured.utils;

import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class TrainUtils {

    public static Optional<Train> getOwnedTrainFromStationPos(Level level, BlockPos pos) {
        return Optional.ofNullable(level.getBlockEntity(pos))
                .filter(StationBlockEntity.class::isInstance)
                .map(StationBlockEntity.class::cast)
                .flatMap(TrainUtils::getOwnedTrainFromStation);
    }

    public static Optional<Train> getOwnedTrainFromStation(StationBlockEntity sbe) {
        return Optional.ofNullable(sbe)
                .map(StationBlockEntity::getStation)
                .map(GlobalStation::getPresentTrain)
                .filter(train -> train.owner != null);
    }
}
