package avkott.content

import arc.graphics.Color
import arc.math.Interp
import avkott.content.AvkLiquids.liquidNitrogen
import mindustry.content.*
import mindustry.entities.bullet.LiquidBulletType
import mindustry.entities.bullet.ShrapnelBulletType
import mindustry.entities.part.DrawPart.PartMove
import mindustry.entities.part.DrawPart.PartProgress
import mindustry.entities.part.RegionPart
import mindustry.entities.pattern.ShootSpread
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.type.PayloadStack
import mindustry.world.Block
import mindustry.world.blocks.defense.turrets.ItemTurret
import mindustry.world.blocks.defense.turrets.LiquidTurret
import mindustry.world.blocks.production.WallCrafter
import mindustry.world.blocks.units.UnitAssembler
import mindustry.world.blocks.units.UnitAssembler.AssemblerUnitPlan
import mindustry.world.blocks.units.UnitFactory
import mindustry.world.draw.DrawTurret
import mindustry.world.meta.Attribute

object AvkBlocks {
    lateinit var cliffPulverizer: Block
    lateinit var ingen: Block
    lateinit var vapor: Block
    lateinit var mechCrafter: Block
    lateinit var vesselFabricator: Block

    fun load(){
        //production
        cliffPulverizer = WallCrafter("cliff-pulverizer").apply{
            requirements(Category.production, ItemStack.with(Items.beryllium, 90, Items.silicon, 50, Items.tungsten, 75))
            consumePower(1.2f)
            drillTime = 69.66f
            size = 3
            attribute = Attribute.sand
            output = Items.sand
            fogRadius = 4
            itemCapacity = 30
            health = 560
        }
        //endregion
        //turret
        ingen = ItemTurret("ingen").apply{
            requirements(Category.turret, ItemStack.with(Items.beryllium, 80, Items.graphite, 45, Items.silicon, 35))

            ammo(Items.graphite, ShrapnelBulletType().apply {
                lifetime = 15f
                length = 105f
                damage = 55f
                shootEffect = Fx.shootBigColor
                smokeEffect = Fx.shootBigSmoke
                ammoMultiplier = 3f
                fromColor = Color.valueOf("feb380")
                hitColor = Color.valueOf("ea8878")
                toColor = hitColor
                hitEffect = Fx.hitBulletColor
                buildingDamageMultiplier = 0.2f
                hitLarge = true
            })
            size = 2
            drawer = DrawTurret("reinforced-").apply {
                parts.addAll(
                    RegionPart("-barrel").apply {
                        progress = PartProgress.reload.curve(Interp.pow2In)
                        moveY = -1f
                        heatColor = Color.valueOf("f03b0e")
                        under = true
                    },
                    RegionPart("-sus").apply {
                        heatProgress = PartProgress.smoothReload
                        progress = PartProgress.smoothReload
                        mirror = true
                        moveX = 1.5f
                        moveY = 0.5f
                        heatColor = Color.red.cpy()
                        moves.add(PartMove(PartProgress.smoothReload, 0f, -1.5f, -2f))
                        under = true
                    }
                )
            }
            health = 750
            recoil = 1f
            outlineColor = Pal.darkOutline
            shootY = 10f
            range = 110f
            inaccuracy = 5f
            ammoPerShot = 2
            reload = 20f
        }

        vapor = LiquidTurret("vapor").apply {
            requirements(Category.turret, ItemStack.with(Items.beryllium, 90, Items.tungsten, 30, Items.silicon, 75))

            ammo(
                Liquids.nitrogen, LiquidBulletType(liquidNitrogen).apply {
                    ammoMultiplier = 2f
                    knockback = 2f
                    drag = 0.01f
                    trailLength = 7
                    trailWidth = 3f
                    trailColor = Liquids.nitrogen.color
                    speed = 5.3f
                    lifetime = 40f
                }
            )

            drawer = DrawTurret().apply {
                parts.add(RegionPart("-part").apply {
                    progress = PartProgress.warmup
                    x = -0.5f
                    y = 0.5f
                    moveX = 0.5f
                    moveY = -0.5f
                    under = true
                    mirror = true
                })
            }
            heatColor = Color.valueOf("b2c5d0")
            size = 2
            reload = 3f
            range = 130f
            shoot = ShootSpread(2, 12f)
            velocityRnd = 0.12f
            recoil = 0.5f
            liquidCapacity = 35f
            health = 750
            shootY = 1f
            inaccuracy = 10f
            cooldownTime = 60f
            shootEffect = Fx.shootSmallColor
        }
        //endregion
        //region units
        vesselFabricator = object : UnitFactory("vessel-fabricator") {
            init {
                this.requirements(
                    Category.units,
                    ItemStack.with(Items.silicon, 250, Items.beryllium, 200, Items.tungsten, 120)
                )
                size = 3
                configurable = false
                plans.add(
                    UnitPlan(
                        AvkUnitTypes.aver,
                        3000f,
                        ItemStack.with(Items.graphite, 65, Items.silicon, 70)
                    )
                )
                regionSuffix = "-dark"
                fogRadius = 5
                researchCostMultiplier = 0.5f
                consumePower(3.0f)
            }
        }

        mechCrafter = UnitAssembler("mech-crafter").apply {
            requirements(
                Category.units,
                ItemStack.with(Items.beryllium, 220, Items.silicon, 200, Items.tungsten, 70, Items.oxide, 40)
            )
            size = 3
            consumePower(2.1f)
            plans.add(
                AssemblerUnitPlan(AvkUnitTypes.elud, 30 * 60f, PayloadStack.list(UnitTypes.elude, 2, UnitTypes.merui, 1))
            )
            areaSize = 8
            consumeLiquid(Liquids.hydrogen, 8 / 60f)
        }
    }
}