package com.kyuu.KyuuKun.KEconomy.Shop;

import com.kyuu.KyuuKun.Core.Util.ItemBuilder;
import com.kyuu.KyuuKun.Core.Util.NBTManager;
import com.kyuu.KyuuKun.KEconomy.Main.KEconomy;
import com.kyuu.KyuuKun.KEconomy.Util.Coin;
import com.kyuu.KyuuKun.KEconomy.Util.Items;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class WrenchInventories
{
    private WrenchInventories(){}
    private static WrenchInventories wi = new WrenchInventories();
    public static WrenchInventories getInstance() { return wi; }

    private final HashMap<Player, Shop> using = new HashMap<Player, Shop>();

    public Inventory getMainWrench(Player p, Shop shop)
    {
        Inventory main = Bukkit.getServer().createInventory(p, InventoryType.CHEST, ChatColor.GOLD + "Edição de Loja");
        main.setMaxStackSize(27);

        ItemStack glass = new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).setName("").construct();
        ItemStack nether = new ItemBuilder(Material.NETHER_STAR).setName(ChatColor.GOLD + "Edição").construct();

        for(int i = 0; i < 27; i++)
        {
            main.setItem(i, glass);
        }

        ItemStack name = new ItemBuilder(Material.NAME_TAG).setName(shop.getShopName())
                .addLore(ChatColor.LIGHT_PURPLE + "Edite o nome da loja.").addLore(ChatColor.RED + "Obs:. O padrão é o nome do item.").construct();
        ItemStack buyValor = new ItemBuilder(Material.GOLD_INGOT).setName(ChatColor.GOLD + "Valores de Compra")
                .addLore(ChatColor.YELLOW + "Compra: " + isActive(shop.canBuy()))
                .addLore(ChatColor.LIGHT_PURPLE + "Clique para editar o valor de acordo com suas moedas ativas.").construct();
        ItemStack sellValor = new ItemBuilder(Material.IRON_INGOT).setName(ChatColor.GOLD + "Valores de Venda")
                .addLore(ChatColor.YELLOW + "Venda: " + isActive(shop.canSell()))
                .addLore(ChatColor.LIGHT_PURPLE + "Clique para editar o valor de acordo com suas moedas ativas.").construct();
        ItemStack permManager = null;
        ItemStack rarityType = null;
        ItemStack rarity = null;

        if(shop.usePermission())
        {
            permManager = new ItemBuilder(Material.HOPPER).setName(ChatColor.GRAY + "Permissão")
                    .addLore(ChatColor.YELLOW + "Permissão: " + isActive(shop.usePermission()) + " (" + shop.getPermission() + ")")
                    .addLore(ChatColor.LIGHT_PURPLE + "Edite a permissão da loja.").construct();
        } else {
            permManager = new ItemBuilder(Material.HOPPER).setName(ChatColor.GRAY + "Permissão")
                    .addLore(ChatColor.YELLOW + "Permissão: " + isActive(shop.usePermission()))
                    .addLore(ChatColor.LIGHT_PURPLE + "Edite a permissão da loja.").construct();
        }

        if(shop.getRarityType() == 0)
        {
            rarityType = new ItemBuilder(Material.GOLDEN_APPLE).setName(ChatColor.AQUA + "Raridade Padrão")
                    .addLore(ChatColor.LIGHT_PURPLE + "Clique para trocar o tipo de raridade").construct();
            rarity = new ItemBuilder(Material.HEART_OF_THE_SEA).setName(ChatColor.GOLD + "Raridade")
                .addLore(ChatColor.LIGHT_PURPLE + "A raridade deste item é de: " + shop.getRarity().getValue()).construct();
        } else if(shop.getRarityType() == 1) {
            rarityType = new ItemBuilder(Material.ENCHANTED_GOLDEN_APPLE).setName(ChatColor.AQUA + "Raridade Customizada")
                    .addLore(ChatColor.LIGHT_PURPLE + "Clique para trocar o tipo de raridade").construct();
            rarity = new ItemBuilder(Material.HEART_OF_THE_SEA).setName(ChatColor.GOLD + "Raridade")
                    .addLore(ChatColor.LIGHT_PURPLE + "A raridade deste item é de: " + shop.getCustomRarity())
                    .addLore(ChatColor.LIGHT_PURPLE + "Clique para mudar a raridade.").construct();
        }

        ItemStack cancel = new ItemBuilder(Material.BARRIER).setName(ChatColor.RED + "Cancelar").construct();
        ItemStack destroy = new ItemBuilder(Material.BONE).setName(ChatColor.RED + "Excluir").construct();
        ItemStack save = new ItemBuilder(Material.EMERALD).setName(ChatColor.GREEN + "Salvar").construct();

        ItemStack item = null;
        if(!shop.getItem().getType().equals(Material.AIR))
        {
            item = new ItemBuilder(shop.getItem().clone()).addLore(ChatColor.LIGHT_PURPLE + "Clique aqui e depois em seu \ninventário para mudar o item.").construct();
        }

        main.setItem(4, nether);
        main.setItem(10, name);
        main.setItem(11, buyValor);
        main.setItem(12, sellValor);
        if(item != null)
        {
            main.setItem(13, item);
        }
        main.setItem(14, permManager);
        main.setItem(15, rarityType);
        main.setItem(16, rarity);
        main.setItem(20, cancel);
        main.setItem(22, save);
        main.setItem(24, destroy);

        return main;
    }

    public Inventory getBuySellInventory(Player p, Shop shop, int page)
    {

        boolean hasPages = false;

        if(getMaxPages() > 1)
        {
            hasPages = true;
        } else page = 1;

        int[] indexes = coinsIndex(page);

        Inventory main = Bukkit.getServer().createInventory(p, 9*indexes.length, ChatColor.GOLD + "Edição de Custos (" + page + ")");

        ItemStack glass = new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).setName("").construct();

        for(int i = 0; i < main.getSize(); i++)
        {
            main.setItem(i, glass);
        }

        int first = 0;

        for(int index : indexes)
        {

            ItemStack buy = new ItemBuilder(Material.GOLD_NUGGET).setName(ChatColor.GOLD + "Dinheiro").addLore(ChatColor.YELLOW + "Compra: " + shop.getBuyValue())
                    .addLore(ChatColor.LIGHT_PURPLE + "Clique para alterar o preço").construct();
            ItemStack sell = new ItemBuilder(Material.IRON_NUGGET).setName(ChatColor.DARK_GREEN + "Dinheiro").addLore(ChatColor.GREEN + "Venda: " + shop.getSellValue())
                    .addLore(ChatColor.LIGHT_PURPLE + "Clique para alterar o preço").construct();

            NBTManager nbtBuy = new NBTManager(buy);
            NBTManager nbtSell = new NBTManager(sell);



            main.setItem(first, nbtBuy.construct());
            main.setItem(first + 1, nbtSell.construct());

            if(shop.isActive())
            {
                ItemStack block = new ItemBuilder(Material.GREEN_TERRACOTTA).setName(ChatColor.GREEN + "Moeda ativada").construct();
                main.setItem(first + 3, block);
            } else {
                ItemStack block = new ItemBuilder(Material.RED_TERRACOTTA).setName(ChatColor.RED + "Moeda desativada").construct();
                main.setItem(first + 3, block);
            }

            first += 9;
        }

        if(page == getMaxPages())
        {
            NBTManager nbtLeft = new NBTManager(Items.ARROW_LEFT.getItem().clone());
            nbtLeft.setCustomTag("left", page - 1);

            main.setItem(last(main.getSize()), nbtLeft.construct());
        } else if(page < getMaxPages() && page > 1) {

            NBTManager nbtLeft = new NBTManager(Items.ARROW_LEFT.getItem().clone());
            NBTManager nbtRight = new NBTManager(Items.ARROW_RIGHT.getItem().clone());

            nbtLeft.setCustomTag("left", page - 1);
            nbtRight.setCustomTag("next", page + 1);

            main.setItem(aLast(main.getSize()), nbtLeft.construct());
            main.setItem(last(main.getSize()), nbtRight.construct());

        } else if(page == 1) {
            NBTManager nbtRight = new NBTManager(Items.ARROW_RIGHT.getItem().clone());
            nbtRight.setCustomTag("next", page + 1);
            main.setItem(last(main.getSize()), nbtRight.construct());
        }

        ItemStack back = new ItemBuilder(Material.EMERALD).setName(ChatColor.LIGHT_PURPLE + "Voltar para o menu principal").construct();
        NBTManager nbtBack = new NBTManager(back);

        nbtBack.setCustomTag("shopID", shop.getId());
        nbtBack.setCustomTag("page", page);

        if(main.getSize() == 9)
        {
            main.setItem(7, nbtBack.construct());
        } else main.setItem(8, nbtBack.construct());

        return main;
    }

    public Inventory getSellInventory(Player p, Shop shop)
    {
        Inventory main = Bukkit.getServer().createInventory(p, 9,  ChatColor.DARK_GREEN + "Venda");

        System.out.println(shop.getSellValue());
        if(shop.getSellValue() > 0)
        {
            ItemStack item = new ItemBuilder(Material.IRON_NUGGET).setName(ChatColor.DARK_GREEN + "Dinheiro")
                    .addLore(ChatColor.LIGHT_PURPLE + "Valor: " + ChatColor.GREEN + shop.getSellValue() + " " + ChatColor.LIGHT_PURPLE).construct();

            NBTManager nbt = new NBTManager(item);
            nbt.setCustomTag("shopID", shop.getId());
            nbt.setCustomTag("price", shop.getSellValue());

            main.addItem(nbt.construct());
        }

        return main;
    }

    public Inventory getBuyInventory(Player p, Shop shop)
    {
        Inventory main = Bukkit.getServer().createInventory(p, 9,  ChatColor.GOLD + "Compra");

        if(shop.getBuyValue() > 0)
        {
            ItemStack item = new ItemBuilder(Material.GOLD_NUGGET).setName(ChatColor.GOLD + "Dinheiro")
                    .addLore(ChatColor.LIGHT_PURPLE + "Valor: " + ChatColor.YELLOW + shop.getBuyValue() + " " + ChatColor.LIGHT_PURPLE).construct();

            NBTManager nbt = new NBTManager(item);
            nbt.setCustomTag("shopID", shop.getId());
            nbt.setCustomTag("price", shop.getBuyValue());

            main.addItem(nbt.construct());
        }

        return main;
    }

    public void setUsing(Player p, Shop shop)
    {
        using.put(p, shop);
    }

    public void removeUsing(Player p)
    {
        if(using.containsKey(p))
        {
            using.remove(p);
        }
    }

    public boolean isUsing(Player p)
    {
        return using.containsKey(p);
    }

    public Shop getShop(Player p)
    {
        return using.get(p);
    }

    private String isActive(boolean b)
    {
        if(b)
        {
            return ChatColor.DARK_GREEN + "Ativada";
        } else return ChatColor.RED + "Desativada";
    }

    private int getMaxPages()
    {
        int r = 9;
        return r;
    }

    private int[] coinsIndex(int page)
    {
        int[] index = null;

        if(getMaxPages() > 1)
        {
            int start = page*6 - 6;
            int k = 0;

            int max = 0;

            if(max > 6)
            {
                max = 6;
            }

            int unt = start + max;

            index = new int[max];

            for(int i = start; i < unt; i++)
            {
                index[k] = i;
                k++;

                if(k == max)
                {
                    break;
                }
            }

        } else {
            int k = 0;
            index = new int[6];
            for(int i = 0; i < 6; i++)
            {
                index[k] = i;
                k++;
            }
        }

        return index;
    }

    private int aLast(int max)
    {
        return max - 2;
    }

    private int last(int max)
    {
        return max - 1;
    }

    private int nextMultiple(int i)
    {
        int multiple = 9;

        for(int j = 1; i > multiple; j++)
        {
            multiple = multiple*j;
        }

        return multiple;
    }
}
