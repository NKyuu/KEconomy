package com.kyuu.KyuuKun.KEconomy.Shop;

import com.kyuu.KyuuKun.Core.Main.Core;
import com.kyuu.KyuuKun.Core.Util.ItemBuilder;
import com.kyuu.KyuuKun.Core.Util.JSON;
import com.kyuu.KyuuKun.KEconomy.Main.KEconomy;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class ShopEntity 
{

    private static HashMap<ShopEntity, HashMap<Player, EntityAnimator>> map = new HashMap<ShopEntity, HashMap<Player, EntityAnimator>>();
    private final Class<?> armorStandClass = Core.getNMSClass("EntityArmorStand");
    private final Class<?> worldServerClass = Core.getNMSClass("World");
    private final Class<?> craftWorldClass = Core.getCBClass("CraftWorld");
    private final Class<?> packetSpawn = Core.getNMSClass("PacketPlayOutSpawnEntityLiving");
    private final Class<?> packetKill = Core.getNMSClass("PacketPlayOutEntityDestroy");
    private final Class<?> craftplayerClass = Core.getCBClass("entity.CraftPlayer");
    private final Class<?> entityLivingClass = Core.getNMSClass("EntityLiving");
    private final Class<?> craftEntityClass = Core.getCBClass("entity.CraftEntity");
    private final Class<?> equipClass = Core.getNMSClass("PacketPlayOutEntityEquipment");
    private final Class<?> craftItemClass = Core.getCBClass("inventory.CraftItemStack");
    private final JSON jsonBuy = new JSON("Comprar (Direito)").setColor(JSON.JSONColor.YELLOW);
    private final JSON mid = new JSON("| ").setColor(JSON.JSONColor.WHITE).setExtra(jsonBuy.generate());
    private final JSON jsonName = new JSON("Vender (Esquerdo) ").setColor(JSON.JSONColor.GREEN).setExtra(mid.generate());
    private final JSON jsonSell = new JSON("Vender (Esquerdo)").setColor(JSON.JSONColor.GREEN);
    private final JSON jsonNew = new JSON("Em configuração").setColor(JSON.JSONColor.RED);

	private ItemBuilder item;
	private Location loc;
	private World world;

	private EntityArmorStand eas;
	private EntityArmorStand shop;
	private EntityArmorStand instruct;
	private Slime slime;

	private String config;
	private int id;

    public ShopEntity(Location loc, ItemStack item, String config, int id, String name, boolean canSell, boolean canBuy)
    {
        this.item = new ItemBuilder(item);
        this.loc = loc;
        this.world = ((CraftWorld) loc.getWorld()).getHandle();
        this.config = config;
        this.id = id;

        EntityArmorStand eas = new EntityArmorStand(world, loc.getBlockX() + 0.5, loc.getBlockY() - 0.2, loc.getBlockZ() + 0.5);
        eas.setBasePlate(false);
        eas.setArms(false);
        eas.setInvisible(true);
        eas.setSmall(true);
        eas.setNoGravity(true);
        eas.setInvulnerable(true);

        EntityArmorStand shop = new EntityArmorStand(world, loc.getBlockX() + 0.5, loc.getBlockY() + 0.2, loc.getBlockZ() + 0.5);
        shop.setCustomNameVisible(true);
        shop.setCustomName(IChatBaseComponent.ChatSerializer.a(new JSON(name).generate()));
        shop.setBasePlate(false);
        shop.setArms(false);
        shop.setInvisible(true);
        shop.setSmall(true);
        shop.setNoGravity(true);
        shop.setInvulnerable(true);

        EntityArmorStand instruct = new EntityArmorStand(world, loc.getBlockX() + 0.5, loc.getBlockY(), loc.getBlockZ() + 0.5);
        instruct.setBasePlate(false);
        instruct.setArms(false);
        instruct.setInvisible(true);
        instruct.setSmall(true);
        instruct.setNoGravity(true);
        instruct.setInvulnerable(true);
        instruct.setCustomNameVisible(true);

        if(canSell && canBuy)
        {
            instruct.setCustomName(IChatBaseComponent.ChatSerializer.a(jsonName.generate()));
        } else if(canBuy && !canSell) {
            instruct.setCustomName(IChatBaseComponent.ChatSerializer.a(jsonBuy.generate()));
        } else if(canSell && !canBuy) {
            instruct.setCustomName(IChatBaseComponent.ChatSerializer.a(jsonSell.generate()));
        } else {
            instruct.setCustomName(IChatBaseComponent.ChatSerializer.a(jsonNew.generate()));
        }

        boolean spawn = true;

        for(Entity e : loc.getWorld().getEntities())
        {
            if(e instanceof Slime)
            {
                Slime slime = (Slime) e;

                if(slime.hasMetadata("shopEntity"))
                {
                    String data = slime.getMetadata("shopEntity").get(0).asString();

                    String[] rData = data.split("@");

                    if(rData[0].equals(config) && Integer.parseInt(rData[1]) == id)
                    {
                        this.slime = slime;
                        spawn = false;
                    }
                }
            }
        }

        if(spawn)
        {
            Slime s = loc.getWorld().spawn(((LivingEntity) eas.getBukkitEntity()).getEyeLocation().clone(), Slime.class);
            s.setSize(2);
            s.setAI(false);
            s.setCollidable(false);
            s.setGravity(false);
//        s.setCustomNameVisible(true);
            s.setSilent(true);
//        s.setCustomName(item.construct().getItemMeta().getDisplayName());
          s.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 9999, false, false));

            s.setMetadata("shopEntity", new FixedMetadataValue(KEconomy.getInstance(), config + "@" + id));

            this.slime = s;
        }

        this.eas = eas;
        this.shop = shop;
        this.instruct = instruct;

        if(!map.containsKey(this))
        {
            map.put(this, new HashMap<Player, EntityAnimator>());
        }
    }

    public void spawnArmorStand(Player p)
    {
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(eas);
        PacketPlayOutSpawnEntityLiving ins = new PacketPlayOutSpawnEntityLiving(instruct);
        PacketPlayOutSpawnEntityLiving sh = new PacketPlayOutSpawnEntityLiving(shop);
        PacketPlayOutEntityEquipment equip = new PacketPlayOutEntityEquipment(eas.getId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(item.construct()));
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(ins);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(sh);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(equip);

        EntityAnimator animator = new EntityAnimator(eas, p);
        animator.runTaskTimer(KEconomy.getInstance(), 0, 2L);
        map.get(this).put(p, animator);
    }

    public void killArmorStand(Player p)
    {
        if(map.get(this).containsKey(p))
        {
            map.get(this).get(p).cancel();
            map.get(this).remove(p);
        }

        PacketPlayOutEntityDestroy d1 = new PacketPlayOutEntityDestroy(eas.getId(), instruct.getId(), shop.getId());
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(d1);
    }

    public void destroy()
    {
        slime.setSize(0);
        slime.damage(slime.getHealth() + 2);
        slime.remove();
        slime.teleport(slime.getLocation().clone().subtract(0, 256, 0));

        for(Player p : Bukkit.getServer().getOnlinePlayers())
        {
            killArmorStand(p);
        }
    }

    public void destroySlime()
    {
        slime.setSize(0);
        slime.damage(slime.getHealth() + 2);
        slime.remove();
        slime.teleport(slime.getLocation().clone().subtract(0, 256, 0));
        slime.setGravity(true);
    }

    public void slimeDebug()
    {
        boolean spawn = true;

        for(Entity e : loc.getWorld().getEntities())
        {
            if(e instanceof Slime)
            {
                Slime slime = (Slime) e;

                if(slime.hasMetadata("shopEntity"))
                {
                    String data = slime.getMetadata("shopEntity").get(0).asString();

                    String[] rData = data.split("@");

                    if(rData[0].equals(config) && Integer.parseInt(rData[1]) == id)
                    {
                        this.slime = slime;
                        spawn = false;
                    }
                }
            }
        }

        if(spawn)
        {
            Slime s = loc.getWorld().spawn(((LivingEntity) eas.getBukkitEntity()).getEyeLocation().clone(), Slime.class);
            s.setSize(2);
            s.setAI(false);
            s.setCollidable(false);
            s.setGravity(false);
//        s.setCustomNameVisible(true);
            s.setSilent(true);
//        s.setCustomName(item.construct().getItemMeta().getDisplayName());
            s.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 9999, false, false));

            s.setMetadata("shopEntity", new FixedMetadataValue(KEconomy.getInstance(), config + "@" + id));

            this.slime = s;
        }
    }

	public ItemBuilder getBuilder() {
		return item;
	}
    public ItemStack getItem() {
        return item.construct();
    }
	public Location getLocation() {
		return loc;
	}

	private class EntityAnimator extends BukkitRunnable
    {
        private Player p;
        private EntityArmorStand stand;
        private float yaw;

        EntityAnimator(EntityArmorStand eas, Player p)
        {
            this.p = p;
            this.stand = eas;
            this.yaw = 0;
        }

        @Override
        public void run()
        {
            PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook moveLook = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(
                    stand.getId(),
                    (short) 0,
                    (short) 0,
                    (short) 0,
                    (byte)(yaw * 256.0F / 360.0F),
                    (byte) 0,
                    false
            );

            ((CraftPlayer)p).getHandle().playerConnection.sendPacket(moveLook);

            yaw += 7;


        }

    }
}
