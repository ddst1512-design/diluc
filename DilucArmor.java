package com.superapp.dilucarmor;

import java.util.EnumMap;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(DilucArmor.MOD_ID)
public final class DilucArmor {
    public static final String MOD_ID = "dilucarmor";
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredRegister<ArmorMaterial> MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, MOD_ID);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> DILUC_MATERIAL =
            MATERIALS.register("diluc", () -> {
                EnumMap<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
                defense.put(ArmorItem.Type.HELMET, 3);
                defense.put(ArmorItem.Type.CHESTPLATE, 8);
                defense.put(ArmorItem.Type.LEGGINGS, 6);
                defense.put(ArmorItem.Type.BOOTS, 3);
                defense.put(ArmorItem.Type.BODY, 8);
                return new ArmorMaterial(defense, 18, SoundEvents.ARMOR_EQUIP_LEATHER,
                        () -> Ingredient.of(Items.LEATHER),
                        List.of(new ArmorMaterial.Layer(id("diluc"))), 2.0F, 0.0F);
            });

    public static final DeferredItem<ArmorItem> CRIMSON_CREST = armor("crimson_crest", ArmorItem.Type.HELMET);
    public static final DeferredItem<ArmorItem> DILUC_COAT = armor("diluc_coat", ArmorItem.Type.CHESTPLATE);
    public static final DeferredItem<ArmorItem> DILUC_TROUSERS = armor("diluc_trousers", ArmorItem.Type.LEGGINGS);
    public static final DeferredItem<ArmorItem> DILUC_BOOTS = armor("diluc_boots", ArmorItem.Type.BOOTS);

    public static final DeferredHolder<EntityType<?>, EntityType<FlameProjectile>> FIREBALL =
            ENTITIES.register("searing_fireball", () -> EntityType.Builder
                    .<FlameProjectile>of(FlameProjectile::new, MobCategory.MISC)
                    .sized(0.35F, 0.35F).clientTrackingRange(8).updateInterval(1)
                    .build(MOD_ID + ":searing_fireball"));
    public static final DeferredHolder<EntityType<?>, EntityType<FlameProjectile>> PHOENIX =
            ENTITIES.register("dawn_phoenix", () -> EntityType.Builder
                    .<FlameProjectile>of(FlameProjectile::new, MobCategory.MISC)
                    .sized(1.2F, 1.2F).clientTrackingRange(12).updateInterval(1)
                    .build(MOD_ID + ":dawn_phoenix"));

    public DilucArmor(IEventBus bus) {
        MATERIALS.register(bus);
        ITEMS.register(bus);
        ENTITIES.register(bus);
        bus.addListener(this::creativeTab);
        bus.addListener(ModNetwork::register);
        NeoForge.EVENT_BUS.addListener(Abilities::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(Abilities::onClone);
    }

    private static DeferredItem<ArmorItem> armor(String name, ArmorItem.Type type) {
        return ITEMS.register(name, () -> new ArmorItem(DILUC_MATERIAL, type,
                new Item.Properties().durability(type.getDurability(25))));
    }

    private void creativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(CRIMSON_CREST);
            event.accept(DILUC_COAT);
            event.accept(DILUC_TROUSERS);
            event.accept(DILUC_BOOTS);
        }
    }

    public static boolean hasFullSet(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(CRIMSON_CREST.get())
                && player.getItemBySlot(EquipmentSlot.CHEST).is(DILUC_COAT.get())
                && player.getItemBySlot(EquipmentSlot.LEGS).is(DILUC_TROUSERS.get())
                && player.getItemBySlot(EquipmentSlot.FEET).is(DILUC_BOOTS.get());
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
