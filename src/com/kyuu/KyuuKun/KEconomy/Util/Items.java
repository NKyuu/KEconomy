package com.kyuu.KyuuKun.KEconomy.Util;

import com.kyuu.KyuuKun.Core.Util.ItemBuilder;
import com.kyuu.KyuuKun.Core.Util.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum Items
{
    WRENCH(new ItemBuilder(Material.DIAMOND_HOE).setName(ChatColor.GOLD + "Wrench").addLore(ChatColor.DARK_PURPLE + "Use para editar lojas!")),
    NEW_SHOP(new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(Misc.getInstance().createPlayerProfile("MHF_Question"))
            .setName("Nova Loja")),
    ARROW_RIGHT(new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(Misc.getInstance().createPlayerProfile("MHF_ArrowRight"))
            .setName("Próximo")),
    ARROW_LEFT(new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner(Misc.getInstance().createPlayerProfile("MHF_ArrowLeft"))
            .setName("Voltar"));
    private ItemBuilder builder;
    Items(ItemBuilder builder)
    {
        this.builder = builder;
    }

    public ItemBuilder getBuilder()
    {
        return builder;
    }

    public ItemStack getItem()
    {
        return builder.construct();
    }
}
