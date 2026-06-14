package com.corentinlg.transistors_components.block.entity;

import com.corentinlg.transistors_components.registry.ModBlockEntityTypes;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;

public class ChannelBlockEntity extends BlockEntity {
  private int outputSignal;

  public ChannelBlockEntity() {
    super(ModBlockEntityTypes.CHANNEL_BLOCK_ENTITY_TYPE);
  }

  @Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		tag.putInt("OutputSignal", this.outputSignal);
		return tag;
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag) {
		super.fromTag(state, tag);
		this.outputSignal = tag.getInt("OutputSignal");
	}

	public int getOutputSignal() {
		return this.outputSignal;
	}

	public void setOutputSignal(int outputSignal) {
		this.outputSignal = outputSignal;
	}
}
