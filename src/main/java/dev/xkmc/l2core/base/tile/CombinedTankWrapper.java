package dev.xkmc.l2core.base.tile;

import com.mojang.datafixers.util.Pair;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.EmptyFluidHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * from Create
 */
public class CombinedTankWrapper implements IFluidHandler {

	public enum Type {
		INSERT, EXTRACT, ALL
	}

	private final List<Pair<IFluidHandler, Type>> list = new ArrayList<>();

	protected int[] baseIndex;
	protected int tankCount;
	protected boolean enforceVariety;

	public CombinedTankWrapper build() {
		this.baseIndex = new int[list.size()];
		int index = 0;
		for (int i = 0; i < list.size(); i++) {
			index += list.get(i).getFirst().getTanks();
			baseIndex[i] = index;
		}
		this.tankCount = index;
		return this;
	}

	public CombinedTankWrapper add(Type type, IFluidHandler... handlers) {
		for (IFluidHandler handler : handlers) {
			list.add(Pair.of(handler, type));
		}
		return this;
	}


	protected Iterable<IFluidHandler> fillable() {
		return list.stream().filter(e -> e.getSecond() != Type.EXTRACT).map(Pair::getFirst).toList();
	}

	protected Iterable<IFluidHandler> drainable() {
		return list.stream().filter(e -> e.getSecond() != Type.INSERT).map(Pair::getFirst).toList();
	}

	public CombinedTankWrapper enforceVariety() {
		enforceVariety = true;
		return this;
	}

	@Override
	public int getTanks() {
		return tankCount;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		int index = getIndexForSlot(tank);
		IFluidHandler handler = getHandlerFromIndex(index);
		tank = getSlotFromIndex(tank, index);
		return handler.getFluidInTank(tank);
	}

	@Override
	public int getTankCapacity(int tank) {
		int index = getIndexForSlot(tank);
		IFluidHandler handler = getHandlerFromIndex(index);
		int localSlot = getSlotFromIndex(tank, index);
		return handler.getTankCapacity(localSlot);
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		int index = getIndexForSlot(tank);
		IFluidHandler handler = getHandlerFromIndex(index);
		int localSlot = getSlotFromIndex(tank, index);
		return handler.isFluidValid(localSlot, stack);
	}

	@Override
	public int fill(FluidStack input, FluidAction action) {
		if (input.isEmpty())
			return 0;

		int ans = 0;
		input = input.copy();

		boolean found = false;

		for (boolean searchPass : new boolean[]{true, false}) {
			for (IFluidHandler handler : fillable()) {
				for (int i = 0; i < handler.getTanks(); i++)
					if (searchPass && FluidStack.isSameFluidSameComponents(handler.getFluidInTank(i), input))
						found = true;
				if (searchPass && !found)
					continue;
				int filler = handler.fill(input, action);
				input.shrink(filler);
				ans += filler;
				if (input.isEmpty() || found || enforceVariety && filler != 0)
					return ans;
			}
		}

		return ans;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		if (resource.isEmpty())
			return resource;

		FluidStack ans = FluidStack.EMPTY;
		resource = resource.copy();

		for (IFluidHandler handler : drainable()) {
			FluidStack drained = handler.drain(resource, action);
			int amount = drained.getAmount();
			resource.shrink(amount);

			if (!drained.isEmpty() && (ans.isEmpty() || FluidStack.isSameFluidSameComponents(drained, ans)))
				ans = new FluidStack(drained.getFluidHolder(), amount + ans.getAmount(),
						drained.getComponentsPatch());
			if (resource.isEmpty())
				break;
		}

		return ans;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		FluidStack ans = FluidStack.EMPTY;

		for (IFluidHandler iFluidHandler : drainable()) {
			FluidStack drained = iFluidHandler.drain(maxDrain, action);
			int amount = drained.getAmount();
			maxDrain -= amount;

			if (!drained.isEmpty() && (ans.isEmpty() || FluidStack.isSameFluidSameComponents(drained, ans)))
				ans = new FluidStack(drained.getFluidHolder(), amount + ans.getAmount(),
						drained.getComponentsPatch());
			if (maxDrain == 0)
				break;
		}

		return ans;
	}

	protected int getIndexForSlot(int slot) {
		if (slot < 0)
			return -1;
		for (int i = 0; i < baseIndex.length; i++)
			if (slot - baseIndex[i] < 0)
				return i;
		return -1;
	}

	protected IFluidHandler getHandlerFromIndex(int index) {
		if (index < 0 || index >= list.size())
			return EmptyFluidHandler.INSTANCE;
		return list.get(index).getFirst();
	}

	protected int getSlotFromIndex(int slot, int index) {
		if (index <= 0 || index >= baseIndex.length)
			return slot;
		return slot - baseIndex[index - 1];
	}
}
