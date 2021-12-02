
package net.mehvahdjukaar.dummmmmmy.common;

import net.mehvahdjukaar.dummmmmmy.DummmmmmyMod;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

public class Configs {

	public static final boolean DAMAGE_EQUIPMENT = true;
	public static final double ANIMATION_INTENSITY = 0.75;
	public static final boolean SHOW_HEARTHS = false;
	public static final boolean DYNAMIC_DPS = true;
	public static final SkinType SKIN = SkinType.DEFAULT;

	public static final int RADIUS = 12;

	public static final int DAMAGE_GENERIC = 0xffffff;
	public static final int DAMAGE_CRIT = 0xff0000;
	public static final int DAMAGE_DRAGON = 0xE600FF;
	public static final int DAMAGE_WITHER = 0x666666;
	public static final int DAMAGE_EXPLOSION = 0xFFBB29;
	public static final int DAMAGE_IND_MAGIC = 0x844CE7;
	public static final int DAMAGE_TRIDENT = 0x00FF9D;
	public static final int DAMAGE_MAGIC = 0x33B1FF;
	public static final int DAMAGE_FIRE = 0xFF7700;
	public static final int DAMAGE_LIGHTNING = 0xFFF200;
	public static final int DAMAGE_CACTUS = 0x0FA209;
	public static final int DAMAGE_TRUE = 0x910038;

	private static final Color COLOR_GENERIC = new Color(0xffffff);
	private static final Color COLOR_CRIT = new Color(0xff0000);
	private static final Color COLOR_DRAGON = new Color(0xE600FF);
	private static final Color COLOR_WITHER = new Color(0x666666);
	private static final Color COLOR_EXPLOSION = new Color(0xFFBB29);
	private static final Color COLOR_IND_MAGIC = new Color(0x844CE7);
	private static final Color COLOR_MAGIC = new Color(0x33B1FF);
	private static final Color COLOR_TRIDENT = new Color(0x00FF9D);
	private static final Color COLOR_FIRE = new Color(0xFF7700);
	private static final Color COLOR_LIGHTNING = new Color(0xFFF200);
	private static final Color COLOR_CACTUS = new Color(0x0FA209);
	private static final Color COLOR_TRUE = new Color(0x910038);


	public enum SkinType {
		DEFAULT("dummy","dummy_h"),
		ORIGINAL("dummy_1","dummy_1"),
		DUNGEONS("dummy_3","dummy_3_h"),
		ALTERNATIVE("dummy_2","dummy_2_h");

		private final ResourceLocation texture;
		private final ResourceLocation shearedTexture;

		SkinType(String name, String shearedName) {
			texture = new ResourceLocation(DummmmmmyMod.MOD_ID + ":textures/" + name + ".png");
			shearedTexture = new ResourceLocation(DummmmmmyMod.MOD_ID + ":textures/" + shearedName + ".png");
		}

		public ResourceLocation getSkin(Boolean sheared) {
			return sheared ? shearedTexture : texture;
		}
	}

}
