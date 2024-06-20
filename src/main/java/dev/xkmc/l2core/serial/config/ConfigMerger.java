package dev.xkmc.l2core.serial.config;

import dev.xkmc.l2serial.serialization.type_cache.ClassCache;
import dev.xkmc.l2serial.serialization.type_cache.FieldCache;
import dev.xkmc.l2serial.serialization.type_cache.TypeInfo;
import dev.xkmc.l2serial.util.Wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ConfigMerger<T extends BaseConfig> {

	private final ClassCache cache;

	public ConfigMerger(Class<T> cls) {
		this.cache = ClassCache.get(cls);
	}

	public T merge(List<T> list) throws Exception {
		T ans = (T) cache.create();
		for (FieldCache field : cache.getFields()) {
			ConfigCollect collect = field.getAnnotation(ConfigCollect.class);
			if (collect == null) continue;
			switch (collect.value()) {
				case OVERWRITE -> {
					int n = list.size();
					if (n > 0) {
						field.set(ans, field.get(list.get(n - 1)));
					}
				}
				case COLLECT -> {
					TypeInfo info = field.toType();
					assert Collection.class.isAssignableFrom(info.getAsClass());
					Collection val = (Collection) info.toCache().create();
					for (T t : list) {
						val.addAll((Collection) field.get(t));
					}
					field.set(ans, val);
				}
				case MAP_COLLECT -> {
					TypeInfo info = field.toType();
					TypeInfo sub = info.getGenericType(1);
					assert Map.class.isAssignableFrom(info.getAsClass());
					Map val = (Map) info.toCache().create();

					if (Collection.class.isAssignableFrom(sub.getAsClass())) {
						for (T t : list) {
							Map map = (Map) field.get(t);
							for (Object e : map.entrySet()) {
								Map.Entry ent = (Map.Entry) e;
								Collection col;
								if (val.containsKey(ent.getKey())) {
									col = (Collection) val.get(ent.getKey());
								} else {
									val.put(ent.getKey(), col = (Collection) sub.toCache().create());
								}
								col.addAll((Collection) ent.getValue());
							}
						}
					} else if (Map.class.isAssignableFrom(sub.getAsClass())) {
						for (T t : list) {
							Map map = (Map) field.get(t);
							for (Object e : map.entrySet()) {
								Map.Entry ent = (Map.Entry) e;
								Map col;
								if (val.containsKey(ent.getKey())) {
									col = (Map) val.get(ent.getKey());
								} else {
									val.put(ent.getKey(), col = (Map) sub.toCache().create());
								}
								col.putAll((Map) ent.getValue());
							}
						}
					}
					field.set(ans, val);
				}
				case MAP_OVERWRITE -> {
					TypeInfo info = field.toType();
					assert Map.class.isAssignableFrom(info.getAsClass());
					Map val = (Map) info.toCache().create();
					for (T t : list) {
						val.putAll((Map) field.get(t));
					}
					field.set(ans, val);
				}
			}
		}
		ans.postMerge();
		return ans;
	}

	public T apply(Collection<T> s) {
		return Wrappers.get(() -> this.merge(new ArrayList<>(s)));
	}
}
