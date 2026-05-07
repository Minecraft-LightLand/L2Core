package dev.xkmc.l2core.base.tile;

import dev.xkmc.l2serial.serialization.codec.AliasCollection;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public class BaseTank extends FluidStacksResourceHandler implements AliasCollection<FluidStack> {

	private final List<BaseContainerListener> listeners = new ArrayList<>();

	private Predicate<FluidResource> predicate = e -> true;
	private BooleanSupplier allowExtract = () -> true;

	private int click_max;

	public BaseTank(int size, int capacity) {
		super(size, capacity);
	}

	public BaseTank add(BaseContainerListener listener) {
		listeners.add(listener);
		return this;
	}

	public BaseTank setPredicate(Predicate<FluidResource> predicate) {
		this.predicate = predicate;
		return this;
	}

	public BaseTank setExtract(BooleanSupplier allowExtract) {
		this.allowExtract = allowExtract;
		return this;
	}

	public BaseTank setClickMax(int max) {
		this.click_max = max;
		return this;
	}

	@Override
	public int insert(FluidResource resource, int amount, TransactionContext transaction) {
		if (!predicate.test(resource)) return 0;
		return super.insert(resource, click_max <= 0 ? amount : Math.min(click_max, amount), transaction);
	}

	@Override
	public int extract(FluidResource resource, int amount, TransactionContext transaction) {
		if (!allowExtract.getAsBoolean()) return 0;
		return super.extract(resource, amount, transaction);
	}

	@Override
	protected void onContentsChanged(int index, FluidStack previousContents) {
		setChanged();
	}

	public void setChanged() {
		listeners.forEach(BaseContainerListener::notifyTile);
	}

	@Override
	public List<FluidStack> getAsList() {
		return stacks;
	}

	@Override
	public void clear() {
		stacks.clear();
		setChanged();
	}

	@Override
	public void set(int n, int i, FluidStack elem) {
		stacks.set(i, elem);
	}

	@Override
	public Class<FluidStack> getElemClass() {
		return FluidStack.class;
	}

	public boolean isEmpty() {
		for (FluidStack stack : stacks) {
			if (!stack.isEmpty())
				return false;
		}
		return true;
	}

}
