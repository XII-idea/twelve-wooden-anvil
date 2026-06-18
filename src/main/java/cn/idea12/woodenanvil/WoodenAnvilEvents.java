// This file is licensed under the MIT License.

package cn.idea12.woodenanvil;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@EventBusSubscriber(modid = WoodenAnvil.MODID)
public class WoodenAnvilEvents {
    private static final int WOODEN_ANVIL_BURN_TIME = 3200;

    @SubscribeEvent
    public static void onFurnaceFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        ItemStack stack = event.getItemStack();
        if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof WoodenAnvilBlock) {
                event.setBurnTime(WOODEN_ANVIL_BURN_TIME);
            }
        }
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        WoodenAnvil.LOGGER.info("Wooden Anvil loaded!");
    }
}