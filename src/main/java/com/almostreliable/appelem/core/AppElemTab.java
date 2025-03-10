package com.almostreliable.appelem.core;

import com.almostreliable.appelem.AppElem;
import com.almostreliable.appelem.data.AppElemLang;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;

final class AppElemTab {

    private static final ResourceKey<CreativeModeTab> KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, AppElem.id("main"));
    private static final List<DeferredItem<? extends Item>> ITEMS = new ArrayList<>();

    private AppElemTab() {}

    static void register(RegisterEvent registerEvent) {
        CreativeModeTab tab = CreativeModeTab.builder()
            .title(AppElemLang.TAB_NAME.get())
            .icon(AppElemBlocks.CONTAINER::toStack)
            .build();

        registerEvent.register(Registries.CREATIVE_MODE_TAB, KEY.location(), () -> tab);
    }

    static void add(DeferredItem<? extends Item> item) {
        ITEMS.add(item);
    }

    static void registerContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() != KEY) return;
        for (var item : ITEMS) {
            event.accept(item);
        }
    }
}
