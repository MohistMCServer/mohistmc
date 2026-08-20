package com.mohistmc.mod.module.create.client.content.logistics.box;

import com.mohistmc.mod.module.create.AllItems;
import com.mohistmc.mod.module.create.client.AllPartialModels;
import com.mohistmc.mod.module.create.content.logistics.box.PackageEntity;
import com.mohistmc.mod.module.create.content.logistics.box.PackageItem;
import com.mohistmc.mod.module.flywheel.api.visualization.VisualizationContext;
import com.mohistmc.mod.module.flywheel.lib.instance.InstanceTypes;
import com.mohistmc.mod.module.flywheel.lib.instance.TransformedInstance;
import com.mohistmc.mod.module.flywheel.lib.model.Models;
import com.mohistmc.mod.module.flywheel.lib.model.baked.PartialModel;
import com.mohistmc.mod.module.flywheel.lib.visual.AbstractEntityVisual;
import com.mohistmc.mod.module.flywheel.lib.visual.SimpleDynamicVisual;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class PackageVisual extends AbstractEntityVisual<PackageEntity> implements SimpleDynamicVisual {
    public final TransformedInstance instance;

    public PackageVisual(VisualizationContext ctx, PackageEntity entity, float partialTick) {
        super(ctx, entity, partialTick);

        ItemStack box = entity.box;
        if (box.isEmpty() || !PackageItem.isPackage(box)) {
            box = AllItems.CARDBOARD_BLOCK.getDefaultInstance();
        }
        PartialModel model = AllPartialModels.PACKAGES.get(BuiltInRegistries.ITEM.getKey(box.getItem()));

        instance = instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(model)).createInstance();

        animate(partialTick);
    }

    @Override
    public void beginFrame(Context ctx) {
        animate(ctx.partialTick());
    }

    private void animate(float partialTick) {
        float yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());

        Vec3 pos = entity.position();
        var renderOrigin = renderOrigin();
        var x = (float) (Mth.lerp(partialTick, entity.xo, pos.x) - renderOrigin.getX());
        var y = (float) (Mth.lerp(partialTick, entity.yo, pos.y) - renderOrigin.getY());
        var z = (float) (Mth.lerp(partialTick, entity.zo, pos.z) - renderOrigin.getZ());

        long randomBits = entity.getId() * 31L * 493286711L;
        randomBits = randomBits * randomBits * 4392167121L + randomBits * 98761L;
        float xNudge = (((randomBits >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float yNudge = (((randomBits >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float zNudge = (((randomBits >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;

        instance.setIdentityTransform().translate(x - 0.5 + xNudge, y + yNudge, z - 0.5 + zNudge)
            .rotateYCenteredDegrees(-yaw - 90).light(computePackedLight(partialTick)).setChanged();
    }

    @Override
    protected void _delete() {
        instance.delete();
    }
}
