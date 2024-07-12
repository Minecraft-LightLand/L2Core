package dev.xkmc.l2core.init.reg.datapack;

import com.mojang.serialization.Codec;
import cpw.mods.util.Lazy;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import dev.xkmc.l2core.init.reg.simple.DCReg;
import dev.xkmc.l2core.init.reg.simple.DCVal;
import dev.xkmc.l2core.init.reg.simple.Reg;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class EnchReg {

	public static EnchReg of(Reg reg, L2Registrate pvd) {
		return new EnchReg(reg, pvd);
	}

	private final DeferredRegister<DataComponentType<?>> reg;
	private final L2Registrate pvd;

	private final List<EnchVal.Impl> list = new ArrayList<>();

	private EnchReg(Reg reg, L2Registrate pvd) {
		this.reg = reg.make(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE);
		this.pvd = pvd;
	}

	public <T> DCVal<T> reg(String id, Codec<T> codec) {
		return new DCReg.DCValImpl<>(reg.register(id, () -> DataComponentType.<T>builder().persistent(codec).build()));
	}

	public DCVal<Unit> unit(String id) {
		return reg(id, Unit.CODEC);
	}

	public DCVal<EnchantmentValueEffect> val(String id) {
		return reg(id, EnchantmentValueEffect.CODEC);
	}

	public EnchVal ench(String id, String name, String desc, UnaryOperator<EnchVal.Builder> cons) {
		var key = ResourceKey.create(Registries.ENCHANTMENT, pvd.loc(id));
		pvd.addRawLang("enchantment." + pvd.getModid() + "." + id, name);
		pvd.addRawLang("enchantment." + pvd.getModid() + "." + id + ".desc", desc);
		var ans = new EnchVal.Simple(key, Lazy.of(() -> cons.apply(new EnchVal.Builder())));
		list.add(ans);
		return ans;
	}

	public EnchVal.Flag enchFlag(String id, String name, String desc, UnaryOperator<EnchVal.Builder> cons) {
		var key = ResourceKey.create(Registries.ENCHANTMENT, pvd.loc(id));
		pvd.addRawLang("enchantment." + pvd.getModid() + "." + id, name);
		pvd.addRawLang("enchantment." + pvd.getModid() + "." + id + ".desc", desc);
		var unit = unit(id);
		var ans = new EnchVal.FlagImpl(unit, key, Lazy.of(() -> cons.apply(new EnchVal.Builder().effect(e -> e.withEffect(unit.get())))));
		list.add(ans);
		return ans;
	}

	public void build(BootstrapContext<Enchantment> ctx) {
		for (var e : list) ctx.register(e.id(), e.builder().get().build(ctx, e.id().location()));
	}

	public void doTagGen(Function<TagKey<Enchantment>, TagsProvider.TagAppender<Enchantment>> func, HolderLookup.Provider pvd) {
		for (var e : list) {
			for (var t : e.builder().get().tags) {
				func.apply(t).add(e.id());
			}
		}
	}

}
