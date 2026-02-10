package dev.sleepy_evelyn.create_configured.utils;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.*;

public class TrainNameGenerator {

    public static final TrainNameGenerator INSTANCE = new TrainNameGenerator();

    private static final String[][] threeNumberTrains = {
            {"Steaming", "Billy", ""},
            {"Flying", "Dutchman", "Scotsman"},
            {"Prideful", "Penguin", "Pelican"},
            {}
    };

    private final List<NamesCollection> genericNames;
    private final Map<String, List<NamesCollection>> namesByLangLocale;

    private TrainNameGenerator() {
        genericNames = new ArrayList<>();
        namesByLangLocale = new HashMap<>();
    }

    @OnlyIn(Dist.CLIENT)
    public String getRandomTrainName() {
        Objects.requireNonNull(Minecraft.getInstance().player, 
                "Cannot generate random train name: client player is null");

        var client = Minecraft.getInstance();
        var username = client.player.getGameProfile().getName();
        var langLocale = client.getLanguageManager().getSelected();
        var random = new Random();

        List<NamesCollection> trainNameCollections = random.nextBoolean()
                ? namesByLangLocale.getOrDefault(langLocale, genericNames) : genericNames;
        var namesCollection = trainNameCollections.get(random.nextInt(trainNameCollections.size()));
        String[][] trainNames = namesCollection.trainNames;
        String[] nameParts = trainNames[random.nextInt(trainNames.length)];
        String id = namesCollection.numberFormat.getRandomId(random);


    }

    private record NamesCollection(IdFormat numberFormat, String[][] trainNames) {

        private NamesCollection {
            if (trainNames.length == 0)
                throw new IllegalArgumentException("Train names collection contained no name options");
        }

        private String getRandomName() {
            Objects.requireNonNull(Minecraft.getInstance().player,
                    "Cannot generate random train name: client player is null");

            var client = Minecraft.getInstance();
            String username = client.player.getGameProfile().getName();
            String langLocale = client.getLanguageManager().getSelected();

            var random = new Random();
            String id = numberFormat.getRandomId(random);
            String[] nameParts = trainNames[random.nextInt(trainNames.length)];
            StringBuilder nameBuilder = new StringBuilder(nameParts[0]);

            if (nameParts.length > 1)
                nameBuilder.append(nameParts[random.nextInt(nameParts.length - 1) + 1]);

            return String.join(" ", username, id, nameBuilder.toString());
        }
    }

    private static class IdFormat {

        private final int bound, numDigits;
        private final boolean zeroPadded;
        private final char[] letterPrefixes;

        private IdFormat(int numDigits, boolean zeroPadded) {
            this(numDigits, zeroPadded, new char[0]);
        }

        private IdFormat(int numDigits, boolean zeroPadded, char[] letterPrefixes) {
            this.numDigits = numDigits;
            this.bound = computeBound(numDigits);
            this.zeroPadded = zeroPadded;
            this.letterPrefixes = letterPrefixes;
        }

        private static int computeBound(int digits) {
            if (digits < 1 || digits > 8)
                throw new IllegalArgumentException("Number of digits for Train ID Format must be between 1 > 8");
            return (int) Math.pow(10, digits);
        }

        private String getRandomId(Random random) {
            char letterPrefix = letterPrefixes[random.nextInt(letterPrefixes.length)];
            int randomIdx = random.nextInt(bound);
            String number = zeroPadded ?
                    String.format("%0" + numDigits + "d", randomIdx) : Integer.toString(randomIdx);

            return letterPrefix + number;
        }
    }
}
