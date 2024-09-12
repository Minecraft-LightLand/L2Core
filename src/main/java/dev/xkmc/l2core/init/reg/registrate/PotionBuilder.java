package dev.xkmc.l2core.init.reg.registrate;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class PotionBuilder {

	public static class TabHolder {

		private final ListMultimap<String, Holder<Potion>> potions;
		private final String modid;

		public TabHolder(String modid) {
			potions = Multimaps.newListMultimap(new LinkedHashMap<>(), ArrayList::new);
			this.modid = modid;
		}

		public Stream<Holder<Potion>> stream() {
			List<String> ans = new ArrayList<>();
			ans.add(modid);
			var set = new TreeSet<>(potions.keySet());
			set.remove(modid);
			ans.addAll(set);
			return ans.stream().flatMap(e -> potions.get(e).stream());
		}

		public synchronized void add(String modid, SimpleEntry<Potion> ans) {
			potions.put(modid, ans);
		}

	}

	private final List<Consumer<PotionBrewing.Builder>> recipes = new ArrayList<>();
	private final TabHolder tab;

	private final L2Registrate reg;

	private PotionBuilder(L2Registrate reg, TabHolder tab) {
		this.reg = reg;
		this.tab = tab;
		NeoForge.EVENT_BUS.addListener(this::registerBrewingRecipe);
	}

	public PotionBuilder(L2Registrate reg, PotionBuilder parent) {
		this(reg, parent.tab);
	}


	public PotionBuilder(L2Registrate reg) {
		this(reg, new TabHolder(reg.getModid()));
	}

	private void registerBrewingRecipe(RegisterBrewingRecipesEvent event) {
		var builder = event.getBuilder();
		recipes.forEach(e -> e.accept(builder));
	}

	public void addMix(Holder<Potion> source, ItemLike item, Holder<Potion> potion) {
		recipes.add(e -> e.addMix(source, item.asItem(), potion));
	}


	public Holder<Potion> regPotion(String id, String name, Holder<MobEffect> sup, int dur, int amp) {
		var ans = reg.potion(id, RegistrateLangProvider.toEnglishName(name), () -> new Potion(new MobEffectInstance(sup, dur, amp)));
		tab.add(reg.getModid(), ans);
		return ans;
	}

	public Holder<Potion> regPotion(String id, String nameId, Holder<MobEffect> sup, Holder<Potion> source, ItemLike item, int dur, int amp) {
		var potion = regPotion(id, nameId, sup, dur, amp);
		addMix(source, item, potion);
		return potion;
	}

	public void regPotion2(String id, Holder<MobEffect> sup, ItemLike item, int dur, int durLong) {
		var potion = regPotion(id, id, sup, Potions.AWKWARD, item, dur, 0);
		regPotion("long_" + id, id, sup, potion, Items.REDSTONE, durLong, 0);
	}

	public void regPotion3(String id, Holder<MobEffect> sup, ItemLike item, int durStrong, int dur, int durLong, int amp, int ampStrong) {
		var potion = regPotion(id, id, sup, Potions.AWKWARD, item, dur, amp);
		regPotion("long_" + id, id, sup, potion, Items.REDSTONE, durLong, amp);
		regPotion("strong_" + id, id, sup, potion, Items.GLOWSTONE_DUST, durStrong, ampStrong);
	}

	public void regPotion3(String id, Holder<MobEffect> sup, ItemLike item, int durStrong, int dur, int durLong, int amp, int ampStrong, ItemLike longItem, ItemLike strongItem) {
		var potion = regPotion(id, id, sup, Potions.AWKWARD, item, dur, amp);
		regPotion("long_" + id, id, sup, potion, longItem, durLong, amp);
		regPotion("strong_" + id, id, sup, potion, strongItem, durStrong, ampStrong);
	}

	public void interleave(String id, Holder<MobEffect> sup, int durStrong, int dur, int durLong, int amp, int ampStrong,
						   ItemLike a, Holder<Potion> ap, @Nullable Holder<Potion> lap, @Nullable Holder<Potion> sap,
						   ItemLike b, Holder<Potion> bp, @Nullable Holder<Potion> lbp, @Nullable Holder<Potion> sbp) {
		var potion = regPotion(id, id, sup, dur, amp);
		var longPotion = regPotion("long_" + id, id, sup, potion, Items.REDSTONE, durLong, amp);
		var strongPotion = regPotion("strong_" + id, id, sup, potion, Items.GLOWSTONE_DUST, durStrong, ampStrong);
		addMix(ap, a, potion);
		addMix(bp, b, potion);
		if (lap != null) addMix(lap, a, longPotion);
		if (lbp != null) addMix(lbp, b, longPotion);
		if (sap != null) addMix(sap, a, strongPotion);
		if (sbp != null) addMix(sbp, b, strongPotion);
	}

	public void regTab(ResourceKey<CreativeModeTab> key) {
		regTab(key, Items.POTION);
		regTab(key, Items.SPLASH_POTION);
		regTab(key, Items.LINGERING_POTION);
	}

	private void regTab(ResourceKey<CreativeModeTab> key, Item potion) {
		reg.modifyCreativeModeTab(key, m -> tab.stream().forEach(e ->
				m.accept(PotionContents.createItemStack(potion, e),
						CreativeModeTab.TabVisibility.PARENT_TAB_ONLY)));
	}

}
