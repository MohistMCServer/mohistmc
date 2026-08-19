package com.mohistmc.mod.module.flywheel.impl.visualization.storage;

import com.mohistmc.mod.module.flywheel.api.visual.SectionTrackedVisual;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Unmodifiable;

public class SectionTracker implements SectionTrackedVisual.SectionCollector {
    private final List<Runnable> listeners = new ArrayList<>(2);

    @Unmodifiable
    private LongSet sections = LongSet.of();

    @Unmodifiable
    public LongSet sections() {
        return sections;
    }

    @Override
    public void sections(LongSet sections) {
        this.sections = LongSets.unmodifiable(new LongArraySet(sections));
        listeners.forEach(Runnable::run);
    }

    public void addListener(Runnable listener) {
        listeners.add(listener);
    }
}
