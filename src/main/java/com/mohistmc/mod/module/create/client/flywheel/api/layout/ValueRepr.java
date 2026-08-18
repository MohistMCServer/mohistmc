package com.mohistmc.mod.module.create.client.flywheel.api.layout;

public sealed interface ValueRepr permits IntegerRepr, UnsignedIntegerRepr, FloatRepr {
    int byteSize();
}
