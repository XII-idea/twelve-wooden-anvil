// This file is licensed under the MIT License.

package cn.idea12.woodenanvil;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// 26.3 起燃料燃烧时间改由物品的 CookingFuel 数据组件声明（见 WoodenAnvilRegistry#registerAnvil），
// 原 FurnaceFuelBurnTimeEvent 已被 NeoForge 移除。
@EventBusSubscriber(modid = WoodenAnvil.MODID)
public class WoodenAnvilEvents {
    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        WoodenAnvil.LOGGER.info("Wooden Anvil loaded!");
    }
}