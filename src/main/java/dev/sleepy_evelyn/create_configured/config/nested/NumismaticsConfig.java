package dev.sleepy_evelyn.create_configured.config.nested;

import dev.ithundxr.createnumismatics.content.backend.Coin;
import net.createmod.catnip.config.ConfigBase;
import org.jetbrains.annotations.NotNull;

public class NumismaticsConfig extends ConfigBase {

    public final ConfigInt ubiAmount = i(10, 0, "universalBasicIncome",
            "Number of Coins to be paid weekly to every active Player.",
            "This is a form of Universal Basic Income (UBI). Set to 0 to disable",
            "(Ignored in Singleplayer)");

    public final ConfigEnum<Coin> ubiCoinDenomination = e(Coin.COG, "UBICoinDenomination",
            "The denomination of coin used for Universal Basic Income.",
            "(Ignored in Singleplayer)");

    public final ConfigInt playerActiveDuration = i(30, 0, 240,   "playerActiveDuration",
            "[In Minutes]",
            "Total amount of time a Player must spend on the Server each week to be considered active. Ignores time spent AFK.",
            "(Ignored in Singleplayer)");

    @Override
    public @NotNull String getName() {
        return "numismatics";
    }
}
