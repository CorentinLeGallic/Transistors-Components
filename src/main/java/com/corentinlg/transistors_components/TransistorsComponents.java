package com.corentinlg.transistors_components;

import net.fabricmc.api.ModInitializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.corentinlg.transistors_components.registry.ModBlockEntityTypes;
import com.corentinlg.transistors_components.registry.ModBlocks;
import com.corentinlg.transistors_components.registry.ModItems;

public class TransistorsComponents implements ModInitializer {
	public static final String MOD_ID = "transistors_components";

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerItems();
		ModBlocks.registerBlocks();
		ModBlockEntityTypes.registerBlockEntityTypes();
	}
}