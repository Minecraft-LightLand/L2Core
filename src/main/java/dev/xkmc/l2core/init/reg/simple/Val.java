package dev.xkmc.l2core.init.reg.simple;

import java.util.function.Supplier;

public interface Val<T> extends Supplier<T> {

	T get();

}
