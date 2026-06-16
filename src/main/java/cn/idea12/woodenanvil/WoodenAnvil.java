package cn.idea12.woodenanvil;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

@Mod(WoodenAnvil.MODID)
public class WoodenAnvil {
    public static final String MODID = "woodenanvil";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM,MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB,MODID);

    public static final List<DeferredBlock<AnvilBlock>> WOODEN_ANVIL_BLOCKS = new ArrayList<>();

    private static DeferredBlock<AnvilBlock> OAK_ANVIL;

    public static final Map<DeferredBlock<AnvilBlock>, DeferredBlock<AnvilBlock>> STRIPPING_MAP = new IdentityHashMap<>();
    public static final Map<DeferredBlock<AnvilBlock>, DeferredBlock<AnvilBlock>> DAMAGE_MAP = new IdentityHashMap<>();

    static {
        registerWoodenAnvils();
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WOODEN_ANVIL_TAB =
            CREATIVE_MODE_TABS.register("wooden_anvils", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.woodenanvil"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> new ItemStack(OAK_ANVIL.get().asItem()))
                    .displayItems((parameters, output) -> {
                        for (DeferredBlock<AnvilBlock> deferredBlock : WOODEN_ANVIL_BLOCKS) {
                            Block block = deferredBlock.get();
                            if (block != null) {
                                output.accept(block.asItem());
                            }
                        }
                    }).build());

    public WoodenAnvil(IEventBus modEventBus, ModContainer modContainer) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private static final int WOODEN_ANVIL_BURN_TIME = 3200;
    @SubscribeEvent
    public void onFurnaceFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        ItemStack stack = event.getItemStack();
        if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof WoodenAnvilBlock) {
                event.setBurnTime(WOODEN_ANVIL_BURN_TIME);
            }
        }
    }

    private static void registerWoodenAnvils() {
        String[] woodIds = {
                "oak_log", "spruce_log", "birch_log", "jungle_log", "acacia_log",
                "dark_oak_log", "mangrove_log", "cherry_log", "pale_oak_log",
                "crimson_stem", "warped_stem", "bamboo"
        };

        String[] strippedWoodIds = {
                "stripped_oak_log", "stripped_spruce_log", "stripped_birch_log",
                "stripped_jungle_log", "stripped_acacia_log", "stripped_dark_oak_log",
                "stripped_mangrove_log", "stripped_cherry_log", "stripped_pale_oak_log",
                "stripped_crimson_stem", "stripped_warped_stem", "stripped_bamboo"
        };

        String[] stateSuffixes = {"_anvil", "_chipped_anvil", "_damaged_anvil"};

        @SuppressWarnings("unchecked")
        DeferredBlock<AnvilBlock>[][][] blocksByState = new DeferredBlock[3][2][woodIds.length];

        for (int si = 0; si < stateSuffixes.length; si++) {
            String suffix = stateSuffixes[si];

            // 未去皮（12 种木头）
            for (int i = 0; i < woodIds.length; i++) {
                DeferredBlock<AnvilBlock> block = registerAnvil(woodIds[i] + suffix);
                blocksByState[si][0][i] = block;
                if (woodIds[i].equals("oak_log") && suffix.equals("_anvil")) {
                    OAK_ANVIL = block;
                }
            }

            // 去皮（12 种木头）
            for (int i = 0; i < strippedWoodIds.length; i++) {
                DeferredBlock<AnvilBlock> block = registerAnvil(strippedWoodIds[i] + suffix);
                blocksByState[si][1][i] = block;
            }

            // 构建该状态下的去皮映射
            for (int i = 0; i < woodIds.length; i++) {
                STRIPPING_MAP.put(blocksByState[si][0][i], blocksByState[si][1][i]);
            }
        }

        // 构建损坏映射：_anvil → _chipped_anvil, _chipped_anvil → _damaged_anvil
        for (int si = 0; si < 2; si++) {
            for (int stripped = 0; stripped < 2; stripped++) {
                for (int i = 0; i < woodIds.length; i++) {
                    DAMAGE_MAP.put(blocksByState[si][stripped][i], blocksByState[si + 1][stripped][i]);
                }
            }
        }
    }

    private static DeferredBlock<AnvilBlock> registerAnvil(String name) {
        Identifier blockId = Identifier.fromNamespaceAndPath(MODID, name);
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, blockId);
        MutableComponent displayName = Component.translatable("block." + MODID + "." + name);

        // ① 注册方块
        DeferredBlock<AnvilBlock> block = BLOCKS.register(name, () -> new WoodenAnvilBlock(
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.WOOD)
                        .strength(3.0F, 3.0F)
                        .sound(SoundType.WOOD)
                        .setId(blockKey),
                displayName
        ));

        // ② 同时注册对应的物品（常规写法！）
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, blockId);
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().setId(itemKey)));

        // ③ 存入方块列表（供创造栏使用）
        WOODEN_ANVIL_BLOCKS.add(block);
        return block;
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Wooden Anvil loaded!");
    }

}