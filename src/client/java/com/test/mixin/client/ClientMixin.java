package com.test.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.profile.*;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;

// @Mixin(MinecraftClient.class)
// public class ExampleClientMixin {
// 	@Inject(at = @At("HEAD"), method = "run")
// 	private void run(CallbackInfo info) {
		
// 		// This code is injected into the start of MinecraftClient.run()V
// 	}
// }
class PlayerData{
    public String id;
    public String name;
}


@Mixin(GameProfile.class)
public abstract class ClientMixin{


	@Inject(at = @At("HEAD"), method = "of")
	protected void injectOfMethod(UUID uniqueId, @Nullable String name) throws MalformedURLException, IOException{
		uniqueId = getPlayerUUIDFromMojang(name);
            
	}
	@Inject(at = @At("HEAD"))	
	protected static UUID getPlayerUUIDFromMojang(String playerUsername) throws MalformedURLException, IOException{
		Gson myGson = new Gson();
            
            URL myUrl = new URL("https://api.mojang.com/users/profiles/minecraft/"+playerUsername);
            HttpsURLConnection conn = (HttpsURLConnection) myUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            
            // int status = conn.getResponseCode();
            BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine())!= null){
                content.append(inputLine);
            }
            PlayerData myPlayer = myGson.fromJson(content.toString(), PlayerData.class);
            UUID playerUUID = UUID.fromString(myPlayer.id);
            in.close();
            conn.disconnect();     

		return playerUUID;

	}

}


