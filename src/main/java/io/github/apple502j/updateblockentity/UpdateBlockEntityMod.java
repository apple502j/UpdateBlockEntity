package io.github.apple502j.updateblockentity;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpdateBlockEntityMod implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("updateblockentity");

	@Override
	public void onInitialize() {
		LOGGER.info("Loaded UpdateBlockEntity");
	}
}
