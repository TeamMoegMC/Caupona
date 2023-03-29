package com.teammoeg.caupona.datagen;

import com.teammoeg.caupona.CPFluids;
import com.teammoeg.caupona.CPMain;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.ExistingFileHelper;

public class RegistryJavaGenerator extends FileGenerator {

	public RegistryJavaGenerator(PackOutput output, ExistingFileHelper helper) {
		super(PackType.SERVER_DATA, output, helper,"Caupona Registry Java ");
	}
	
	
	@Override
	protected void gather(FileStorage reciver) {
		FileOutput fo=this.formatJava("CPStewTexture");
		
		fo.getPrint().println("import net.minecraft.resources.ResourceLocation;");
		fo.getPrint().println("import java.util.HashMap;");
		fo.getPrint().println("import java.util.Map;");
		fo.getPrint().println();
		fo.getPrint().println("import com.teammoeg.caupona.CPMain;");
		fo.getPrint().println();
		fo.getPrint().println("/**");
		fo.getPrint().println("* This file is auto generated, do not modify!");
		fo.getPrint().println("*/");
		fo.getPrint().println("public class CPStewTexture{");
		fo.getPrint().println("\tpublic static Map<String,ResourceLocation> texture=new HashMap<>();");
		fo.getPrint().println("\tstatic{");
		for(String sf:CPFluids.getSoupfluids()) {
			ResourceLocation image = new ResourceLocation(CPMain.MODID, "textures/block/soups/" + sf + ".png");
			if (helper.exists(image, PackType.CLIENT_RESOURCES)) {
				fo.getPrint().println("\t\ttexture.put(\""+sf+"\",new ResourceLocation(CPMain.MODID,\"block/soups/"+sf+"\"));");
			}
		}
		fo.getPrint().println("\t}");
		fo.getPrint().println("}");
		reciver.accept(fo);
		
	}

}
