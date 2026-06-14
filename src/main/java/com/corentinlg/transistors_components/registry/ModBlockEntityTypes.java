package com.corentinlg.transistors_components.registry;

import com.corentinlg.transistors_components.TransistorsComponents;
import com.corentinlg.transistors_components.block.entity.ChannelBlockEntity;
import com.corentinlg.transistors_components.block.entity.VerticalTransmitterBlockEntity;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlockEntityTypes {
  public static final BlockEntityType<ChannelBlockEntity> CHANNEL_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.create(ChannelBlockEntity::new, ModBlocks.CHANNEL_BLOCK).build(null);
  public static final BlockEntityType<VerticalTransmitterBlockEntity> VERTICAL_TRANSMITTER_BLOCK_ENTITY_TYPE = BlockEntityType.Builder.create(VerticalTransmitterBlockEntity::new, ModBlocks.VERTICAL_TRANSMITTER_BLOCK).build(null);

  public static void registerBlockEntityTypes() {
    Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(TransistorsComponents.MOD_ID, "channel_block_entity"), CHANNEL_BLOCK_ENTITY_TYPE);
    Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(TransistorsComponents.MOD_ID, "vertical_transmitter_block_entity"), VERTICAL_TRANSMITTER_BLOCK_ENTITY_TYPE);
  }
}
