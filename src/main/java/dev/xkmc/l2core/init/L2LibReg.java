package dev.xkmc.l2core.init;

import dev.xkmc.l2core.base.effects.ClientEffectCap;
import dev.xkmc.l2core.base.menu.base.MenuLayoutConfig;
import dev.xkmc.l2core.capability.conditionals.ConditionalData;
import dev.xkmc.l2core.capability.conditionals.PlayerFlagData;
import dev.xkmc.l2core.capability.player.PlayerCapabilityNetworkHandler;
import dev.xkmc.l2core.init.reg.datapack.DatapackReg;
import dev.xkmc.l2core.init.reg.ench.EECVal;
import dev.xkmc.l2core.init.reg.ench.EnchReg;
import dev.xkmc.l2core.init.reg.ench.LegacyEnchantment;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate;
import dev.xkmc.l2core.init.reg.simple.*;
import dev.xkmc.l2core.serial.configval.*;
import dev.xkmc.l2core.serial.ingredients.EnchantmentIngredient;
import dev.xkmc.l2core.serial.ingredients.PotionIngredient;
import dev.xkmc.l2core.serial.loot.AddItemModifier;
import dev.xkmc.l2core.serial.loot.AddLootTableModifier;
import dev.xkmc.l2core.serial.loot.PlayerFlagCondition;
import dev.xkmc.l2serial.serialization.codec.MapCodecAdaptor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class L2LibReg {

	public static final Reg REG = new Reg(L2Core.MODID);

	// ingredients
	public static final IngReg INGREDIENT = IngReg.of(REG);
	public static final IngVal<EnchantmentIngredient> ING_ENCH = INGREDIENT.reg("enchantment", EnchantmentIngredient.class);
	public static final IngVal<PotionIngredient> ING_POTION = INGREDIENT.reg("potion", PotionIngredient.class);

	// conditions
	public static final CdcReg<ICondition> CONDITION = CdcReg.of(REG, NeoForgeRegistries.CONDITION_SERIALIZERS);
	public static final CdcVal<BooleanValueCondition> CONDITION_BOOL = CONDITION.reg("bool_config", BooleanValueCondition.class);
	public static final CdcVal<IntValueCondition> CONDITION_INT = CONDITION.reg("int_config", IntValueCondition.class);
	public static final CdcVal<DoubleValueCondition> CONDITION_DOUBLE = CONDITION.reg("double_config", DoubleValueCondition.class);
	public static final CdcVal<StringValueCondition> CONDITION_STR = CONDITION.reg("string_config", StringValueCondition.class);
	public static final CdcVal<ListStringValueCondition> CONDITION_LIST_STR = CONDITION.reg("string_list_config", ListStringValueCondition.class);

	// attachments
	public static final AttReg ATTACHMENT = AttReg.of(REG);

	public static final AttVal.CapVal<LivingEntity, ClientEffectCap> EFFECT = ATTACHMENT.entity("effect",
			ClientEffectCap.class, ClientEffectCap::new, LivingEntity.class, e -> e.level().isClientSide());
	public static final AttVal.PlayerVal<ConditionalData> CONDITIONAL = ATTACHMENT.player("conditionals",
			ConditionalData.class, ConditionalData::new, PlayerCapabilityNetworkHandler::new);
	public static final AttVal.PlayerVal<PlayerFlagData> FLAGS = ATTACHMENT.player("flags",
			PlayerFlagData.class, PlayerFlagData::new, PlayerCapabilityNetworkHandler::new);

	// loot modifiers
	public static final CdcReg<IGlobalLootModifier> GLM = CdcReg.of(REG, NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS);
	public static final CdcVal<AddItemModifier> ADD_ITEM = GLM.reg("add_item", AddItemModifier.MAP_CODEC);
	public static final CdcVal<AddLootTableModifier> ADD_TABLE = GLM.reg("add_table", AddLootTableModifier.MAP_CODEC);

	// loot conditions
	public static final SR<LootItemConditionType> LIC = SR.of(REG, BuiltInRegistries.LOOT_CONDITION_TYPE);
	public static final Val<LootItemConditionType> LIC_FLAG = LIC.reg("player_flag",
			() -> new LootItemConditionType(MapCodecAdaptor.of(PlayerFlagCondition.class)));

	// datapack
	public static final DatapackReg<MenuLayoutConfig> MENU_LAYOUT = REG.dataReg("menu_layout", MenuLayoutConfig.class);

	// enchantment
	public static final L2Registrate.RegistryInstance<LegacyEnchantment> ENCH =
			L2Core.REGISTRATE.newRegistry("legacy_enchantment", LegacyEnchantment.class, e -> e.sync(true));
	public static final EECVal.Special<LegacyEnchantment> LEGACY =
			EnchReg.of(REG, L2Core.REGISTRATE).special("legacy", ENCH.reg().byNameCodec());

	public static void register() {
	}

}
