package com.corentinlg.transistors_components.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.corentinlg.transistors_components.block.custom.ChannelBlock;
import com.corentinlg.transistors_components.registry.ModBlocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.math.Direction;

@Mixin(RedstoneWireBlock.class)
public class RedstoneWireDirectionEditor {
  @Inject(
    method = "connectsTo(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;)Z",
    at = @At("HEAD"),
    cancellable = true
  )
  private static void onConnectsTo(BlockState state, @Nullable Direction dir, CallbackInfoReturnable<Boolean> cir) {
    if (state.isOf(ModBlocks.CHANNEL_BLOCK)) {
			Direction direction = state.get(ChannelBlock.FACING);

      boolean shouldConnect = (direction == dir || direction.getOpposite() == dir);
			
      cir.setReturnValue(shouldConnect);
		}
  }
}
