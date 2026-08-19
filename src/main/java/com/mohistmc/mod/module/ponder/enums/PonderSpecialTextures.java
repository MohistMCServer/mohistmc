package com.mohistmc.mod.module.ponder.enums;

import com.mohistmc.mod.module.create.client.catnip.render.BindableTexture;
import com.mohistmc.mod.module.ponder.Ponder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;

public enum PonderSpecialTextures implements BindableTexture {
    BLANK("blank.png");

    public static final String ASSET_PATH = "textures/special/";
    private final Identifier location;

    PonderSpecialTextures(String filename) {
        location = Ponder.asResource(ASSET_PATH + filename);
    }

    @Override
    public TextureSetup bind() {
        TextureManager manager = Minecraft.getInstance().getTextureManager();
        AbstractTexture texture = manager.getTexture(location);
        return TextureSetup.singleTexture(texture.getTextureView(), texture.getSampler());
    }

    @Override
    public Identifier getLocation() {
        return location;
    }

}
