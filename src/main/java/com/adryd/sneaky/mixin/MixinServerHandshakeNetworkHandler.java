package com.adryd.sneaky.mixin;

import com.adryd.sneaky.Config;
import com.adryd.sneaky.IPList;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerHandshakeNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerHandshakeNetworkHandler.class)
public class MixinServerHandshakeNetworkHandler {

    @Shadow
    @Final
    private ClientConnection connection;

    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    @Final
    private static Text IGNORING_STATUS_REQUEST_MESSAGE;

    @Redirect(method = "onHandshake", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;acceptsStatusQuery()Z"))
    private boolean acceptsQuery(MinecraftServer instance) {
        if (this.server.acceptsStatusQuery()) {
            if (Config.INSTANCE.getDisableAllPingsUntilLogin() && !IPList.INSTANCE.canPing(this.connection.getAddress())) {
                return false;
            }
            return true;
        }
        return false;
    }
}
