package com.mohistmc.mod.module.create.client.catnip.gui;

import com.mohistmc.mod.module.create.client.catnip.render.BindableTexture;

public interface TextureSheetSegment extends BindableTexture {

    int getStartX();

    int getStartY();

    int getWidth();

    int getHeight();

}
