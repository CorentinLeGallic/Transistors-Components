package com.corentinlg.transistors_components.registry;

import com.corentinlg.transistors_components.TransistorsComponents;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {
  // Block Items
  public static final BlockItem VERTICAL_TRANSMITTER_BLOCK_ITEM = new BlockItem(ModBlocks.VERTICAL_TRANSMITTER_BLOCK, new Item.Settings().group(ItemGroup.REDSTONE));
  public static final BlockItem CHANNEL_BLOCK_ITEM = new BlockItem(ModBlocks.CHANNEL_BLOCK, new Item.Settings().group(ItemGroup.REDSTONE));
  public static final BlockItem BIDIRECTIONAL_REPEATER_BLOCK_ITEM = new BlockItem(ModBlocks.BIDIRECTIONAL_REPEATER_BLOCK, new Item.Settings().group(ItemGroup.REDSTONE));

  public static void registerItems() {
    Registry.register(Registry.ITEM, new Identifier(TransistorsComponents.MOD_ID, "vertical_transmitter_block"), VERTICAL_TRANSMITTER_BLOCK_ITEM);
    Registry.register(Registry.ITEM, new Identifier(TransistorsComponents.MOD_ID, "channel_block"), CHANNEL_BLOCK_ITEM);
    Registry.register(Registry.ITEM, new Identifier(TransistorsComponents.MOD_ID, "bidirectional_repeater_block"), BIDIRECTIONAL_REPEATER_BLOCK_ITEM);
  }
}
