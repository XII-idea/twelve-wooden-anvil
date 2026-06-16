package cn.idea12.woodenanvil.mixin;

import cn.idea12.woodenanvil.WoodenAnvilBlock;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilBlock.class)
public abstract class AnvilBlockMixin {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private static void onDamage(BlockState state, CallbackInfoReturnable<@Nullable BlockState> cir) {
        if (WoodenAnvilBlock.isWoodenAnvil(state)) {
            cir.setReturnValue(WoodenAnvilBlock.damageWoodenAnvil(state));
        }
    }
}