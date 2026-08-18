package com.mohistmc.mod.module.create;

import com.mohistmc.mod.module.create.api.registry.CreateRegistries;
import com.mohistmc.mod.module.create.content.equipment.blueprint.BlueprintEntity.BlueprintSection;
import com.mohistmc.mod.module.create.content.equipment.blueprint.BlueprintMenu;
import com.mohistmc.mod.module.create.content.equipment.toolbox.ToolboxBlockEntity;
import com.mohistmc.mod.module.create.content.equipment.toolbox.ToolboxMenu;
import com.mohistmc.mod.module.create.content.logistics.factoryBoard.FactoryPanelSetItemMenu;
import com.mohistmc.mod.module.create.content.logistics.factoryBoard.ServerFactoryPanelBehaviour;
import com.mohistmc.mod.module.create.content.logistics.filter.AttributeFilterMenu;
import com.mohistmc.mod.module.create.content.logistics.filter.FilterMenu;
import com.mohistmc.mod.module.create.content.logistics.filter.PackageFilterMenu;
import com.mohistmc.mod.module.create.content.logistics.packagePort.PackagePortBlockEntity;
import com.mohistmc.mod.module.create.content.logistics.packagePort.PackagePortMenu;
import com.mohistmc.mod.module.create.content.logistics.redstoneRequester.RedstoneRequesterBlockEntity;
import com.mohistmc.mod.module.create.content.logistics.redstoneRequester.RedstoneRequesterMenu;
import com.mohistmc.mod.module.create.content.logistics.stockTicker.StockKeeperCategoryMenu;
import com.mohistmc.mod.module.create.content.logistics.stockTicker.StockKeeperRequestMenu;
import com.mohistmc.mod.module.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.mohistmc.mod.module.create.content.redstone.link.controller.LinkedControllerMenu;
import com.mohistmc.mod.module.create.content.schematics.cannon.SchematicannonBlockEntity;
import com.mohistmc.mod.module.create.content.schematics.cannon.SchematicannonMenu;
import com.mohistmc.mod.module.create.content.schematics.table.SchematicTableBlockEntity;
import com.mohistmc.mod.module.create.content.schematics.table.SchematicTableMenu;
import com.mohistmc.mod.module.create.content.trains.schedule.ScheduleMenu;
import com.mohistmc.mod.module.create.foundation.gui.menu.MenuType;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import static com.mohistmc.mod.module.create.Create.MOD_ID;

public class AllMenuTypes {
    public static final MenuType<ItemStack> SCHEDULE = register("schedule", ScheduleMenu::new);
    public static final MenuType<ItemStack> LINKED_CONTROLLER = register(
        "linked_controller",
        LinkedControllerMenu::new
    );
    public static final MenuType<ItemStack> FILTER = register("filter", FilterMenu::new);
    public static final MenuType<ItemStack> ATTRIBUTE_FILTER = register("attribute_filter", AttributeFilterMenu::new);
    public static final MenuType<ItemStack> PACKAGE_FILTER = register("package_filter", PackageFilterMenu::new);
    public static final MenuType<RedstoneRequesterBlockEntity> REDSTONE_REQUESTER = register(
        "redstone_requester",
        RedstoneRequesterMenu::new
    );
    public static final MenuType<StockTickerBlockEntity> STOCK_KEEPER_CATEGORY = register(
        "stock_keeper_category",
        StockKeeperCategoryMenu::new
    );
    public static final MenuType<StockTickerBlockEntity> STOCK_KEEPER_REQUEST = register(
        "stock_keeper_request",
        StockKeeperRequestMenu::new
    );
    public static final MenuType<PackagePortBlockEntity> PACKAGE_PORT = register("package_port", PackagePortMenu::new);
    public static final MenuType<ServerFactoryPanelBehaviour> FACTORY_PANEL_SET_ITEM = register(
        "factory_panel_set_item",
        FactoryPanelSetItemMenu::new
    );
    public static final MenuType<BlueprintSection> CRAFTING_BLUEPRINT = register(
        "crafting_blueprint",
        BlueprintMenu::new
    );
    public static final MenuType<ToolboxBlockEntity> TOOLBOX = register("toolbox", ToolboxMenu::new);
    public static final MenuType<SchematicTableBlockEntity> SCHEMATIC_TABLE = register(
        "schematic_table",
        SchematicTableMenu::new
    );
    public static final MenuType<SchematicannonBlockEntity> SCHEMATICANNON = register(
        "schematicannon",
        SchematicannonMenu::new
    );

    public static <T> MenuType<T> register(String name, MenuType<T> type) {
        return Registry.register(CreateRegistries.MENU_TYPE, Identifier.fromNamespaceAndPath(MOD_ID, name), type);
    }

    public static void register() {
    }
}
