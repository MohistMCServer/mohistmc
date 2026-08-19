package com.mohistmc.mod.module.flywheel.api.layout;

public sealed interface ElementType permits ScalarElementType, VectorElementType, MatrixElementType, ArrayElementType {
    int byteSize();

    int byteAlignment();
}
