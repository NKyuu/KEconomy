package com.kyuu.KyuuKun.KEconomy.Shop;

import com.kyuu.KyuuKun.Core.Util.Messager;
import com.kyuu.KyuuKun.Core.Util.Misc;
import com.kyuu.KyuuKun.Core.Util.NBTManager;
import com.kyuu.KyuuKun.KEconomy.Main.KEconomy;
import com.kyuu.KyuuKun.KEconomy.Util.Coin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class WrenchManager implements Listener
{

    private static ArrayList<Player> changeName = new ArrayList<Player>();
    private static ArrayList<Player> changeItem = new ArrayList<Player>();
    private static ArrayList<Player> changePerm = new ArrayList<Player>();
    private static ArrayList<Player> changeRarity = new ArrayList<Player>();

    private static ArrayList<Player> setBuy = new ArrayList<Player>();
    private static ArrayList<Player> setSell = new ArrayList<Player>();

    private static HashMap<Player, Integer> pager = new HashMap<Player, Integer>();
    private static ArrayList<Player> pBuy = new ArrayList<>();
    private static ArrayList<Player> pSell = new ArrayList<>();

    @EventHandler
    public void inventoryClick(InventoryClickEvent e)
    {
        if(e.getWhoClicked() instanceof Player)
        {
            Player p = (Player) e.getWhoClicked();

            if(e.getClickedInventory() != null)
            {
                if(WrenchInventories.getInstance().isUsing(p))
                {
                    if(e.getView().getTitle().equals(ChatColor.GOLD + "Edição de Loja"))
                    {
                        e.setCancelled(true);

                        if(e.getCurrentItem() != null)
                        {
                            if (e.getRawSlot() == 13) {
                                changeItem.add(p);
                            } else {
                                if(e.getCurrentItem().getType().equals(Material.NAME_TAG)) {
                                    p.closeInventory();
                                    KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.NONE, "Digite no chat o novo nome da loja. Caso seja \"\" voltará ao padrão.");
                                    changeName.add(p);
                                } else if(e.getCurrentItem().getType().equals(Material.HOPPER)) {
                                    p.closeInventory();
                                    KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.NONE, "Digite no chat a nova permissão da loja. Caso esteja vazio será liberado para todos.");
                                    changePerm.add(p);
                                } else if(e.getCurrentItem().getType().equals(Material.GOLDEN_APPLE)) {
                                    p.closeInventory();
                                    Shop shop = WrenchInventories.getInstance().getShop(p);
                                    shop.setRarityType((byte) 1);
                                    p.openInventory(WrenchInventories.getInstance().getMainWrench(p, shop));
                                } else if(e.getCurrentItem().getType().equals(Material.ENCHANTED_GOLDEN_APPLE)) {
                                    p.closeInventory();
                                    Shop shop = WrenchInventories.getInstance().getShop(p);
                                    shop.setRarityType((byte) 0);
                                    p.openInventory(WrenchInventories.getInstance().getMainWrench(p, shop));
                                } else if(e.getCurrentItem().getType().equals(Material.HEART_OF_THE_SEA)) {
                                    if(WrenchInventories.getInstance().getShop(p).getRarityType() == 1)
                                    {
                                        changeRarity.add(p);
                                        p.closeInventory();
                                        KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.NONE, "Digite no chat a nova raridade da loja.");
                                    }
                                } else if(e.getCurrentItem().getType().equals(Material.BARRIER)) {
                                    p.closeInventory();
                                    WrenchInventories.getInstance().removeUsing(p);
                                    KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.ERROR, "Você cancelou a edição.");
                                } else if(e.getCurrentItem().getType().equals(Material.BONE)) {
                                    p.closeInventory();
                                    KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.ERROR, "Você deletou a loja.");
                                    WrenchInventories.getInstance().getShop(p).delete();
                                    WrenchInventories.getInstance().removeUsing(p);
                                } else if(e.getCurrentItem().getType().equals(Material.EMERALD)) {
                                    p.closeInventory();
                                    KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.SUCCESS, "Você editou a loja com sucesso.");
                                    WrenchInventories.getInstance().getShop(p).saveConfig();
                                    WrenchInventories.getInstance().removeUsing(p);
                                } else if(e.getCurrentItem().getType().equals(Material.GOLD_INGOT)) {
                                    p.closeInventory();
                                    pBuy.add(p);
                                    KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.SUCCESS, "Digite o preço de compra");
                                    //p.openInventory(WrenchInventories.getInstance().getBuySellInventory(p, WrenchInventories.getInstance().getShop(p), 1));
                                } else if(e.getCurrentItem().getType().equals(Material.IRON_INGOT)) {
                                    p.closeInventory();
                                    pSell.add(p);
                                    KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.SUCCESS, "Digite o preço de venda");
                                }

                                if(changeItem.contains(p))
                                {
                                    if(e.getCurrentItem() != null)
                                    {
                                        if(!e.getCurrentItem().getType().equals(Material.AIR))
                                        {
                                            Shop shop = WrenchInventories.getInstance().getShop(p);

                                            for(Player player : Bukkit.getOnlinePlayers())
                                            {
                                                shop.send(player, false);
                                            }

                                            ItemStack item = (e.getCurrentItem().getType() != Material.AIR) ? e.getCurrentItem() : e.getCursor();
                                            shop.setItem(item.clone());

                                            p.closeInventory();

                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    p.openInventory(WrenchInventories.getInstance().getMainWrench(p, shop));
                                                    changeItem.remove(p);

                                                }
                                            }.runTaskLater(KEconomy.getInstance(), 1L);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if(e.getView().getTitle().startsWith(ChatColor.GOLD + "Edição de Custos ("))
                    {
                        e.setCancelled(true);

                        if(e.getCurrentItem().getType().equals(Material.GOLD_NUGGET))
                        {
                           int page = getPage(e.getClickedInventory());

                            if(page > 0)
                            {
                                setBuy.add(p);
                                pager.put(p, page);
                                p.closeInventory();
                                KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.NONE, "Digite o valor de compra para a moeda");
                            }

                        } else if(e.getCurrentItem().getType().equals(Material.IRON_NUGGET)) {
                            int page = getPage(e.getClickedInventory());

                            if(page > 0)
                            {
                                setSell.add(p);
                                pager.put(p, page);
                                p.closeInventory();
                                KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.NONE, "Digite o valor de venda para a moeda");
                            }
                        } else if(e.getCurrentItem().getType().equals(Material.GREEN_TERRACOTTA)) {

                            int page = getPage(e.getClickedInventory());
                            Shop shop = WrenchInventories.getInstance().getShop(p);
                            shop.active(true);

                            p.closeInventory();
                            p.openInventory(WrenchInventories.getInstance().getBuySellInventory(p, shop, page));

                        } else if(e.getCurrentItem().getType().equals(Material.RED_TERRACOTTA)) {

                            int page = getPage(e.getClickedInventory());
                            Shop shop = WrenchInventories.getInstance().getShop(p);
                            shop.active(false);

                            p.closeInventory();
                            p.openInventory(WrenchInventories.getInstance().getBuySellInventory(p, shop, page));

                        } else if(e.getCurrentItem().getType().equals(Material.PLAYER_HEAD)) {
                            NBTManager nbt = new NBTManager(e.getCurrentItem().clone());

                            if(nbt.hasCustomTag("left"))
                            {
                                int page = (int) nbt.getCustomTag("left", NBTManager.ValueType.INT);
                                p.closeInventory();
                                p.openInventory(WrenchInventories.getInstance().getBuySellInventory(p, WrenchInventories.getInstance().getShop(p), page));
                            } else if(nbt.hasCustomTag("next")) {
                                int page = (int) nbt.getCustomTag("next", NBTManager.ValueType.INT);
                                p.closeInventory();
                                p.openInventory(WrenchInventories.getInstance().getBuySellInventory(p, WrenchInventories.getInstance().getShop(p), page));
                            }
                        } else if(e.getCurrentItem().getType().equals(Material.EMERALD)) {
                            NBTManager nbt = new NBTManager(e.getCurrentItem().clone());

                            if(nbt.hasCustomTag("shopID"))
                            {
                                int id = (int) nbt.getCustomTag("shopID", NBTManager.ValueType.INT);

                                if(WrenchInventories.getInstance().getShop(p).getId() == id)
                                {
                                    p.closeInventory();
                                    p.openInventory(WrenchInventories.getInstance().getMainWrench(p, WrenchInventories.getInstance().getShop(p)));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e)
    {
        if(changeName.contains(e.getPlayer()))
        {
            Shop shop = WrenchInventories.getInstance().getShop(e.getPlayer());
            shop.setShopName(e.getMessage());

            new BukkitRunnable(){
                @Override
                public void run() {
                    e.getPlayer().openInventory(WrenchInventories.getInstance().getMainWrench(e.getPlayer(), shop));
                }
            }.runTask(KEconomy.getInstance());

            changeName.remove(e.getPlayer());

            e.setCancelled(true);
        } else if(changePerm.contains(e.getPlayer())) {
            Shop shop = WrenchInventories.getInstance().getShop(e.getPlayer());

            if(!e.getMessage().equals("\"\""))
            {
                shop.setUsePermission(true);
                shop.setPermission(e.getMessage());
            } else {
                shop.setUsePermission(false);
            }

            new BukkitRunnable(){
                @Override
                public void run() {
                    e.getPlayer().openInventory(WrenchInventories.getInstance().getMainWrench(e.getPlayer(), shop));
                }
            }.runTask(KEconomy.getInstance());


            changePerm.remove(e.getPlayer());

            e.setCancelled(true);
        } else if(changeRarity.contains(e.getPlayer())) {
            if(Misc.getInstance().tryParseDouble(e.getMessage()))
            {
                Shop shop = WrenchInventories.getInstance().getShop(e.getPlayer());
                shop.setCustomRarity(Double.parseDouble(e.getMessage()));

                new BukkitRunnable(){
                    @Override
                    public void run() {
                        e.getPlayer().openInventory(WrenchInventories.getInstance().getMainWrench(e.getPlayer(), shop));
                    }
                }.runTask(KEconomy.getInstance());


                changeRarity.remove(e.getPlayer());
            } else KEconomy.getInstance().getMessager().sendMessage(e.getPlayer(), Messager.MessageType.ERROR, "Número inválido. Tente novamente.");

            e.setCancelled(true);
        } else if(setBuy.contains(e.getPlayer())) {
            e.setCancelled(true);
            if(Misc.getInstance().tryParseDouble(e.getMessage()))
            {
                Shop shop = WrenchInventories.getInstance().getShop(e.getPlayer());
                shop.setBuyValue(Double.parseDouble(e.getMessage()));

                setBuy.remove(e.getPlayer());
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        e.getPlayer().openInventory(WrenchInventories.getInstance().getBuySellInventory(e.getPlayer(), shop, pager.get(e.getPlayer())));
                    }
                }.runTask(KEconomy.getInstance());

                pager.remove(e.getPlayer());

            } else KEconomy.getInstance().getMessager().sendMessage(e.getPlayer(), Messager.MessageType.ERROR, "Número inválido. Tente novamente.");
        } else if(setSell.contains(e.getPlayer())) {
            e.setCancelled(true);
            if(Misc.getInstance().tryParseDouble(e.getMessage()))
            {
                Shop shop = WrenchInventories.getInstance().getShop(e.getPlayer());
                shop.setSellValue(Double.parseDouble(e.getMessage()));

                setSell.remove(e.getPlayer());
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        e.getPlayer().openInventory(WrenchInventories.getInstance().getBuySellInventory(e.getPlayer(), shop, pager.get(e.getPlayer())));
                    }
                }.runTask(KEconomy.getInstance());

                pager.remove(e.getPlayer());

            } else KEconomy.getInstance().getMessager().sendMessage(e.getPlayer(), Messager.MessageType.ERROR, "Número inválido. Tente novamente.");
        } else if(pBuy.contains(e.getPlayer())) {
            e.setCancelled(true);
            if(Misc.getInstance().tryParseDouble(e.getMessage()))
            {
                Player p = e.getPlayer();
                Shop shop = WrenchInventories.getInstance().getShop(p);
                shop.setBuyValue(Double.parseDouble(e.getMessage()));
                pBuy.remove(e.getPlayer());

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.openInventory(WrenchInventories.getInstance().getMainWrench(p, shop));
                    }
                }.runTask(KEconomy.getInstance());

            } else KEconomy.getInstance().getMessager().sendMessage(e.getPlayer(), Messager.MessageType.ERROR, "Número inválido. Tente novamente.");
        } else if(pSell.contains(e.getPlayer())) {
            e.setCancelled(true);
            if(Misc.getInstance().tryParseDouble(e.getMessage()))
            {
                Player p = e.getPlayer();
                Shop shop = WrenchInventories.getInstance().getShop(p);
                shop.setSellValue(Double.parseDouble(e.getMessage()));
                pSell.remove(e.getPlayer());

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.openInventory(WrenchInventories.getInstance().getMainWrench(p, shop));
                    }
                }.runTask(KEconomy.getInstance());

            } else KEconomy.getInstance().getMessager().sendMessage(e.getPlayer(), Messager.MessageType.ERROR, "Número inválido. Tente novamente.");
        }
    }

    private int getPage(Inventory inventory)
    {
        if(inventory.getMaxStackSize() >= 9)
        {
            if(inventory.getItem(8).getType().equals(Material.EMERALD))
            {
                NBTManager nbt = new NBTManager(inventory.getItem(8));
                if(nbt.hasCustomTag("page"))
                {
                    return (int) nbt.getCustomTag("page", NBTManager.ValueType.INT);
                } else return -1;
            } else return -1;
        } else return -1;
    }
}
