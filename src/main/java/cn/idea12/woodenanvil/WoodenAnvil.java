// This file is licensed under the MIT License.

package cn.idea12.woodenanvil;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(WoodenAnvil.MODID)
public class WoodenAnvil {
    public static final String MODID = "woodenanvil";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WoodenAnvil(IEventBus modEventBus, ModContainer modContainer) {
        // 注册所有注册表
        WoodenAnvilRegistry.BLOCKS.register(modEventBus);
        WoodenAnvilRegistry.ITEMS.register(modEventBus);
        WoodenAnvilRegistry.TABS.register(modEventBus);

        // 注册配置文件
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}