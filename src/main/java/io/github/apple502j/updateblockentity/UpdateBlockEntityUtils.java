package io.github.apple502j.updateblockentity;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;

public final class UpdateBlockEntityUtils {
    private UpdateBlockEntityUtils() {}

    public static final int SAFE_UPGRADE_DATA_VERSION = 2841;
    public static final int CHUNK_LEVEL_FIX_DATA_VERSION = 2842;
    public static final String KEY = "UpdateBlockEntity";

    public static NbtCompound update(DataFixer dataFixer, NbtCompound nbt, int target) {
        return (NbtCompound)dataFixer.update(TypeReferences.BLOCK_ENTITY, new Dynamic(NbtOps.INSTANCE, nbt), SAFE_UPGRADE_DATA_VERSION, target).getValue();
    }
}
