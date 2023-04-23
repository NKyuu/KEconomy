package com.kyuu.KyuuKun.KEconomy.Shop;

import com.kyuu.KyuuKun.Core.Util.ItemBuilder;
import com.kyuu.KyuuKun.Core.Util.Messager;
import com.kyuu.KyuuKun.Core.Util.Misc;
import com.kyuu.KyuuKun.Core.Util.NBTManager;
import com.kyuu.KyuuKun.KEconomy.Main.KEconomy;
import com.kyuu.KyuuKun.KEconomy.Util.Coin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShopListener implements Listener
{
    @EventHandler
    public void onShop(PlayerShopEvent e)
    {
        Player p = e.getPlayer();
        Shop shop = e.getShop();

        if(shop.usePermission())
        {
            if(Misc.checkPlayer(p, shop.getPermission()))
            {
                if(e.getAction().equals(ShopAction.BUY))
                {
                    if(shop.canBuy())
                    {
                        p.openInventory(WrenchInventories.getInstance().getBuyInventory(p, shop));
                    } else KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.ERROR, "Esta loja não exerce esta função.");
                } else {
                    if(shop.canSell())
                    {
                        p.openInventory(WrenchInventories.getInstance().getSellInventory(p, shop));
                    }
                }
            } else KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.ERROR, "Você não tem permissão para utilizar esta loja.");
        } else {
            if(e.getAction().equals(ShopAction.BUY))
            {
                if(shop.canBuy())
                {
                    p.openInventory(WrenchInventories.getInstance().getBuyInventory(p, shop));
                } else KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.ERROR, "Esta loja não exerce esta função.");
            } else {
                if(shop.canSell())
                {
                    p.openInventory(WrenchInventories.getInstance().getSellInventory(p, shop));
                }
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e)
    {
        if(e.getWhoClicked() instanceof Player)
        {
            Player p = (Player) e.getWhoClicked();

            if(e.getClickedInventory() != null)
            {
                if(e.getView().getTitle().equals(ChatColor.GOLD + "Compra"))
                {
                    e.setCancelled(true);

                    if(e.getCurrentItem() != null)
                    {
                        if(e.getCurrentItem().getType().equals(Material.GOLD_NUGGET))
                        {
                            NBTManager nbt = new NBTManager(e.getCurrentItem());

                            int shopID = (int) nbt.getCustomTag("shopID", NBTManager.ValueType.INT);
                            double price = (double) nbt.getCustomTag("price", NBTManager.ValueType.DOUBLE);

                            Shop shop = Shop.getShopById(shopID);

                            if(shop != null)
                            {
                                if(KEconomy.getInstance().getEconomy().getBalance(p) >= price)
                                {
                                    KEconomy.getInstance().getEconomy().withdrawPlayer(p, price);
                                    p.getInventory().addItem(shop.getItem());
                                    KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.SUCCESS, "Compra realizada com sucesso!");
                                } else {
                                    p.closeInventory();
                                    KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.ERROR, "Você não tem dinheiro o suficiente.");
                                }

                            } else {
                                p.closeInventory();
                                KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.ERROR, "Sistemas inoperantes. Chame um administrador.");
                            }

                            } else {
                                p.closeInventory();
                                KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.ERROR, "Sistemas inoperantes. Chame um administrador.");
                            }
                        }
                    } else if(e.getView().getTitle().contains(ChatColor.DARK_GREEN + "Venda"))
                    {
                        e.setCancelled(true);

                        if(e.getCurrentItem() != null)
                        {
                            if(e.getCurrentItem().getType().equals(Material.IRON_NUGGET))
                            {
                                NBTManager nbt = new NBTManager(e.getCurrentItem());


                                int shopID = (int) nbt.getCustomTag("shopID", NBTManager.ValueType.INT);
                                double price = (double) nbt.getCustomTag("price", NBTManager.ValueType.DOUBLE);


                                Shop shop = Shop.getShopById(shopID);

                                if(shop != null)
                                {
                                    ItemStack selled = shop.getItem().clone();

                                    if(p.getInventory().contains(selled))
                                    {
                                        ItemStack toSell = null;
                                        for(ItemStack it : p.getInventory().getContents())
                                        {
                                            if(it != null)
                                            {
                                                if(it.isSimilar(selled))
                                                {
                                                    toSell = it;
                                                    break;
                                                }
                                            }
                                        }

                                        if(toSell != null)
                                        {
                                            if(toSell.getAmount() > selled.getAmount())
                                            {
                                                int slot = getSlot(p.getInventory(), toSell);

                                                if(slot > -1)
                                                {
                                                    ItemBuilder builder = new ItemBuilder(toSell);
                                                    builder.setAmount(toSell.getAmount() - selled.getAmount());
                                                    p.getInventory().setItem(slot, builder.construct());
                                                    KEconomy.getInstance().getEconomy().depositPlayer(p, price);
                                                    KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.SUCCESS, "Venda realizada com sucesso!");
                                                } else {
                                                    p.closeInventory();
                                                    KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.ERROR, "Sistemas inoperantes. Chame um administrador.");
                                                }
                                            } else {
                                                p.getInventory().remove(toSell);
                                                KEconomy.getInstance().getEconomy().depositPlayer(p, price);
                                                KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.SUCCESS, "Venda realizada com sucesso!");
                                            }
                                        } else {
                                            p.closeInventory();
                                            KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.ERROR, "Sistemas inoperantes. Chame um administrador.");
                                        }
                                    } else {
                                        ItemStack toSell = null;
                                        for(ItemStack it : p.getInventory().getContents())
                                        {
                                            if(it != null)
                                            {
                                                if(it.isSimilar(selled))
                                                {
                                                    toSell = it;
                                                    break;
                                                }
                                            }
                                        }

                                        if(toSell != null)
                                        {
                                            if(toSell.getAmount() > selled.getAmount())
                                            {
                                                int slot = getSlot(p.getInventory(), toSell);

                                                if(slot > -1)
                                                {
                                                    ItemBuilder builder = new ItemBuilder(toSell);
                                                    builder.setAmount(toSell.getAmount() - selled.getAmount());
                                                    p.getInventory().setItem(slot, builder.construct());
                                                    KEconomy.getInstance().getEconomy().depositPlayer(p, price);
                                                    KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.SUCCESS, "Venda realizada com sucesso!");
                                                } else {
                                                    p.closeInventory();
                                                    KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.ERROR, "Sistemas inoperantes. Chame um administrador.");
                                                }
                                            } else {
                                                p.getInventory().remove(toSell);
                                                KEconomy.getInstance().getEconomy().depositPlayer(p, price);
                                                KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.SUCCESS, "Venda realizada com sucesso!");
                                            }
                                        } else {
                                            p.closeInventory();
                                            KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.ERROR, "Sistemas inoperantes. Chame um administrador.");
                                        }
                                    }

                                } else {
                                    p.closeInventory();
                                    KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.ERROR, "Sistemas inoperantes. Chame um administrador.");
                                }

                            }
                        }

                    }

                }

        }
    }

    private int getSlot(Inventory inv, ItemStack item)
    {
        int slot = -1;

        for(int i = 0; i < inv.getContents().length; i++)
        {
            if(inv.getItem(i) != null)
            {
                if(inv.getItem(i).isSimilar(item))
                {
                    slot = i;
                    break;
                }
            }
        }

        return slot;
    }
}
