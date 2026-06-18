// This file is licensed under the MIT License.

package cn.idea12.woodenanvil;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class WoodenAnvilBlock extends AnvilBlock {
    private final MutableComponent displayName;

    public WoodenAnvilBlock(Properties properties, MutableComponent displayName) {
        super(properties);
        this.displayName = displayName;
    }

    @Override
    public MutableComponent getName() {
        return this.displayName;
    }

    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider(
                (containerId, inventory, player) -> new AnvilMenu(containerId, inventory, ContainerLevelAccess.create(level, pos)),
                this.displayName
        );
    }

    public static boolean isWoodenAnvil(BlockState state) {
        return state.getBlock() instanceof WoodenAnvilBlock;
    }

    public static @Nullable BlockState damageWoodenAnvil(BlockState state) {
        Block block = state.getBlock();
        for (Map.Entry<DeferredBlock<AnvilBlock>, DeferredBlock<AnvilBlock>> entry : WoodenAnvilRegistry.DAMAGE_MAP.entrySet()) {
            if (entry.getKey().get() == block) {
                Block nextBlock = entry.getValue().get();
                return nextBlock.defaultBlockState().setValue(FACING, state.getValue(FACING));
            }
        }
        return null;
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if (itemAbility == ItemAbilities.AXE_STRIP) {
            if (context.getPlayer() != null && context.getPlayer().isSecondaryUseActive()) {
                for (Map.Entry<DeferredBlock<AnvilBlock>, DeferredBlock<AnvilBlock>> entry : WoodenAnvilRegistry.STRIPPING_MAP.entrySet()) {
                    if (entry.getKey().get() == state.getBlock()) {
                        Block strippedBlock = entry.getValue().get();
                        return strippedBlock.defaultBlockState().setValue(FACING, state.getValue(FACING));
                    }
                }
            }
        }
        return null;
    }
}