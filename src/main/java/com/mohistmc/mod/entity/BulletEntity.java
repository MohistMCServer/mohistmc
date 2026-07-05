package com.mohistmc.mod.entity;

import com.mohistmc.mod.register.ModEntities;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * 自定义子弹实体 (普通抛射物)
 * @author Mgazul
 * @date 2026/3/31
 */
public class BulletEntity extends Projectile {

    private double damage = 5.0;
    private int life = 0; // 简单的生命周期计数器，防止无限飞行

    // 必须的无参构造函数（反序列化用）
    public BulletEntity(EntityType<? extends BulletEntity> entityType, Level level) {
        super(entityType, level);
    }

    // 生成实体时调用的构造函数
    public BulletEntity(Level level, LivingEntity shooter, double x, double y, double z) {
        super(ModEntities.BULLET.get(), level);
        this.setPos(x, y, z); // 设置生成位置
        this.xo = x; // 同步旧位置
        this.yo = y;
        this.zo = z;
        this.setOwner(shooter);
    }

    @Override
    public void tick() {
        super.tick();
        // 防止实体卡住或无限飞行
        if (++life > 100) {
            this.discard();
            return;
        }

        Vec3 movement = this.getDeltaMovement();
        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);

        if (hitResult.getType() != HitResult.Type.MISS) {
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                this.onHit(hitResult);
                return;
            }
            if (hitResult.getType() == HitResult.Type.ENTITY && hitResult instanceof EntityHitResult entityHitResult) {
                this.onHitEntity(entityHitResult);
                return;
            }
        }

        // 更新位置
        double newX = this.getX() + movement.x;
        double newY = this.getY() + movement.y;
        double newZ = this.getZ() + movement.z;
        this.setPos(newX, newY, newZ);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        if (!this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 3);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        Entity entity = hitResult.getEntity();
        entity.hurt(this.damageSources().thrown(this, this.getOwner()), (float) damage);
    }

    @Override
    public EntityType<?> getType() {
        return ModEntities.BULLET.get();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }
}