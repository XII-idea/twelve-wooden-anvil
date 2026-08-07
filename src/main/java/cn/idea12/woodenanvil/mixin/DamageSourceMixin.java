// This file is licensed under the MIT License.

package cn.idea12.woodenanvil.mixin;

import cn.idea12.woodenanvil.WoodenAnvilBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin {

    @Inject(method = "getLocalizedDeathMessage", at = @At("HEAD"), cancellable = true)
    private void onGetLocalizedDeathMessage(LivingEntity killed, CallbackInfoReturnable<Component> cir) {
        // 检查伤害源是否来自掉落中的木砧
        DamageSource self = (DamageSource) (Object) this;
        if (self.getEntity() instanceof FallingBlockEntity fallingBlock) {
            if (WoodenAnvilBlock.isWoodenAnvil(fallingBlock.getBlockState())) {
                // 获取木砧的具体名字（如"橡木砧"）
                Component blockName = fallingBlock.getBlockState().getBlock().getName();
                // 检查是否有击杀归属者（用于.player变体）
                LivingEntity attacker = killed.getKillCredit();

                if (attacker != null) {
                    cir.setReturnValue(Component.translatable(
                            "death.attack.wooden_anvil.player",
                            killed.getDisplayName(), attacker.getDisplayName(), blockName));
                } else {
                    cir.setReturnValue(Component.translatable(
                            "death.attack.wooden_anvil",
                            killed.getDisplayName(), blockName));
                }
            }
        }
    }
}
