package com.almostreliable.appelem.core;

import appeng.api.client.AEKeyRendering;
import appeng.api.client.StorageCellModels;
import appeng.api.stacks.AEKeyTypes;
import appeng.api.storage.StorageCells;
import appeng.core.definitions.AEBlockEntities;
import com.almostreliable.appelem.AppElem;
import com.almostreliable.appelem.element.*;
import com.almostreliable.appelem.network.PacketHandler;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.Nullable;
import sirttas.elementalcraft.api.capability.ElementalCraftCapabilities;

public final class AppElemRegistration {

    private AppElemRegistration() {}

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(AppElemRegistration::onRegisterEvent);
        modEventBus.addListener(AppElemRegistration::registerCapabilities);
        modEventBus.addListener(AppElemTab::registerContents);
        modEventBus.addListener(PacketHandler::registerPackets);

        AppElemBlocks.REGISTRY.register(modEventBus);
        AppElemItems.REGISTRY.register(modEventBus);

        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(AppElemClientRegistration::onClientSetup);
        }
    }

    private static void onRegisterEvent(RegisterEvent event) {
        if (event.getRegistryKey() == Registries.CREATIVE_MODE_TAB) {
            AppElemTab.register(event);
        }
        if (event.getRegistryKey() == Registries.ITEM) {
            registerElementKey();
        }
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            ElementalCraftCapabilities.ElementStorage.BLOCK,
            AEBlockEntities.INTERFACE,
            (be, ctx) -> new GenericInternalInventoryWrapper(be.getStorage())
        );
        event.registerBlockEntity(
            ElementalCraftCapabilities.ElementStorage.BLOCK,
            AEBlockEntities.PATTERN_PROVIDER,
            (be, ctx) -> new GenericInternalInventoryWrapper(be.getLogic().getReturnInv())
        );
    }

    private static void registerElementKey() {
        AEKeyTypes.register(ElementKeyType.INSTANCE);
        ElementStrategies.register();
    }

    static String getNameOrFormatId(String id, @Nullable String name) {
        if (name == null) {
            String[] parts = id.split("_");
            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1);
            }
            return String.join(" ", parts);
        }
        return name;
    }

    private static class AppElemClientRegistration {

        private static void onClientSetup(FMLClientSetupEvent ignoredEvent) {
            AEKeyRendering.register(ElementKeyType.INSTANCE, ElementKey.class, new ElementRenderer());

            StorageCells.addCellGuiHandler(new ElementCellGuiHandler());

            for (var cell : AppElemItems.getCells()) {
                String path = cell.getId().getPath();
                String tier = path.substring(path.lastIndexOf('_') + 1);
                StorageCellModels.registerModel(cell, AppElem.id("block/" + tier + "_element_cell"));
            }
            for (var portableCell : AppElemItems.getPortableCells()) {
                String path = portableCell.getId().getPath();
                String tier = path.substring(path.lastIndexOf('_') + 1);
                StorageCellModels.registerModel(portableCell, AppElem.id("block/" + tier + "_element_cell"));
            }
        }
    }
}
