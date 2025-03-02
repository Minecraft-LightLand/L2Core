package dev.xkmc.l2core.serial.config;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.neoforged.fml.LogicalSide;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public class RegistrateNestedProvider implements RegistrateProvider {

	public static final ProviderType<RegistrateNestedProvider> TYPE = ProviderType.registerProvider("l2_custom", RegistrateNestedProvider::new);

	private final List<DataProvider> list = new ArrayList<>();
	private final AbstractRegistrate<?> reg;
	private final DataGenerator gen;
	private final CompletableFuture<HolderLookup.Provider> pvd;

	public RegistrateNestedProvider(ProviderType.Context<RegistrateNestedProvider> ctx) {
		this.reg = ctx.parent();
		this.gen = ctx.event().getGenerator();
		this.pvd = ctx.provider();
	}

	@Override
	public LogicalSide getSide() {
		return LogicalSide.SERVER;
	}

	public RegistrateNestedProvider add(BiFunction<DataGenerator, CompletableFuture<HolderLookup.Provider>, DataProvider> factory) {
		list.add(factory.apply(gen, pvd));
		return this;
	}

	@Override
	public CompletableFuture<?> run(CachedOutput cachedOutput) {
		reg.genData(TYPE, this);
		return CompletableFuture.allOf(list.stream().map(e -> e.run(cachedOutput)).toArray(CompletableFuture[]::new));
	}

	@Override
	public String getName() {
		return "Custom Registrate Provider";
	}

}
