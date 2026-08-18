package com.mohistmc.mod.module.jei;

import com.mohistmc.mod.module.create.client.content.logistics.stockTicker.StockKeeperRequestScreen;
import java.util.Optional;
import mezz.jei.api.gui.builder.IClickableIngredientFactory;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.runtime.IClickableIngredient;

public class StockKeeperGuiContainerHandler implements IGuiContainerHandler<StockKeeperRequestScreen> {
    @Override
    public Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse(
        IClickableIngredientFactory factory,
        StockKeeperRequestScreen containerScreen,
        double mouseX,
        double mouseY
    ) {
        return containerScreen.getHoveredIngredient((int) mouseX, (int) mouseY)
            .flatMap(pair -> factory.createBuilder(pair.getFirst()).buildWithArea(pair.getSecond()));
    }
}
