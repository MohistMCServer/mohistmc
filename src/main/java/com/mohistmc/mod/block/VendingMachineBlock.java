package com.mohistmc.mod.block;

import com.mohistmc.mod.module.shop.common.ShopSession;
import com.mohistmc.mod.module.shop.common.attachment.PlayerBalance;
import com.mohistmc.mod.module.shop.common.network.payload.OpenShopPayload;
import com.mohistmc.mod.network.NetworkHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 自动售货机：右键打开系统商店
 * <p>服务端发 OpenShopPayload（附余额）由客户端开屏；客户端分支不本地开屏（防单机双开），
 * 单机（集成服）与联机走同一发包路径。
 *
 * @author Mgazul
 * @date 2025/12/3 23:49
 */
public class VendingMachineBlock extends BaseBlock {

    public VendingMachineBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    protected VoxelShape getShape(BlockState p_48760_, BlockGetter p_48761_, BlockPos p_48762_, CollisionContext p_48763_) {
        return makeShape();
    }

    public VoxelShape makeShape(){
        return Shapes.box(0, 0, 0, 1, 2, 1);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide()) {
            if (player instanceof ServerPlayer serverPlayer) {
                // 服务端登记购买会话（记录售货机位置，购买时校验），再通知客户端开屏
                ShopSession.open(serverPlayer, pos);
                NetworkHandler.sendToClientPlayer(new OpenShopPayload(PlayerBalance.get(serverPlayer)), serverPlayer);
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }
}
