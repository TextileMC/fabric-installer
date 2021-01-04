/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.installer.client;

import net.fabricmc.installer.util.*;

import java.io.File;
import java.io.IOException;

public class ClientInstaller {

	public static String install(File mcDir, String gameVersion, String loaderVersion, InstallerProgress progress) throws IOException {
		System.out.println("Installing " + gameVersion + " with fabric " + loaderVersion);

		String profileName = String.format("%s-%s-%s", Reference.LOADER_NAME, loaderVersion, gameVersion);

		MinecraftLaunchJson launchJson = Utils.getLaunchMeta(loaderVersion);
		launchJson.id = profileName;

		launchJson.inheritsFrom = "inf-20100618";

		//Adds loader and the mappings
		//launchJson.libraries.add(new MinecraftLaunchJson.Library(Reference.PACKAGE.replaceAll("/", ".") + ":" + Reference.MAPPINGS_NAME + ":" + gameVersion, "https://maven.concern.i.ng/"));
		launchJson.minecraftArguments += " --fabric.game.version inf-20100618";
		launchJson.libraries.add(new MinecraftLaunchJson.Library("net.textilemc:intermediary:inf-20100618-v2", "https://maven.concern.i.ng/"));
		launchJson.libraries.add(new MinecraftLaunchJson.Library("net.textilemc:fabric-loader:0.10.8+local", "https://dl.bintray.com/pheonixvx/textile-loader/"));

		// Extra libraries
		launchJson.libraries.add(new MinecraftLaunchJson.Library("org.apache.logging.log4j:log4j-api:2.8.1", "https://libraries.minecraft.net/"));
		launchJson.libraries.add(new MinecraftLaunchJson.Library("org.apache.logging.log4j:log4j-core:2.8.1", "https://libraries.minecraft.net/"));
		String gsonHack = "com.";
		String gsonHack2 ="google.code.gson:gson:2.8.0";
		launchJson.libraries.add(new MinecraftLaunchJson.Library(gsonHack + gsonHack2, "https://libraries.minecraft.net/"));

		File versionsDir = new File(mcDir, "versions");
		File profileDir = new File(versionsDir, profileName);
		File profileJson = new File(profileDir, profileName + ".json");

		if (!profileDir.exists()) {
			profileDir.mkdirs();
		}

		/*

		This is a fun meme

		The vanilla launcher assumes the profile name is the same name as a maven artifact, how ever our profile name is a combination of 2
		(mappings and loader). The launcher will also accept any jar with the same name as the profile, it doesnt care if its empty

		 */
		File dummyJar = new File(profileDir, profileName + ".jar");
		dummyJar.createNewFile();

		Utils.writeToFile(profileJson, Utils.GSON.toJson(launchJson));

		progress.updateProgress(Utils.BUNDLE.getString("progress.done"));

		return profileName;
	}
}
