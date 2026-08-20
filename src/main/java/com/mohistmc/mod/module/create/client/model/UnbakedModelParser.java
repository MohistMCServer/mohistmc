/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.mohistmc.mod.module.create.client.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.internal.bind.TreeTypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.mohistmc.mod.module.create.client.model.obj.ObjLoader;
import com.mojang.math.Transformation;
import java.lang.reflect.Type;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.cuboid.CuboidModel;
import org.jspecify.annotations.Nullable;

public class UnbakedModelParser {
    public static Gson wrap(Gson gson) {
        return new GsonBuilder().registerTypeAdapterFactory(new Deserializer(gson))
            .registerTypeAdapter(Transformation.class, new TransformationHelper.Deserializer()).create();
    }

    public static class Deserializer implements JsonDeserializer<UnbakedModel>, TypeAdapterFactory {
        private static final TypeToken<? extends UnbakedModel> NEXT_TYPE = TypeToken.get(CuboidModel.class);
        private final Gson gson;
        private @Nullable TypeAdapter<?> cached;

        public Deserializer(Gson gson) {
            this.gson = gson;
        }

        @Override
        public UnbakedModel deserialize(
            JsonElement jsonElement,
            Type type,
            JsonDeserializationContext jsonDeserializationContext
        ) throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonElement element = jsonObject.get("loader");
            if (element != null && element.isJsonPrimitive() && element.getAsString().equals("neoforge:obj")) {
                return ObjLoader.INSTANCE.read(jsonObject, jsonDeserializationContext);
            }
            return gson.fromJson(new JsonTreeReader(jsonObject), NEXT_TYPE);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> TypeAdapter<T> create(Gson proxy, TypeToken<T> type) {
            if (type.getRawType() == UnbakedModel.class) {
                if (cached != null) {
                    return (TypeAdapter<T>) cached;
                }
                TreeTypeAdapter<T> adapter = new TreeTypeAdapter<>(null, (JsonDeserializer<T>) this, proxy, type, this);
                cached = adapter;
                return adapter;
            }
            return gson.getAdapter(type);
        }
    }
}
