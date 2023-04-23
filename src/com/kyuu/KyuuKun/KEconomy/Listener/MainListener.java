package com.kyuu.KyuuKun.KEconomy.Listener;

import com.kyuu.KyuuKun.Core.Util.Messager;
import com.kyuu.KyuuKun.Core.Util.Misc;
import com.kyuu.KyuuKun.KEconomy.Main.KEconomy;
import com.kyuu.KyuuKun.KEconomy.Shop.PlayerShopEvent;
import com.kyuu.KyuuKun.KEconomy.Shop.Shop;
import com.kyuu.KyuuKun.KEconomy.Shop.ShopAction;
import com.kyuu.KyuuKun.KEconomy.Shop.WrenchInventories;
import com.kyuu.KyuuKun.KEconomy.Util.Items;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class MainListener implements Listener
{
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent e)
	{
		if(Shop.getShops().size() > 0)
        {
            for(Shop shop : Shop.getShops())
            {
                shop.send(e.getPlayer(), true);
            }
        }

		//System.out.println("KKKKKK");
	}

	@EventHandler
	public void shopBuy(PlayerInteractAtEntityEvent e)
	{
		if(e.getRightClicked() instanceof Slime)
		{
			Slime slime = (Slime) e.getRightClicked();

			if(slime.hasMetadata("shopEntity"))
			{
			    if(slime.getMetadata("shopEntity").get(0).asString() != null)
                {
                    if(getIdByData(slime.getMetadata("shopEntity").get(0).asString()) >= 0)
                    {
                        Shop shop = Shop.getShopByConfigName(getConfigByData(slime.getMetadata("shopEntity").get(0).asString()), getIdByData(slime.getMetadata("shopEntity").get(0).asString()),
                                false);

                        if(shop != null)
                        {
                            if(!Misc.checkPlayer(e.getPlayer(), "kyuu.keconomy.shopadmin"))
                            {
                                PlayerShopEvent event = new PlayerShopEvent(e.getPlayer(), shop, ShopAction.BUY);
                                Bukkit.getServer().getPluginManager().callEvent(event);
                            } else {
                                if(!e.getPlayer().getInventory().getItemInMainHand().equals(Items.WRENCH.getItem()))
                                {
                                    PlayerShopEvent event = new PlayerShopEvent(e.getPlayer(), shop, ShopAction.BUY);
                                    Bukkit.getServer().getPluginManager().callEvent(event);
                                } else {
                                    e.getPlayer().openInventory(WrenchInventories.getInstance().getMainWrench(e.getPlayer(), shop));
                                    WrenchInventories.getInstance().setUsing(e.getPlayer(), shop);
                                }
                            }
                        } else KEconomy.getInstance().getMessager().sendMessage(e.getPlayer(), Messager.MessageType.ERROR, "Loja não reconhecida.");
                    } else {
                        if(Misc.checkPlayer(e.getPlayer(), "kyuu.keconomy.shopadmin"))
                        {
                            Shop shop = Shop.getShopByConfigName(getConfigByData(slime.getMetadata("shopEntity").get(0).asString()), -1, true);

                            if(shop != null)
                            {
                                if(e.getPlayer().getInventory().getItemInMainHand().equals(Items.WRENCH.getItem()))
                                {
                                    WrenchInventories.getInstance().setUsing(e.getPlayer(), shop);
                                    e.getPlayer().openInventory(WrenchInventories.getInstance().getMainWrench(e.getPlayer(), shop));
                                }
                            } else KEconomy.getInstance().getMessager().sendMessage(e.getPlayer(), Messager.MessageType.ERROR, "Loja não reconhecida.");
                        }
                    }
                }
			}
		}
	}

	@EventHandler
    public void shopSell(EntityDamageByEntityEvent e)
    {
        if(e.getDamager() instanceof Player && e.getEntity() instanceof Slime)
        {
            Slime slime = (Slime) e.getEntity();
            Player p = (Player) e.getDamager();
            if(slime.hasMetadata("shopEntity"))
            {
                if(slime.getMetadata("shopEntity").get(0).asString() != null)
                {
                    if(getIdByData(slime.getMetadata("shopEntity").get(0).asString()) >= 0)
                    {
                        Shop shop = Shop.getShopByConfigName(getConfigByData(slime.getMetadata("shopEntity").get(0).asString()), getIdByData(slime.getMetadata("shopEntity").get(0).asString()),
                                false);

                        if(shop != null)
                        {
                            if(!Misc.checkPlayer(p, "kyuu.keconomy.shopadmin"))
                            {
                                PlayerShopEvent event = new PlayerShopEvent(p, shop, ShopAction.SELL);
                                Bukkit.getServer().getPluginManager().callEvent(event);
                            } else {
                                if(!p.getInventory().getItemInMainHand().equals(Items.WRENCH.getItem()))
                                {
                                    PlayerShopEvent event = new PlayerShopEvent(p, shop, ShopAction.SELL);
                                    Bukkit.getServer().getPluginManager().callEvent(event);
                                } else {
                                    p.openInventory(WrenchInventories.getInstance().getMainWrench(p, shop));
                                    WrenchInventories.getInstance().setUsing(p, shop);
                                }
                            }
                        } else KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.ERROR, "Loja não reconhecida.");
                    } else {
                        if(Misc.checkPlayer(p, "kyuu.keconomy.shopadmin"))
                        {
                            Shop shop = Shop.getShopByConfigName(getConfigByData(slime.getMetadata("shopEntity").get(0).asString()), -1, true);

                            if(shop != null)
                            {
                                if(p.getInventory().getItemInMainHand().equals(Items.WRENCH.getItem()))
                                {
                                    WrenchInventories.getInstance().setUsing(p, shop);
                                    p.openInventory(WrenchInventories.getInstance().getMainWrench(p, shop));
                                }
                            } else KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.ERROR, "Loja não reconhecida.");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e)
    {
        if(e.getEntity() instanceof Slime)
        {
            Slime slime = (Slime) e.getEntity();
            if(slime.hasMetadata("shopEntity"))
            {
                e.setDroppedExp(0);
                e.getDrops().clear();
            }
        }
    }


    private String getConfigByData(String data)
    {
        return data.split("@")[0];
    }

    private int getIdByData(String data)
    {
        return Integer.parseInt(data.split("@")[1]);
    }
}
