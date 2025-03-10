package com.almostreliable.appelem.data;

import appeng.core.AppEng;
import appeng.items.tools.powered.PortableCellItem;
import com.almostreliable.appelem.AppElem;
import com.almostreliable.appelem.BuildConfig;
import com.almostreliable.appelem.content.ElementStorageCell;
import com.almostreliable.appelem.core.AppElemBlocks;
import com.almostreliable.appelem.core.AppElemItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;
import sirttas.elementalcraft.block.ECBlocks;

class AppElemModels extends BlockStateProvider {

    AppElemModels(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, BuildConfig.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // blocks
        simpleBlockWithExistingModel(AppElemBlocks.CONTAINER.get());

        // cells
        itemModels().basicItem(AppElemItems.ELEMENT_CELL_HOUSING.get());
        models().singleTexture("element_cell", AppEng.makeId("block/drive/drive_cell"), "cell", AppElem.id("block/element_cell"));
        for (var cell : AppElemItems.getCells()) {
            cell(cell);
        }
        for (var portableCell : AppElemItems.getPortableCells()) {
            portableCell(portableCell);
        }

        // parts
        p2p();
    }

    private void simpleBlockWithExistingModel(Block block) {
        ModelFile modelFile = models().getExistingFile(blockTexture(block));
        simpleBlockWithItem(block, modelFile);
    }

    private void cell(DeferredItem<ElementStorageCell> cell) {
        itemModels().basicItem(cell.get()).texture("layer1", AppEng.makeId("item/storage_cell_led"));
    }

    private void portableCell(DeferredItem<PortableCellItem> portableCell) {
        String path = portableCell.getId().getPath();
        String tier = portableCell.get().getTier().namePrefix();
        itemModels().singleTexture(path, mcLoc("item/generated"), "layer0", AppEng.makeId("item/portable_cell_screen"))
            .texture("layer1", AppEng.makeId("item/portable_cell_led"))
            .texture("layer2", AppElem.id("item/portable_element_cell_housing"))
            .texture("layer3", AppEng.makeId("item/portable_cell_side_" + tier));
    }

    private void p2p() {
        ResourceLocation texture = blockTexture(ECBlocks.WHITE_ROCK.get());
        itemModels().withExistingParent("item/element_p2p_tunnel", AppEng.makeId("item/p2p_tunnel_base")).texture("type", texture);
        itemModels().withExistingParent("part/element_p2p_tunnel", AppEng.makeId("part/p2p/p2p_tunnel_base")).texture("type", texture);
    }
}
