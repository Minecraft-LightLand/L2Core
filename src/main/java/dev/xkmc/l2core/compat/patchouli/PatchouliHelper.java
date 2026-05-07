package dev.xkmc.l2core.compat.patchouli;

public class PatchouliHelper {
/*
	public static final ProviderType<PatchouliProvider> PATCHOULI = ProviderType.registerServerData("patchouli", PatchouliProvider::new);

	public static ItemStackTemplate getBook(Identifier book) {
		return ItemModBook.forBook(book);
	}

	public static LootTable.Builder getBookLoot(Identifier book) {
		return LootTable.lootTable().withPool(
				LootPool.lootPool().add(LootItem.lootTableItem(PatchouliItems.BOOK)
						.apply(SetComponentsFunction.setComponent(PatchouliDataComponents.BOOK, book)))
		);
	}

	private final L2Registrate reg;
	public final Identifier book;
	public final RewardBuilder reward;

	private Identifier model;

	public PatchouliHelper(L2Registrate reg, String name) {
		this.reg = reg;
		book = reg.loc(name);
		reward = new RewardBuilder(reg, 0, ResourceKey.create(Registries.LOOT_TABLE, book), () -> PatchouliHelper.getBookLoot(book));
	}

	public PatchouliHelper buildModel() {
		return buildModel("book");
	}

	public PatchouliHelper buildModel(String path) {
		model = Identifier.fromNamespaceAndPath(reg.getModid(), path);
		reg.addDataGenerator(ProviderType.ITEM_MODEL, pvd -> pvd.getBuilder(path)
				.parent(new ModelFile.UncheckedModelFile("item/generated"))
				.texture("layer0", "item/" + path));
		return this;
	}

	private PatchouliHelper buildRecipe(Supplier<RecipeBuilder> cons, Supplier<Item> unlock) {
		reg.addDataGenerator(ProviderType.RECIPE, pvd -> {
			var builder = cons.get();
			builder.unlockedBy("has_" + pvd.safeName(unlock.get()),
					DataIngredient.items(unlock.get()).getCriterion(pvd));
			builder.save(ConditionalRecipeWrapper.mod(pvd, "patchouli"),
					Identifier.fromNamespaceAndPath(reg.getModid(), "book"));
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
				new BookEntry(titleId, descId, ver, model, tab.identifier(), true)));
		return this;
	}

	public record BookEntry(String name, String landing_text, int version,
							Identifier model, Identifier creative_tab,
							boolean use_resource_pack) {
	}*/

}
