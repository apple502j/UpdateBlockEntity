package io.github.apple502j.updateblockentity.mixin;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Codec;
import io.github.apple502j.updateblockentity.UpdateBlockEntityMod;
import io.github.apple502j.updateblockentity.UpdateBlockEntityUtils;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.storage.VersionedChunkStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Optional;
import java.util.function.Supplier;

@Mixin(VersionedChunkStorage.class)
public class VersionedChunkStorageMixin {
    @Shadow @Final protected DataFixer dataFixer;

    @ModifyArgs(method = "updateChunkNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtHelper;update(Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/datafixer/DataFixTypes;Lnet/minecraft/nbt/NbtCompound;I)Lnet/minecraft/nbt/NbtCompound;"))
    private void beforeUpdate(Args args) {
        NbtCompound nbt = args.get(2);
        int oldVersion = VersionedChunkStorage.getDataVersion(nbt);
        if (oldVersion >= UpdateBlockEntityUtils.CHUNK_LEVEL_FIX_DATA_VERSION) {
            // This fix only applies to pre-2842 to post-2842 for now
            return;
        }
        // Update to data version 2841 first to apply any changes in previous versions
        nbt = NbtHelper.update(this.dataFixer, DataFixTypes.CHUNK, nbt, oldVersion, UpdateBlockEntityUtils.SAFE_UPGRADE_DATA_VERSION);
        NbtCompound context = nbt.getCompound("__context");
        NbtList blockEntities = nbt.getCompound("Level").getList("TileEntities", NbtElement.COMPOUND_TYPE);
        context.put(UpdateBlockEntityUtils.KEY, blockEntities);
        args.set(2, nbt);
        args.set(3, UpdateBlockEntityUtils.SAFE_UPGRADE_DATA_VERSION);
    }

    @Inject(method = "updateChunkNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtCompound;remove(Ljava/lang/String;)V"))
    private void afterUpdate(RegistryKey<World> worldKey, Supplier<PersistentStateManager> persistentStateManagerFactory, NbtCompound nbt, Optional<RegistryKey<Codec<? extends ChunkGenerator>>> generatorCodecKey, CallbackInfoReturnable<NbtCompound> cir) {
        NbtCompound context = nbt.getCompound("__context");
        if (context == null) {
            return;
        }
        NbtList blockEntities = context.getList(UpdateBlockEntityUtils.KEY, NbtElement.COMPOUND_TYPE);
        if (blockEntities == null) {
            // Updating between safe versions; no action necessary
            return;
        }
        if (blockEntities.size() > 0) {
            UpdateBlockEntityMod.LOGGER.info("Updating " + blockEntities.size() + " block entities");
            for (int i = 0; i < blockEntities.size(); i++) {
                NbtCompound blockEntityNbt = blockEntities.getCompound(i);
                blockEntities.set(i, UpdateBlockEntityUtils.update(this.dataFixer, blockEntityNbt, SharedConstants.getGameVersion().getWorldVersion()));
            }
            nbt.put("block_entities", blockEntities);
        }
        context.remove(UpdateBlockEntityUtils.KEY);
    }
}
