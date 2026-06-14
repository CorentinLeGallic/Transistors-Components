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

import com.corentinlg.transistors_components.block.entity.ChannelBlockEntity;
import com.corentinlg.transistors_components.block.enums.ChannelMode;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class ChannelBlock extends Block implements BlockEntityProvider {

  // Block States & Properties

  public static final EnumProperty<ChannelMode> MODE = EnumProperty.of("mode", ChannelMode.class);
  public static final BooleanProperty SOURCE_POWERED = BooleanProperty.of("source_powered");
  public static final BooleanProperty GATE_POWERED = BooleanProperty.of("gate_powered");

  public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

  private static final VoxelShape SHAPE = Block.createCuboidShape(0f, 0f, 0f, 16f, 2f, 16f);

  // Constructor
  public ChannelBlock(Settings settings) {
    super(settings);
    setDefaultState(getDefaultState()
      .with(FACING, Direction.NORTH)
      .with(MODE, ChannelMode.NMOS)
      .with(SOURCE_POWERED, false)
      .with(GATE_POWERED, false)
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
    return getDefaultState().with(FACING, ctx.getPlayerFacing());
  }

  @Override
  public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
    if (!state.canPlaceAt(world, pos)) return Blocks.AIR.getDefaultState();
    return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
  }

  @Override
  protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
    builder.add(FACING, MODE, SOURCE_POWERED, GATE_POWERED);
  }

  // Block entity

  @Override
  public BlockEntity createBlockEntity(BlockView world) {
    return new ChannelBlockEntity();
  }

  // Interaction & World Logic

  @Override
  public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
    if (world.isClient()) return;

    // Source

    Direction behindDirection = state.get(FACING).getOpposite();
    BlockPos behindBlockPos = pos.offset(behindDirection);

    int sourcePowerStrength = world.getEmittedRedstonePower(behindBlockPos, behindDirection);
    boolean isSourcePowered = sourcePowerStrength > 0;

    // Gate

    BlockPos upBlockPos = pos.up();

    boolean isGatePowered = world.isReceivingRedstonePower(upBlockPos);

    BlockState newState = state
      .with(SOURCE_POWERED, isSourcePowered)
      .with(GATE_POWERED, isGatePowered);

    if (!state.equals(newState)) world.setBlockState(pos, newState, 3);

    // Apply the new output value if needed

    BlockEntity blockEntity = world.getBlockEntity(pos);

    if (blockEntity instanceof ChannelBlockEntity) {
      ChannelBlockEntity channelEntity = (ChannelBlockEntity) blockEntity;
            
      if (channelEntity.getOutputSignal() != sourcePowerStrength) {
        channelEntity.setOutputSignal(sourcePowerStrength);
        world.updateNeighborsAlways(pos, this);
      }
    }
  }

  @Override
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    if (!world.isClient() && hand == Hand.MAIN_HAND) {
      BlockState newState = state.cycle(MODE);
      world.setBlockState(pos, newState, 3);
    }

    return ActionResult.SUCCESS;
  }

  // Redstone

  @Override
  public boolean emitsRedstonePower(BlockState state) {
    return true;
  }

  @Override
  public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
    if (!isDrainActive(state)) return 0;
    
    if (state.get(FACING) == direction.getOpposite()) {
      BlockEntity blockEntity = world.getBlockEntity(pos);

      if (blockEntity instanceof ChannelBlockEntity) return ((ChannelBlockEntity) blockEntity).getOutputSignal();

      return 0;
    }

    return super.getWeakRedstonePower(state, world, pos, direction);
  }

  @Override
  public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
    return getWeakRedstonePower(state, world, pos, direction);
  }

  // Private properties

  private boolean isDrainActive(BlockState state) {
    boolean isDrainActive;

    boolean isSourcePowered = state.get(SOURCE_POWERED);
    boolean isGatePowered = state.get(GATE_POWERED);

    if (state.get(MODE) == ChannelMode.PMOS) {
      isDrainActive = isSourcePowered && !isGatePowered;
    } else {
      isDrainActive = isSourcePowered && isGatePowered;
    }

    return isDrainActive;
  }

  // Shape

  @Override
  public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
    return SHAPE;
  }
}