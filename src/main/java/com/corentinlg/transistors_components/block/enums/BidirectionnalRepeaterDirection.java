package com.corentinlg.transistors_components.block.enums;

import net.minecraft.util.StringIdentifiable;

public enum BidirectionnalRepeaterDirection implements StringIdentifiable {
  X("x"),
  Z("z");

  private final String name;

  BidirectionnalRepeaterDirection(String name) {
    this.name = name;
  }

  @Override
  public String asString() {
    return this.name;
  }
}
