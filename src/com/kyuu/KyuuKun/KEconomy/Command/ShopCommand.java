package com.kyuu.KyuuKun.KEconomy.Command;

import com.kyuu.KyuuKun.Core.Util.Messager;
import com.kyuu.KyuuKun.Core.Util.Misc;
import com.kyuu.KyuuKun.KEconomy.Main.KEconomy;
import com.kyuu.KyuuKun.KEconomy.Shop.Shop;
import com.kyuu.KyuuKun.KEconomy.Util.Coin;
import com.kyuu.KyuuKun.KEconomy.Util.Items;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ShopCommand implements CommandExecutor
{

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args)
    {
        if(sender instanceof Player)
        {
            Player p = (Player) sender;

            if(Misc.checkPlayer(p, "kyuu.keconomy.shopadmin"))
            {
                if(cmd.getName().equalsIgnoreCase("loja"))
                {
                    if(args.length == 0)
                    {
                        KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.NONE, "Lista de Comandos:\n" +
                                "/loja criar ------> Cria uma loja;\n" +
                                "/loja wrench -----> Pega o editor de lojas.");
                    } else if(args.length == 1) {
                        if(args[0].equalsIgnoreCase("criar"))
                        {
                        Shop shop = new Shop(Items.NEW_SHOP.getItem(), p.getLocation().clone(), "Loja em configuração",false, 0, false, 0, (byte) 0, Shop.ShopRarity.NORMAL);

                            for(Player player : Bukkit.getOnlinePlayers())
                            {
                                shop.send(player, true);
                            }

                            KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.SUCCESS, "Você criou uma loja. Use uma wrench para editá-la e salvá-la.");

                        } else if(args[0].equalsIgnoreCase("wrench")) {
                            p.getInventory().addItem(Items.WRENCH.getItem());

                            KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.SUCCESS, "A chave foi adicionada com sucesso!.");
                        } else KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.ERROR, "Argumentos desconhecidos.");
                    } else KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.ERROR, "Argumentos desconhecidos.");
                }
            } else KEconomy.getInstance().getMessager().sendMessage(p, Messager.MessageType.ERROR, "Você não tem permissão para executar este comando.");

        } else KEconomy.getInstance().getMessager().sendMessage(sender, Messager.MessageType.ERROR, "Apenas jogadores podem executar este comando.");
        return true;
    }
}
