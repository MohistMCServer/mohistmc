package com.mohistmc.mod.register;

import com.mohistmc.mod.MohistMC;
import com.mohistmc.mod.block.BaseBlock;
import com.mohistmc.mod.block.CaoYaoBlock;
import com.mohistmc.mod.block.TestBlock;
import com.mohistmc.mod.block.VendingMachineBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * @author Mgazul
 * @date 2026/4/18 15:22
 */
public class BlockRegister {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MohistMC.MODID);

    public static final DeferredBlock<Block> TEST_BLOCK = BLOCKS.registerBlock("test_block", TestBlock::new, BlockBehaviour.Properties::of);
    public static final DeferredBlock<Block> VENDING_MACHINE_BLOCK = BLOCKS.registerBlock("vending_machine", VendingMachineBlock::new, BlockBehaviour.Properties::of);

    public static final DeferredBlock<Block> DI_HUANG = BLOCKS.registerBlock("di_huang", CaoYaoBlock::new, () -> BlockBehaviour.Properties.of().noCollision().offsetType(BlockBehaviour.OffsetType.XZ));
    public static final DeferredBlock<Block> SHA_SHEN = BLOCKS.registerBlock("sha_shen", CaoYaoBlock::new, () -> BlockBehaviour.Properties.of().noCollision().offsetType(BlockBehaviour.OffsetType.XZ));
    public static final DeferredBlock<Block> GAN_CAO = BLOCKS.registerBlock("gan_cao", CaoYaoBlock::new, () -> BlockBehaviour.Properties.of().noCollision().offsetType(BlockBehaviour.OffsetType.XZ));
    public static final DeferredBlock<Block> REN_SHEN = BLOCKS.registerBlock("ren_shen", CaoYaoBlock::new, () -> BlockBehaviour.Properties.of().noCollision().offsetType(BlockBehaviour.OffsetType.XZ));
    public static final DeferredBlock<Block> JI_GUAN = BLOCKS.registerBlock("ji_guan", CaoYaoBlock::new, () -> BlockBehaviour.Properties.of().noCollision().offsetType(BlockBehaviour.OffsetType.XZ));
    public static final DeferredBlock<Block> JIE_GENG = BLOCKS.registerBlock("jie_geng", CaoYaoBlock::new, () -> BlockBehaviour.Properties.of().noCollision().offsetType(BlockBehaviour.OffsetType.XZ));
    public static final DeferredBlock<Block> HUANG_JING = BLOCKS.registerBlock("huang_jing", CaoYaoBlock::new, () -> BlockBehaviour.Properties.of().noCollision().offsetType(BlockBehaviour.OffsetType.XZ));
    public static final DeferredBlock<Block> BAI_TOU_WENG = BLOCKS.registerBlock("bai_tou_weng", CaoYaoBlock::new, () -> BlockBehaviour.Properties.of().noCollision().offsetType(BlockBehaviour.OffsetType.XZ));
    public static final DeferredBlock<Block> DAN_SHEN = BLOCKS.registerBlock("dan_shen", CaoYaoBlock::new, () -> BlockBehaviour.Properties.of().noCollision().offsetType(BlockBehaviour.OffsetType.XZ));
    public static final DeferredBlock<Block> SAN_QI = BLOCKS.registerBlock("san_qi", CaoYaoBlock::new, () -> BlockBehaviour.Properties.of().noCollision().offsetType(BlockBehaviour.OffsetType.XZ));
    public static final DeferredBlock<Block> HUANG_LIAN = BLOCKS.registerBlock("huang_lian", CaoYaoBlock::new, () -> BlockBehaviour.Properties.of().noCollision().offsetType(BlockBehaviour.OffsetType.XZ));
    public static final DeferredBlock<Block> HUANG_QI = BLOCKS.registerBlock("huang_qi", CaoYaoBlock::new, () -> BlockBehaviour.Properties.of().noCollision().offsetType(BlockBehaviour.OffsetType.XZ));
    public static final DeferredBlock<Block> MA_SHENG = BLOCKS.registerBlock("ma_sheng", CaoYaoBlock::new, () -> BlockBehaviour.Properties.of().noCollision().offsetType(BlockBehaviour.OffsetType.XZ));
    public static final DeferredBlock<Block> SHUI_XIAN = BLOCKS.registerBlock("shui_xian", CaoYaoBlock::new, () -> BlockBehaviour.Properties.of().noCollision().offsetType(BlockBehaviour.OffsetType.XZ));
    public static final DeferredBlock<Block> DANG_GUI = BLOCKS.registerBlock("dang_gui", CaoYaoBlock::new, () -> BlockBehaviour.Properties.of().noCollision().offsetType(BlockBehaviour.OffsetType.XZ));
    public static final DeferredBlock<Block> BAI_ZHI = BLOCKS.registerBlock("bai_zhi", CaoYaoBlock::new, () -> BlockBehaviour.Properties.of().noCollision().offsetType(BlockBehaviour.OffsetType.XZ));
    public static final DeferredBlock<Block> YU_LOU = BLOCKS.registerBlock("yu_lou", BaseBlock::new, () -> BlockBehaviour.Properties.of().noOcclusion());
}
