package dev.xkmc.l2core.compat.patchouli;

import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.DataIngredient;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import dev.xkmc.l2core.serial.recipe.ConditionalRecipeWrapper;
import net.minecraft.Util;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.client.model.generators.ModelFile;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class PatchouliHelper {

	public static final ProviderType<PatchouliProvider> PATCHOULI = ProviderType.register("patchouli", (p, e) -> new PatchouliProvider(p, e.getLookupProvider(), e.getGenerator()));

	public static ItemStack getBook(ResourceLocation book) {
		return ItemStack.EMPTY; // TODO ItemModBook.forBook(book);
	}

	public static LootTable.Builder getBookLoot(ResourceLocation book) {
		CompoundTag tag = new CompoundTag();
		tag.putString("patchouli:book", book.toString());
		return LootTable.lootTable().withPool(
				LootPool.lootPool()//TODO .add(LootItem.lootTableItem(PatchouliItems.BOOK).apply(SetNbtFunction.setTag(tag)))
		);
	}

	private final L2Registrate reg;
	private final ResourceLocation book;

	private ResourceLocation model;

	public PatchouliHelper(L2Registrate reg, String name) {
		this.reg = reg;
		book = ResourceLocation.fromNamespaceAndPath(reg.getModid(), name);
	}

	public PatchouliHelper buildModel() {
		return buildModel("book");
	}

	public PatchouliHelper buildModel(String path) {
		model = ResourceLocation.fromNamespaceAndPath(reg.getModid(), path);
		reg.addDataGenerator(ProviderType.ITEM_MODEL, pvd -> pvd.getBuilder(path)
				.parent(new ModelFile.UncheckedModelFile("item/generated"))
				.texture("layer0", "item/" + path));
		return this;
	}

	/* TODO
	public PatchouliHelper buildShapelessRecipe(Consumer<ShapelessPatchouliBuilder> cons, Supplier<Item> unlock) {
		return buildRecipe(() -> Util.make(new ShapelessPatchouliBuilder(book), cons), unlock);
	}

	public PatchouliHelper buildShapedRecipe(Consumer<ShapedPatchouliBuilder> cons, Supplier<Item> unlock) {
		return buildRecipe(() -> Util.make(new ShapedPatchouliBuilder(book), cons), unlock);
	}
	 */

	private PatchouliHelper buildRecipe(Supplier<RecipeBuilder> cons, Supplier<Item> unlock) {
		reg.addDataGenerator(ProviderType.RECIPE, pvd -> {
			var builder = cons.get();
			builder.unlockedBy("has_" + pvd.safeName(unlock.get()),
					DataIngredient.items(unlock.get()).getCriterion(pvd));
			builder.save(ConditionalRecipeWrapper.mod(pvd, "patchouli"),
					ResourceLocation.fromNamespaceAndPath(reg.getModid(), "book"));
		});
		return this;
	}

	public PatchouliHelper buildBook(String title, String landing, int ver, ResourceKey<CreativeModeTab> tab) {
		if (model == null) {
			throw new IllegalStateException("Patchouli Book must have a model first");
		}
		String titleId = "patchouli." + reg.getModid() + ".title";
		String descId = "patchouli." + reg.getModid() + ".landing";
		reg.addRawLang(titleId, title);
		reg.addRawLang(descId, landing);
		reg.addDataGenerator(PATCHOULI, pvd -> pvd.accept(reg.getModid() + "/patchouli_books/" + book.getPath() + "/book",
				new BookEntry(titleId, descId, ver, model, tab.location(), true)));
		return this;
	}

	public record BookEntry(String name, String landing_text, int version,
							ResourceLocation model, ResourceLocation creative_tab,
							boolean use_resource_pack) {
	}

}
