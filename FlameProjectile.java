package com.superapp.dilucarmor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Straight-flying projectiles; no explosions, fire blocks, or terrain griefing. */
public final class FlameProjectile extends Projectile {
    private static final ResourceKey<DamageType> DAWN_FLAME =
            ResourceKey.create(Registries.DAMAGE_TYPE, DilucArmor.id("dawn_flame"));
    private final Set<UUID> alreadyHit = new HashSet<>();

    public FlameProjectile(EntityType<? extends FlameProjectile> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    public boolean isPhoenix() { return getType() == DilucArmor.PHOENIX.get(); }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override public void tick() {
        super.tick();
        if (tickCount > (isPhoenix() ? 40 : 25)) { discard(); return; }
        Vec3 start = position();
        Vec3 end = start.add(getDeltaMovement());
        BlockHitResult wall = level().clip(new ClipContext(start, end,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (wall.getType() != HitResult.Type.MISS) end = wall.getLocation();

        if (level() instanceof ServerLevel server) {
            double radius = isPhoenix() ? 0.95D : 0.20D;
            AABB swept = getBoundingBox().expandTowards(end.subtract(start)).inflate(radius);
            List<LivingEntity> hits = new ArrayList<>();
            for (LivingEntity target : server.getEntitiesOfClass(LivingEntity.class, swept, this::validTarget)) {
                AABB box = target.getBoundingBox().inflate(radius);
                boolean touched = box.contains(start) || box.clip(start, end).isPresent();
                if (!touched) continue;
                // A bird's wide hitbox must not burn entities behind a solid wall.
                HitResult sight = level().clip(new ClipContext(start, target.getBoundingBox().getCenter(),
                        ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
                if (sight.getType() == HitResult.Type.MISS) hits.add(target);
            }
            hits.sort(Comparator.comparingDouble(e -> e.getBoundingBox().getCenter().distanceToSqr(start)));
            for (LivingEntity target : hits) {
                alreadyHit.add(target.getUUID());
                DamageSource source = new DamageSource(server.registryAccess()
                        .registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DAWN_FLAME), this, getOwner());
                float damage = isPhoenix() ? Abilities.PHOENIX_DAMAGE : Abilities.FIREBALL_DAMAGE;
                if (target.hurt(source, damage)) target.igniteForSeconds(4.0F);
                if (!isPhoenix()) {
                    setPos(target.getBoundingBox().clip(start, end).orElse(end));
                    burst(server);
                    discard();
                    return;
                }
            }
            if (wall.getType() != HitResult.Type.MISS) {
                setPos(end);
                burst(server);
                discard();
                return;
            }
        }
        setPos(end);
        if (level().isClientSide) particles();
    }

    private boolean validTarget(LivingEntity target) {
        Entity owner = getOwner();
        return target.isAlive() && !target.isSpectator() && target != owner
                && !alreadyHit.contains(target.getUUID())
                && (owner == null || (!owner.isAlliedTo(target) && !target.isAlliedTo(owner)));
    }

    private void particles() {
        level().addParticle(ParticleTypes.FLAME, getX(), getY(), getZ(), 0, 0.01, 0);
        if (isPhoenix()) {
            Vec3 right = getDeltaMovement().normalize().cross(new Vec3(0, 1, 0));
            if (right.lengthSqr() < 0.01D) right = new Vec3(1, 0, 0);
            right = right.normalize();
            for (int side = -1; side <= 1; side += 2) {
                Vec3 wing = position().add(right.scale(1.1D * side));
                level().addParticle(ParticleTypes.FLAME, wing.x, wing.y, wing.z, 0, 0.025, 0);
            }
            level().addParticle(ParticleTypes.SMALL_FLAME, getX(), getY() + 0.15, getZ(), 0, 0.015, 0);
        } else {
            level().addParticle(ParticleTypes.SMOKE, getX(), getY(), getZ(), 0, 0.01, 0);
        }
    }

    private void burst(ServerLevel level) {
        level.sendParticles(ParticleTypes.FLAME, getX(), getY(), getZ(),
                isPhoenix() ? 28 : 14, 0.25, 0.25, 0.25, 0.035);
        level.playSound(null, getX(), getY(), getZ(), SoundEvents.FIRECHARGE_USE,
                SoundSource.PLAYERS, 0.5F, isPhoenix() ? 0.8F : 1.25F);
    }

    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("DawnAge", tickCount);
        // An entity must never be re-hit if a chunk is saved while the phoenix is in flight.
        net.minecraft.nbt.ListTag hit = new net.minecraft.nbt.ListTag();
        for (UUID uuid : alreadyHit) hit.add(net.minecraft.nbt.NbtUtils.createUUID(uuid));
        tag.put("DawnHit", hit);
    }

    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        tickCount = tag.getInt("DawnAge");
        alreadyHit.clear();
        net.minecraft.nbt.ListTag hit = tag.getList("DawnHit", net.minecraft.nbt.Tag.TAG_INT_ARRAY);
        for (net.minecraft.nbt.Tag entry : hit) alreadyHit.add(net.minecraft.nbt.NbtUtils.loadUUID(entry));
    }
}
