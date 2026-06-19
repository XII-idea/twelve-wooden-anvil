// This file is licensed under the MIT License.

package cn.idea12.woodenanvil;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class WoodenAnvilRegistry {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(WoodenAnvil.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, WoodenAnvil.MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WoodenAnvil.MODID);

    public static final List<DeferredBlock<AnvilBlock>> WOODEN_ANVIL_BLOCKS = new ArrayList<>(); //存砧对象
    public static final Map<DeferredBlock<AnvilBlock>, DeferredBlock<AnvilBlock>> STRIPPING_MAP = new IdentityHashMap<>();  //去皮映射
    public static final Map<DeferredBlock<AnvilBlock>, DeferredBlock<AnvilBlock>> DAMAGE_MAP = new IdentityHashMap<>();  //损坏映射

    public static DeferredBlock<AnvilBlock> OAK_ANVIL;
    public static DeferredHolder<CreativeModeTab, CreativeModeTab> WOODEN_ANVIL_TAB;

    static {
        registerAllAnvils();
        WOODEN_ANVIL_TAB = TABS.register("wooden_anvils", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.woodenanvil"))
                .icon(() -> new ItemStack(OAK_ANVIL.get().asItem()))
                .displayItems((parameters, output) -> {
                    for (DeferredBlock<AnvilBlock> deferredBlock : WOODEN_ANVIL_BLOCKS) { //添加砧对象
                        Block block = deferredBlock.get();
                        if (block != null) {
                            output.accept(block.asItem());
                        }
                    }
                }).build());
    }

    private static void registerAllAnvils() {
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
        DeferredBlock<AnvilBlock>[][][] blocksByState = new DeferredBlock[3][2][woodIds.length]; //blocksByState[损伤状态][是否去皮][木头索引]

        for (int si = 0; si < stateSuffixes.length; si++) {
            String suffix = stateSuffixes[si];

            // 未去皮
            for (int i = 0; i < woodIds.length; i++) {
                DeferredBlock<AnvilBlock> block = registerAnvil(woodIds[i] + suffix);
                blocksByState[si][0][i] = block;
                if (woodIds[i].equals("oak_log") && suffix.equals("_anvil")) {
                    OAK_ANVIL = block;
                }
            }

            // 去皮
            for (int i = 0; i < strippedWoodIds.length; i++) {
                DeferredBlock<AnvilBlock> block = registerAnvil(strippedWoodIds[i] + suffix);
                blocksByState[si][1][i] = block;
            }

            // 构建该状态下的去皮映射
            for (int i = 0; i < woodIds.length; i++) {
                STRIPPING_MAP.put(blocksByState[si][0][i], blocksByState[si][1][i]);
            }
        }

        // 构建损坏映射
        for (int si = 0; si < 2; si++) {
            for (int stripped = 0; stripped < 2; stripped++) {
                for (int i = 0; i < woodIds.length; i++) {
                    DAMAGE_MAP.put(blocksByState[si][stripped][i], blocksByState[si + 1][stripped][i]);
                }
            }
        }
    }

    private static DeferredBlock<AnvilBlock> registerAnvil(String name) {

        Identifier blockId = Identifier.fromNamespaceAndPath(WoodenAnvil.MODID, name);
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, blockId);
        MutableComponent displayName = Component.translatable("block." + WoodenAnvil.MODID + "." + name);
        DeferredBlock<AnvilBlock> block = BLOCKS.register(name, () -> new WoodenAnvilBlock(
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.WOOD)
                        .strength(3.0F, 3.0F)
                        .sound(SoundType.WOOD)
                        .setId(blockKey),
                displayName
        ));

        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, blockId);
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().setId(itemKey)));

        WOODEN_ANVIL_BLOCKS.add(block);
        return block;
    }
}