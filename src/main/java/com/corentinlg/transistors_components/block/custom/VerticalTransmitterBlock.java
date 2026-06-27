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

import java.util.ArrayList;
import java.util.List;

import com.corentinlg.transistors_components.block.entity.VerticalTransmitterBlockEntity;
import com.corentinlg.transistors_components.util.enums.HorizontalDirection;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class VerticalTransmitterBlock extends Block implements BlockEntityProvider {

  // Block States & Properties
  
  public static final BooleanProperty POWERED = BooleanProperty.of("powered");

  // Constructor
  public VerticalTransmitterBlock(Settings settings) {
    super(settings);
    setDefaultState(getDefaultState()
      .with(POWERED, false)
    );
  }

  // Registration & Placement

  @Override
  public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
    if (!world.isClient() && !state.isOf(newState.getBlock())) {
      super.onStateReplaced(state, world, pos, newState, moved);
      world.updateNeighborsAlways(pos, Blocks.AIR);
    }
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(POWERED);
  }

  // Block entity

  @Override
  public BlockEntity createBlockEntity(BlockView world) {
    return new VerticalTransmitterBlockEntity();
  }

  // Interaction & World Logic

  @Override
  public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
    // Disable updates from vertical transmitters
    if (block == this) return;
    
    // Find others transmitters in the column
    List<BlockPos> columnPositions = findColumnPositions(world, pos);

    // Determine whether the column is powered or not
    boolean isColumnExternallyPowered = false;

    for (BlockPos columnPosition : columnPositions) {
      if (isPoweredExternally(world, columnPosition)) {
        isColumnExternallyPowered = true;
        break;
      }
    }

    // Set the correct POWERED value to all the vertical transmitters of the column
    for (BlockPos columnPosition : columnPositions) {
      BlockState currentState = world.getBlockState(columnPosition);

      if (currentState.get(POWERED) != isColumnExternallyPowered) {
        world.setBlockState(columnPosition, currentState.with(POWERED, isColumnExternallyPowered), 2);
        
        Direction[] horizontalDirections = HorizontalDirection.VALUES;

        for (Direction horizontalDirection : horizontalDirections) {
          BlockPos neighborPos = columnPosition.offset(horizontalDirection);

          world.updateNeighbor(neighborPos, this, columnPosition);
        }

        if (!world.getBlockState(columnPosition.up()).isOf(this)) world.updateNeighbor(columnPosition.up(), this, columnPosition);
      }
    }
  }

  // Redstone

  @Override
  public boolean emitsRedstonePower(BlockState state) {
    return true;
  }

  @Override
  public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
    if (!state.get(POWERED)) return 0;

    Direction[] horizontalDirections = HorizontalDirection.VALUES;

    for (Direction currentDirection : horizontalDirections) {
      if (direction == currentDirection || direction == Direction.DOWN) return 15;
    }

    return 0;
  }

  @Override
  public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
    return 0;
  }

  // Private methpods

  private List<BlockPos> findColumnPositions(World world, BlockPos startPos) {
    List<BlockPos> columnPositions = new ArrayList<BlockPos>();

    BlockPos currentPos = startPos;

    // Find the vertical transmitters upward
    while (world.getBlockState(currentPos).getBlock() == this) {
      columnPositions.add(currentPos);
      currentPos = currentPos.up();
    }

    currentPos = startPos.down();

    // Find the vertical transmitters downward
    while (world.getBlockState(currentPos).getBlock() == this) {
      columnPositions.add(currentPos);
      currentPos = currentPos.down();
    }

    return columnPositions;
  }

  private boolean isPoweredExternally(World world, BlockPos pos) {
    for (Direction direction : HorizontalDirection.VALUES) {
      BlockPos neighborBlockPos = pos.offset(direction);
      BlockState neighborBlockState = world.getBlockState(neighborBlockPos);

      if (neighborBlockState.getBlock() == this || neighborBlockState.isOf(Blocks.REDSTONE_WIRE)) continue;
      if (neighborBlockState.isSolidBlock(world, neighborBlockPos) && world.getBlockState(neighborBlockPos.up()).isOf(Blocks.REDSTONE_WIRE)) continue;

      if (world.getEmittedRedstonePower(neighborBlockPos, direction) == 15) return true;
    }

    return false;
  }
}
