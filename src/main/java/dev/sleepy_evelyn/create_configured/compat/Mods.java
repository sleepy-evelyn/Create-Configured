package dev.sleepy_evelyn.create_configured.compat;

/*
 * MIT License
 *
 * Copyright (c) 2019 simibubi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 *         to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
*/

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.LoadingModList;

import java.util.Locale;
import java.util.function.Supplier;

public enum Mods {
    OPENPARTIESANDCLAIMS("Open Parties & Claims");

    private final String id;
    private final String modName;

    Mods(String modName) {
        id = name().toLowerCase(Locale.ENGLISH);
        this.modName = modName;
    }

    public String id() {
        return id;
    }

    public String modName() { return modName; }

    public ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(id, path);
    }

    public Boolean isLoaded() {
        return LoadingModList.get().getModFileById(id) != null;
    }

    public void executeIfInstalled(Supplier<Runnable> toExecute) {
        if (isLoaded()) toExecute.get().run();
    }
}
