package com.kyuu.KyuuKun.KEconomy.Shop;

import com.kyuu.KyuuKun.Core.Main.Core;
import com.kyuu.KyuuKun.Core.Util.ItemBuilder;
import com.kyuu.KyuuKun.KEconomy.Main.KEconomy;
import com.kyuu.KyuuKun.KEconomy.Util.Coin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

public class Shop
{
    private ItemStack item; //Item
    private String configName;
    private Location loc;
    private double buyValue; //Value of 1 item
    private double sellValue;
    private boolean isActive;
    private String shopName;
    private boolean usePermission;
    private String permission;
    private byte rarityType; // 0 -> ShopRarity, 1 -> ConfigRarity
    private ShopRarity rarity;
    private boolean canBuy;
    private boolean canSell;
    private double customRarity = 0;
    private int id = -1;
    private ShopEntity shopEntity;
    private static ArrayList<String> tempIds = new ArrayList<String>();
    private static HashMap<String, Shop> tempShops = new HashMap<String, Shop>();
    private static ArrayList<Shop> shops = new ArrayList<Shop>();

    public Shop(ItemStack item, Location loc, String shopName, boolean canBuy, double buyValue, boolean canSell, double sellValue, byte rarityType, ShopRarity rarity)
    {
        this.item = item;
        this.loc = loc;
        this.configName = "shop_" + ItemBuilder.serialize(item) + "_" + loc.getWorld().getName() + "_" + loc.getBlockX() + "_" + loc.getBlockY() + "_" + loc.getBlockZ();
        this.buyValue = buyValue;
        this.sellValue = sellValue;
        if(shopName != null)
        {
            this.shopName = shopName;
        } else {
            this.shopName = (item.hasItemMeta()) ? ((item.getItemMeta().hasDisplayName()) ? item.getItemMeta().getDisplayName() : Core.getNames().getName(item.getType()))
                    : Core.getNames().getName(item.getType());
        }
        this.usePermission = false;
        this.permission = "";
        this.rarityType = rarityType;
        this.rarity = rarity;
        this.canBuy = canBuy;
        this.canSell = canSell;
        this.isActive = false;


        for(int i = 0; i < (int) KEconomy.getInstance().getFileManager().get("Shop", "id"); i++)
        {
            if(KEconomy.getInstance().getFileManager().get("Shop", i + ".configName") != null)
            {
                if((KEconomy.getInstance().getFileManager().get("Shop", i + ".configName")).equals(configName))
                {
                    id = i;
                    break;
                }
            }
        }

        if(id < 0)
        {
            Random r = new Random();
            id = ((int) KEconomy.getInstance().getFileManager().get("Shop", "id")) + 1 + 2 * (r.nextInt() + 1);

            if(id > 0)
            {
                id = -id;
            }

            tempIds.add(String.valueOf(id));
            tempShops.put(String.valueOf(id), this);
        }

        this.shopEntity = new ShopEntity(loc, item, configName, id, shopName, canSell, canBuy);

        if(id >= 0)
        {
            HashMap<Coin, Boolean> isActive = new HashMap<Coin, Boolean>();

            if(!contains(this))
            {
                shops.add(this);
            }
        }
    }

    public void destroySlime()
    {
        shopEntity.destroySlime();
    }

    public static void clearTemps()
    {
        for(Shop shop : tempShops.values())
        {
            shop.destroySlime();
        }
    }

    private boolean contains(Shop shop)
    {
        boolean has = false;

        for(Shop sho : shops)
        {
            if(sho.getId() == shop.getId())
            {
                has = true;
                break;
            }
        }

        return has;
    }

    public void setItem(ItemStack item)
    {
        for(Player p : Bukkit.getOnlinePlayers())
        {
            send(p, false);
        }

        this.shopEntity.destroy();

        this.item = item;
        this.configName = "shop_" + ItemBuilder.serialize(item) + "_" + loc.getWorld().getName() + "_" + loc.getBlockX() + "_" + loc.getBlockY() + "_" + loc.getBlockZ();
        this.shopName = (item.hasItemMeta()) ? ((item.getItemMeta().hasDisplayName()) ? item.getItemMeta().getDisplayName() : Core.getNames().getName(item.getType()))
                : Core.getNames().getName(item.getType());

        attCan();

        new BukkitRunnable() {
            @Override
            public void run() {

                shopEntity = new ShopEntity(loc, item, configName, id, shopName, canSell, canBuy);

                for(Player p : Bukkit.getOnlinePlayers())
                {
                    send(p, true);
                }
            }
        }.runTaskLater(KEconomy.getInstance(), 1L);
    }

    public void saveConfig()
    {
        shopEntity.destroy();

        if(tempShops.containsValue(this))
        {
            tempIds.remove(String.valueOf(id));
            tempShops.remove(String.valueOf(id));

            id = -1;

            for(int i = 0; i < (int) KEconomy.getInstance().getFileManager().get("Shop", "id"); i++)
            {
                if((KEconomy.getInstance().getFileManager().get("Shop", i + ".configName")).equals(configName))
                {
                    id = i;
                    break;
                }
            }

            if(id < 0)
            {
                id = (int) KEconomy.getInstance().getFileManager().get("Shop", "id");
                KEconomy.getInstance().getFileManager().set("Shop", "id", id + 1);
            }
        }

        KEconomy.getInstance().getFileManager().set("Shop", id + ".item", ItemBuilder.serialize(item));
        KEconomy.getInstance().getFileManager().set("Shop", id + ".configName", configName);
        KEconomy.getInstance().getFileManager().writeLocation("Shop", loc, id + ".loc");

        KEconomy.getInstance().getFileManager().set("Shop", id + ".buy", buyValue);
        KEconomy.getInstance().getFileManager().set("Shop", id + ".sell", sellValue);
        KEconomy.getInstance().getFileManager().set("Shop", id + ".active" , isActive);

        attCan();

        KEconomy.getInstance().getFileManager().set("Shop", id + ".shopName", shopName);
        KEconomy.getInstance().getFileManager().set("Shop", id + ".canBuy", canBuy);
        KEconomy.getInstance().getFileManager().set("Shop", id + ".canSell", canSell);
        KEconomy.getInstance().getFileManager().set("Shop", id + ".usePermission", usePermission);
        KEconomy.getInstance().getFileManager().set("Shop", id + ".permission", permission);
        KEconomy.getInstance().getFileManager().set("Shop", id + ".rarityType", rarityType);
        KEconomy.getInstance().getFileManager().set("Shop", id + ".rarity", rarity.name.toUpperCase());
        KEconomy.getInstance().getFileManager().set("Shop", id + ".customRarity", customRarity);

        if(!KEconomy.getInstance().getFileManager().get("Shop", id + ".shopName").equals("\"\""))
        {
            setShopName((String) KEconomy.getInstance().getFileManager().get("Shop", id + ".shopName"));
            this.shopEntity = new ShopEntity(loc, item, configName, id, shopName, canSell, canBuy);
        } else {
            this.shopEntity = new ShopEntity(loc, item, configName, id, shopName, canSell, canBuy);
        }

        for(Player p : Bukkit.getOnlinePlayers())
        {
            send(p, true);
        }

        if(!contains(this))
        {
            shops.add(this);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                shopEntity.slimeDebug();
            }
        }.runTaskLater(KEconomy.getInstance(), 1L);
    }

    public void setUsePermission(boolean use)
    {
        usePermission = use;
    }

    public void setPermission(String perm)
    {
        permission = perm;
    }

    public void setShopName(String name)
    {
        shopName = name;
    }

    public void setBuyValue(double buyValue)
    {
        this.buyValue = buyValue;
        attCan();
    }

    public void setRarity(ShopRarity rarity)
    {
        this.rarity = rarity;
    }

    public void setRarityType(byte rarityType)
    {
        this.rarityType = rarityType;
    }

    public void setSellValue(double sellValue)
    {
        this.sellValue = sellValue;
        attCan();
    }

    public boolean isActive()
    {
        return isActive;
    }

    public byte getRarityType()
    {
        return rarityType;
    }

    public double getCustomRarity()
    {
        return customRarity;
    }

    public void setCustomRarity(double rarity)
    {
        this.customRarity = rarity;
    }

    public double getBuyValue()
    {
        return buyValue;
    }

    public double getSellValue()
    {
        return sellValue;
    }

    public ItemStack getItem()
    {
        return item;
    }

    public Location getLocation()
    {
        return loc;
    }

    public ShopRarity getRarity()
    {
        return rarity;
    }

    public String getPermission()
    {
        return permission;
    }

    public String getShopName()
    {
        return shopName;
    }

    public boolean canBuy() {
        return canBuy;
    }

    public boolean canSell() {
        return canSell;
    }

    public boolean usePermission()
    {
        return usePermission;
    }

    public int getId()
    {
        return id;
    }

    public void active(boolean active)
    {
        this.isActive = active;
    }

    public void send(Player p, boolean visible)
    {
        if(visible)
        {
            shopEntity.spawnArmorStand(p);
        } else shopEntity.killArmorStand(p);
    }

    public void delete()
    {
        if(id >= 0)
        {
            KEconomy.getInstance().getFileManager().set("Shop", String.valueOf(id), null);

            for(Player p : Bukkit.getServer().getOnlinePlayers())
            {
                send(p, false);
            }

            shopEntity.destroy();
        } else {
            shopEntity.destroy();
        }
    }

    public static Shop getTempShop(int id)
    {
        return tempShops.get(String.valueOf(id));
    }

    public static Shop getShopById(int id)
    {
        if(((int) KEconomy.getInstance().getFileManager().get("Shop", "id")) >= id)
        {
            Shop shop = null;

            for(Shop sh : shops)
            {
                if(sh.getId() == id)
                {
                    shop = sh;
                    break;
                }
            }

            if(KEconomy.getInstance().getFileManager().get("Shop", String.valueOf(id)) == null)
            {
                return null;
            }

            if(shop == null)
            {
                double buy = 0;
                double sell = 0;
                boolean isActive = false;
                String name = null;

                for(String key : KEconomy.getInstance().getFileManager().getConfig("Shop").getKeys(true))
                {
                    if(key.startsWith(id + ".buy"))
                    {
                        buy = (double) KEconomy.getInstance().getFileManager().get("Shop", key);
                    }

                    if(key.startsWith(id + ".sell"))
                    {
                        sell = (double) KEconomy.getInstance().getFileManager().get("Shop", key);
                    }

                    if(key.startsWith(id + ".active"))
                    {
                        isActive = (boolean) KEconomy.getInstance().getFileManager().get("Shop", key);
                    }

                    if(key.startsWith(id + ".shopName"))
                    {
                        name = (String) KEconomy.getInstance().getFileManager().get("Shop", key);
                    }
                }

                boolean canSell = false;

                if(sell > 0)
                {
                    canSell = true;
                }

                boolean canBuy = false;

                if(buy > 0)
                {
                    canBuy = true;
                }

                shop = new Shop(
                        ItemBuilder.unserialize(((String)KEconomy.getInstance().getFileManager().get("Shop", id +".item"))),
                        KEconomy.getInstance().getFileManager().readCenterLocation("Shop", id + ".loc"),
                        name,
                        canBuy,
                        buy,
                        canSell,
                        sell,
                        Byte.valueOf(String.valueOf(KEconomy.getInstance().getFileManager().get("Shop", id + ".rarityType"))),
                        ShopRarity.valueOf((String) KEconomy.getInstance().getFileManager().get("Shop", id + ".rarity"))
                );

                shop.setPermission((String) KEconomy.getInstance().getFileManager().get("Shop", id + ".permission"));
                shop.setUsePermission((Boolean) KEconomy.getInstance().getFileManager().get("Shop", id + ".usePermission"));
                shop.setCustomRarity((Double) KEconomy.getInstance().getFileManager().get("Shop", id + ".customRarity"));

                if(!KEconomy.getInstance().getFileManager().get("Shop", id + ".shopName").equals("\"\""))
                {
                    shop.setShopName((String) KEconomy.getInstance().getFileManager().get("Shop", id + ".shopName"));
                }

                shop.attActive(isActive);

                return shop;
            } else return shop;

        } else return null;
    }

    public static Shop getShopByConfigName(String configName, int id, boolean isTemp)
    {
        if(!isTemp)
        {
            if(((int) KEconomy.getInstance().getFileManager().get("Shop", "id")) > 0)
            {
                int maxId = ((int) KEconomy.getInstance().getFileManager().get("Shop", "id"));
                int idd = -1;

                for(int i = 0; i < maxId; i++)
                {
                    Shop shop = getShopById(i);
                    if(shop.id == id);
                    {
                        idd = i;
                        break;
                    }
                }

                if(idd >= 0)
                {
                    return getShopById(id);
                } else return null;
            } else return null;
        } else {
            Shop s = null;

            for(String idd : tempIds)
            {
                if(tempShops.get(idd).configName.equals(configName))
                {
                    s = getTempShop(Integer.valueOf(idd));
                    break;
                }
            }

            return s;
        }
    }

    private void attActive(boolean isActive)
    {
        this.isActive = isActive;
    }

    private void attCan()
    {
        if(buyValue > 0)
        {
            canBuy = true;
        }

        if(sellValue > 0)
        {
            canSell = true;
        }
    }

    public static ArrayList<Shop> getShops()
    {
        ArrayList<Shop> shops = new ArrayList<Shop>();

        int go = ((int) KEconomy.getInstance().getFileManager().get("Shop", "id"));

        for(int i = 0; i < go; i++)
        {
			Shop shop = getShopById(i);
            if(shop != null)
			{
				shops.add(shop);
			}
        }

        return shops;
    }

    public enum ShopRarity
    {
        NORMAL("Normal", 0.2);
        private String name;
        private double mod;

        ShopRarity(String name, double mod)
        {
            this.name = name;
            this.mod = mod;
        }

        public String getRarityName()
        {
            return name;
        }

        public double getValue()
        {
            return mod;
        }
    }
}
