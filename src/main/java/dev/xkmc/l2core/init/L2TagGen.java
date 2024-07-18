package dev.xkmc.l2core.init;

import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.enchantment.Enchantment;

public class L2TagGen {

	public static <T> ProviderType<RegistrateTagsProvider.IntrinsicImpl<T>> getProvider(ResourceKey<Registry<T>> id, Registry<T> reg) {
		String name = id.location().getPath();
		return ProviderType.registerIntrinsicTag("tags/" + name, name, id,
				ench -> reg.getResourceKey(ench).orElseThrow());
	}

	public static <T> ProviderType<RegistrateTagsProvider.Impl<T>> getProvider(ResourceKey<Registry<T>> id) {
		String name = id.location().getPath();
		return ProviderType.registerDynamicTag("tags/" + name, name, id);
	}

	public static final ProviderType<RegistrateTagsProvider.IntrinsicImpl<MobEffect>> EFF_TAGS =
			getProvider(Registries.MOB_EFFECT, BuiltInRegistries.MOB_EFFECT);

	public static final ProviderType<RegistrateTagsProvider.IntrinsicImpl<Attribute>> ATTR_TAGS =
			getProvider(Registries.ATTRIBUTE, BuiltInRegistries.ATTRIBUTE);

	public static final ProviderType<RegistrateTagsProvider.Impl<Enchantment>> ENCH_TAGS =
			getProvider(Registries.ENCHANTMENT);

	public static final TagKey<MobEffect> TRACKED_EFFECTS = effectTag(ResourceLocation.fromNamespaceAndPath(L2Core.MODID, "tracked_effects"));

	public static void onEffectTagGen(RegistrateTagsProvider.IntrinsicImpl<MobEffect> pvd) {
		pvd.addTag(TRACKED_EFFECTS);
	}

	public static TagKey<MobEffect> effectTag(ResourceLocation id) {
		return TagKey.create(Registries.MOB_EFFECT, id);
	}

}
