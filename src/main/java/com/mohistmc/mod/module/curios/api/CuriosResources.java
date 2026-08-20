package com.mohistmc.mod.module.curios.api;

import com.mohistmc.mod.MohistMC;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CuriosResources {
    public static Identifier resource(String path) {
        return Identifier.fromNamespaceAndPath(MohistMC.MODID, path);
    }
}
