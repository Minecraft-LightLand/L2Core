package dev.xkmc.l2core.util;

import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;

public class MathHelper {

	public static double horSq(Vec3 vec3) {
		return vec3.x * vec3.x + vec3.z * vec3.z;
	}

	public static UUID getUUIDFromString(String str) {
		int hash = str.hashCode();
		Random r = new Random(hash);
		long l0 = r.nextLong();
		long l1 = r.nextLong();
		return new UUID(l0, l1);
	}

	@SafeVarargs
	public static <T> T[] merge(T[] arr, T... vals) {
		var ans = Arrays.copyOf(arr, arr.length + vals.length);
		System.arraycopy(vals, 0, ans, arr.length, vals.length);
		return ans;
	}

	@Nullable
	public static <T> T pick(List<T> list, Function<T, Integer> func, double random) {
		int total = 0;
		for (T t : list) {
			total += func.apply(t);
		}
		double val = random * total;
		for (T t : list) {
			val -= func.apply(t);
			if (val < 0) {
				return t;
			}
		}
		return null;
	}


}
