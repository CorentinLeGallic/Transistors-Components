/*
 * Transistor Components Mod - A Minecraft mod that adds several blocks facilitating transistors and logical gates creation in Minecraft.
 * Copyright (C) 2026 Corentin LE GALLIC <corentinlg.dev@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,

 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.corentinlg.transistors_components.block.custom;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.corentinlg.transistors_components.block.enums.BidirectionalRepeaterDirection;
import com.corentinlg.transistors_components.block.enums.BidirectionalRepeaterMode;
import com.corentinlg.transistors_components.block.enums.BidirectionalRepeaterSource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SideShapeType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class BidirectionalRepeaterBlock extends Block {

  // Block States & Properties
  
  public static final EnumProperty<BidirectionalRepeaterMode> MODE = EnumProperty.of("mode", BidirectionalRepeaterMode.class);
  public static final EnumProperty<BidirectionalRepeaterDirection> DIRECTION = EnumProperty.of("direction", BidirectionalRepeaterDirection.class);

  private static final VoxelShape SHAPE = Block.createCuboidShape(0f, 0f, 0f, 16f, 2f, 16f);
  
  private static final ThreadLocal<Set<BlockPos>> INSTANT_THREAD_LOCAL = ThreadLocal.withInitial(HashSet::new);

  // Constructor
  public BidirectionalRepeaterBlock(Settings settings) {
    super(settings);
    setDefaultState(getDefaultState()
      .with(MODE, BidirectionalRepeaterMode.DISABLED)
      .with(DIRECTION, BidirectionalRepeaterDirection.Z)
    );
  }

  // Registration & Placement

  @Override
  public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
    BlockPos belowBlockPos = pos.down();
    BlockState belowBlockState = world.getBlockState(belowBlockPos);
    
    return SideShapeType.RIGID.matches(belowBlockState, world, belowBlockPos, Direction.UP);
  }

  @Override
  public BlockState getPlacementState(ItemPlacementContext ctx) {
    Direction playerFacing = ctx.getPlayerFacing();

    switch (playerFacing) {
      case NORTH: case SOUTH:
        return getDefaultState().with(DIRECTION, BidirectionalRepeaterDirection.Z);
      case WEST: case EAST:
        return getDefaultState().with(DIRECTION, BidirectionalRepeaterDirection.X);
      default:
        return getDefaultState();
    }
  }

  @Override
  public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
    if (!state.canPlaceAt(world, pos)) return Blocks.AIR.getDefaultState();
    return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(MODE, DIRECTION);
  }

  // Interaction & World Logic

  @Override
  public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
    if (world.isClient()) return;

    // Prevent output updates if an update is already ongoing

    Set<BlockPos> processing = INSTANT_THREAD_LOCAL.get();

    if (processing.contains(pos)) return;

    runInstantUpdateLogic(world, pos);
  }

  // Redstone

  @Override
  public boolean emitsRedstonePower(BlockState state) {
    return true;
  }

  @Override
  public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
    BidirectionalRepeaterMode modeState = state.get(MODE);
    BidirectionalRepeaterDirection directionState = state.get(DIRECTION);

    if (modeState == BidirectionalRepeaterMode.DISABLED) return 0;

    if (modeState == BidirectionalRepeaterMode.POWERED_A) {
      if (directionState == BidirectionalRepeaterDirection.Z) return (direction == Direction.SOUTH) ? 15 : 0;
      if (directionState == BidirectionalRepeaterDirection.X) return (direction == Direction.WEST) ? 15 : 0;
    }
    
    if (modeState == BidirectionalRepeaterMode.POWERED_B) {
      if (directionState == BidirectionalRepeaterDirection.Z) return (direction == Direction.NORTH) ? 15 : 0;
      if (directionState == BidirectionalRepeaterDirection.X) return (direction == Direction.EAST) ? 15 : 0;
    }

    return super.getWeakRedstonePower(state, world, pos, direction);
  }

  @Override
  public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
    return getWeakRedstonePower(state, world, pos, direction);
  }

  // Shape

  @Override
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return SHAPE;
  }

@Environment(EnvType.CLIENT)
@Override
public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
  BidirectionalRepeaterMode mode = state.get(MODE);

  if (mode == BidirectionalRepeaterMode.DISABLED) return;

  BidirectionalRepeaterDirection direction = state.get(DIRECTION);
  
  double d = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
  double e = pos.getY() + 0.4375 + (random.nextDouble() - 0.5) * 0.1;
  double f = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
  
  float pixelOffset = (mode == BidirectionalRepeaterMode.POWERED_A) ? 5.0F : -5.0F;
  double offset = pixelOffset / 16.0;

  double h = (direction == BidirectionalRepeaterDirection.Z) ? 0 : offset;
  double i = (direction == BidirectionalRepeaterDirection.Z) ? offset : 0;
  
  world.addParticle(DustParticleEffect.RED, d + h, e, f + i, 0.0, 0.0, 0.0);
}

  // Private properties

  private void runInstantUpdateLogic(World world, BlockPos pos) {
    Set<BlockPos> processing = INSTANT_THREAD_LOCAL.get();

    // Update the output as soon as the setBlockState method ends
    
    while (true) {
      BlockState state = world.getBlockState(pos);
      if (!(state.getBlock() instanceof BidirectionalRepeaterBlock)) break;

      BidirectionalRepeaterMode modeState = state.get(MODE);
  
      boolean isReceivingA = isReceivingRedstonePower(state, world, pos, BidirectionalRepeaterSource.A);
      boolean isReceivingB = isReceivingRedstonePower(state, world, pos, BidirectionalRepeaterSource.B);
      
      BlockState newState = state;
  
      if (modeState == BidirectionalRepeaterMode.DISABLED) {
        if (isReceivingA) {
          newState = newState.with(MODE, BidirectionalRepeaterMode.POWERED_A);
        } else if (isReceivingB) {
          newState = newState.with(MODE, BidirectionalRepeaterMode.POWERED_B);
        }
      } else if (modeState == BidirectionalRepeaterMode.POWERED_A) {
        if (!isReceivingA) {
          newState = newState.with(MODE, BidirectionalRepeaterMode.DISABLED);
        }
      } else if (modeState == BidirectionalRepeaterMode.POWERED_B) {
        if (!isReceivingB) {
          newState = newState.with(MODE, BidirectionalRepeaterMode.DISABLED);
        }
      }

      if (state.equals(newState)) break;

      processing.add(pos);

      try {
        world.setBlockState(pos, newState, 3);
      } finally {
        processing.remove(pos);
      }
    }
  }

  private boolean isReceivingRedstonePower(BlockState state, World world, BlockPos pos, BidirectionalRepeaterSource source) {
    Direction sourceDirection = (state.get(DIRECTION) == BidirectionalRepeaterDirection.Z)
      ? (source == BidirectionalRepeaterSource.A ? Direction.SOUTH : Direction.NORTH)
      : (source == BidirectionalRepeaterSource.A ? Direction.WEST : Direction.EAST);

    BlockPos sourceBlockPos = pos.offset(sourceDirection);

    return world.isEmittingRedstonePower(sourceBlockPos, sourceDirection);
  }
}