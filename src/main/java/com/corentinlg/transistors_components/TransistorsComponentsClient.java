package com.corentinlg.transistors_components;

import com.corentinlg.transistors_components.registry.ModBlocks;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class TransistorsComponentsClient implements ClientModInitializer {

  public void onInitializeClient() {
    BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CHANNEL_BLOCK, RenderLayer.getCutout());
    BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BIDIRECTIONAL_REPEATER_BLOCK, RenderLayer.getCutout());
  }
}
