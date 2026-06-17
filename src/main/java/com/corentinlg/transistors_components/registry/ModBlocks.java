package com.corentinlg.transistors_components.registry;

import com.corentinlg.transistors_components.TransistorsComponents;
import com.corentinlg.transistors_components.block.custom.BidirectionalRepeaterBlock;
import com.corentinlg.transistors_components.block.custom.ChannelBlock;
import com.corentinlg.transistors_components.block.custom.VerticalTransmitterBlock;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlocks {
  public static final Block VERTICAL_TRANSMITTER_BLOCK = new VerticalTransmitterBlock(FabricBlockSettings
    .of(Material.METAL)
    .requiresTool()
    .breakByTool(FabricToolTags.PICKAXES, 1)
    .strength(3.0f, 6.0f)
    .sounds(BlockSoundGroup.METAL)
  );

  public static final ChannelBlock CHANNEL_BLOCK = new ChannelBlock(FabricBlockSettings
    .of(Material.SUPPORTED)
    .breakByHand(true)
    .strength(0f, 0f)
    .sounds(BlockSoundGroup.WOOD)
    .nonOpaque()
  );

  public static final BidirectionalRepeaterBlock BIDIRECTIONAL_REPEATER_BLOCK = new BidirectionalRepeaterBlock(FabricBlockSettings
    .of(Material.SUPPORTED)
    .breakByHand(true)
    .strength(0f, 0f)
    .sounds(BlockSoundGroup.WOOD)
    .nonOpaque()
  );

  public static void registerBlocks() {
    Registry.register(Registry.BLOCK, new Identifier(TransistorsComponents.MOD_ID, "vertical_transmitter_block"), VERTICAL_TRANSMITTER_BLOCK);
    Registry.register(Registry.BLOCK, new Identifier(TransistorsComponents.MOD_ID, "channel_block"), CHANNEL_BLOCK);
    Registry.register(Registry.BLOCK, new Identifier(TransistorsComponents.MOD_ID, "bidirectional_repeater_block"), BIDIRECTIONAL_REPEATER_BLOCK);
  }
}
