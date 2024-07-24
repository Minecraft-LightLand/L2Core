package dev.xkmc.l2core.init.reg.ench;

import com.mojang.serialization.Codec;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2core.init.L2TagGen;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import dev.xkmc.l2core.init.reg.simple.Reg;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@SuppressWarnings("unused")
public class EnchReg {

	public static EnchReg of(Reg reg, L2Registrate pvd) {
		return new EnchReg(reg, pvd);
	}

	private final DeferredRegister<DataComponentType<?>> reg;
	private final DeferredRegister<LegacyEnchantment> legacy;
	private final L2Registrate pvd;

	private final List<EnchVal.Impl> list = new ArrayList<>();

	private EnchReg(Reg reg, L2Registrate pvd) {
		this.reg = reg.make(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE);
		this.legacy = reg.make(L2LibReg.ENCH.get());
		this.pvd = pvd;
		pvd.addDataGenerator(L2TagGen.ENCH_TAGS, this::doTagGen);
		var init = pvd.getDataGenInitializer();
		init.add(Registries.ENCHANTMENT, this::build);
		init.addDependency(L2TagGen.ENCH_TAGS, ProviderType.DYNAMIC);
	}

	private <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> reg(String id, Codec<T> codec) {
		return reg.register(id, () -> DataComponentType.<T>builder().persistent(codec).build());
	}

	public EECVal.Flag unit(String id) {
		return new EECVal.Flag.Impl<>(reg(id, Unit.CODEC));
	}

	public <T> EECVal.Special<T> special(String id, Codec<T> codec) {
		return new EECVal.Special.Impl<>(reg(id, codec.listOf()));
	}

	public <T> EECVal<T> eff(String id, Codec<T> codec, LootContextParamSet loot) {
		return new EECVal.Impl<>(reg(id, ConditionalEffect.codec(codec, loot).listOf()));
	}

	public EECVal.Num val(String id) {
		return new EECVal.Num.Impl(reg(id, ConditionalEffect.codec(
				EnchantmentValueEffect.CODEC,
				LootContextParamSets.ENCHANTED_ITEM
		).listOf()));
	}

	private <T extends EnchVal.Impl> T enchBase(String id, String name, String desc, Function<ResourceKey<Enchantment>, T> factory) {
		var key = ResourceKey.create(Registries.ENCHANTMENT, pvd.loc(id));
		pvd.addRawLang("enchantment." + pvd.getModid() + "." + id, name);
		pvd.addRawLang("enchantment." + pvd.getModid() + "." + id + ".desc", desc);
		var ans = factory.apply(key);
		list.add(ans);
		return ans;
	}

	public EnchVal ench(String id, String name, String desc, UnaryOperator<EnchVal.Builder> cons) {
		return enchBase(id, name, desc, key -> new EnchVal.Simple(key,
				Lazy.of(() -> cons.apply(new EnchVal.Builder(key.location())))));
	}

	public EnchVal.Flag enchFlag(String id, String name, String desc, UnaryOperator<EnchVal.Builder> cons) {
		var unit = unit(id);
		return enchBase(id, name, desc, key -> new EnchVal.FlagImpl(unit, key,
				Lazy.of(() -> cons.apply(new EnchVal.Builder(key.location()).effect(e ->
						e.withEffect(unit.get()))))));
	}

	public EnchVal enchLegacy(String id, String name, String desc, UnaryOperator<EnchVal.Builder> cons, Supplier<LegacyEnchantment> factory) {
		var unit = legacy.register(id, factory);
		return enchBase(id, name, desc, key -> new EnchVal.Simple(key,
				Lazy.of(() -> cons.apply(new EnchVal.Builder(key.location()).effect(e ->
						e.withSpecialEffect(L2LibReg.LEGACY.get(), List.of(unit.get())))))));
	}

	public void build(BootstrapContext<Enchantment> ctx) {
		for (var e : list) ctx.register(e.id(), e.builder().get().build(ctx, e.id().location()));
	}

	public void doTagGen(RegistrateTagsProvider<Enchantment> pvd) {
		for (var e : list) {
			for (var t : e.builder().get().tags) {
				pvd.addTag(t).add(e.id());
			}
		}
	}

}
