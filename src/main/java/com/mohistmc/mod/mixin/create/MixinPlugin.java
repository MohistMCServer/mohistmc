package com.mohistmc.mod.mixin.create;

import com.mohistmc.mod.module.create.Create;
import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class MixinPlugin implements IMixinConfigPlugin {
    private List<String> mixins;

    @Override
    public void onLoad(String mixinPackage) {
        Create.Lazy = false;
        mixins = List.of(
                "SavedDataStorageMixin",
                "IngredientMixin",
                "CuboidModelElementDeserializerMixin",
                "CuboidModelElementMixin",
                "UnbakedCuboidGeometryMixin",
                "BakedQuadMixin",
                "VertexConsumerMixin",
                "ModelBlockRendererMixin",
                "BlockModelLighterMixin",
                "TerrainParticleMixin"
        );
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return mixins;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
