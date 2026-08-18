package com.mohistmc.mod.module.create.client.flywheel.impl.layout;

import com.mohistmc.mod.module.create.client.flywheel.api.layout.ScalarElementType;
import com.mohistmc.mod.module.create.client.flywheel.api.layout.ValueRepr;

record ScalarElementTypeImpl(ValueRepr repr, int byteSize, int byteAlignment) implements ScalarElementType {
    static ScalarElementTypeImpl create(ValueRepr repr) {
        return new ScalarElementTypeImpl(repr, repr.byteSize(), repr.byteSize());
    }
}
