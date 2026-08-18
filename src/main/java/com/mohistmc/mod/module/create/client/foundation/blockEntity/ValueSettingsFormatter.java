package com.mohistmc.mod.module.create.client.foundation.blockEntity;

import com.mohistmc.mod.module.create.client.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.mohistmc.mod.module.create.client.foundation.gui.AllIcons;
import com.mohistmc.mod.module.create.client.foundation.utility.CreateLang;
import com.mohistmc.mod.module.create.foundation.blockEntity.behaviour.ValueSettings;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.Nullable;

public class ValueSettingsFormatter {

    @Nullable
    private final Function<ValueSettings, MutableComponent> formatter;

    public ValueSettingsFormatter(@Nullable Function<ValueSettings, MutableComponent> formatter) {
        this.formatter = formatter;
    }

    public ValueSettingsFormatter() {
        this(null);
    }

    public MutableComponent format(ValueSettings valueSettings) {
        return formatter == null ? toLocaleNumber(valueSettings) : formatter.apply(valueSettings);
    }

    public static MutableComponent toLocaleNumber(ValueSettings valueSettings) {
        return CreateLang.number(valueSettings.value()).component();
    }

    public static class ScrollOptionSettingsFormatter extends ValueSettingsFormatter {

        private final INamedIconOptions[] options;

        public ScrollOptionSettingsFormatter(INamedIconOptions[] options) {
            super(v -> Component.translatable(options[v.value()].getTranslationKey()));
            this.options = options;
        }

        public AllIcons getIcon(ValueSettings valueSettings) {
            return options[valueSettings.value()].getIcon();
        }

    }

}
