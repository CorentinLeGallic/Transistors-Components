package com.corentinlg.transistors_components.util.enums;

import net.minecraft.util.math.Direction;

public enum HorizontalDirection {
  NORTH(Direction.NORTH),
  EAST(Direction.EAST),
  SOUTH(Direction.SOUTH),
  WEST(Direction.WEST);

  private final Direction direction;

  HorizontalDirection(Direction direction) {
    this.direction = direction;
  }

  public Direction getDirection() {
    return this.direction;
  }

  public static final Direction[] VALUES = new Direction[]{
    Direction.NORTH, 
    Direction.EAST, 
    Direction.SOUTH, 
    Direction.WEST
  };
}
