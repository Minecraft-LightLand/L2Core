package dev.xkmc.l2core.init;

import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;

public class L2TagGen {

	public static final ProviderType<RegistrateTagsProvider.IntrinsicImpl<MobEffect>> EFF_TAGS =
			ProviderType.register("tags/mob_effect",
					type -> (p, e) -> new RegistrateTagsProvider.IntrinsicImpl<>(p, type, "mob_effects",
							e.getGenerator().getPackOutput(), Registries.MOB_EFFECT, e.getLookupProvider(),
							ench -> ResourceKey.create(Registries.MOB_EFFECT, BuiltInRegistries.MOB_EFFECT.getKey(ench)),
							e.getExistingFileHelper()));


	public static final TagKey<MobEffect> TRACKED_EFFECTS = effectTag(ResourceLocation.fromNamespaceAndPath(L2Core.MODID, "tracked_effects"));

	public static void onEffectTagGen(RegistrateTagsProvider.IntrinsicImpl<MobEffect> pvd) {
		pvd.addTag(TRACKED_EFFECTS);
	}

	public static TagKey<MobEffect> effectTag(ResourceLocation id) {
		return TagKey.create(Registries.MOB_EFFECT, id);
	}

}
