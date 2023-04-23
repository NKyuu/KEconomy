package com.kyuu.KyuuKun.KEconomy.Main;

import java.util.logging.Logger;

import com.kyuu.KyuuKun.KEconomy.Command.ShopCommand;
import com.kyuu.KyuuKun.KEconomy.Shop.Shop;
import com.kyuu.KyuuKun.KEconomy.Shop.ShopListener;
import com.kyuu.KyuuKun.KEconomy.Shop.WrenchManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.kyuu.KyuuKun.Core.Util.FileManager;
import com.kyuu.KyuuKun.Core.Util.Messager;
import com.kyuu.KyuuKun.KEconomy.Listener.MainListener;

public class KEconomy extends JavaPlugin
{
	
	private FileManager manager;
	private Messager messager;
	
	private static KEconomy instance;
	private static Economy econ = null;
	
	@Override
	public void onEnable()
	{
		manager = new FileManager(this);
		manager.setup("KEconomy");

		if(manager.get("Shop", "id") == null)
        {
            manager.set("Shop", "id", 0);
        }

        if(manager.get("Shop", "useOP") == null)
        {
            manager.set("Shop", "useOP", true);
        }

		messager = new Messager(ChatColor.DARK_GREEN + "[" + ChatColor.RED + "KEconomy" + ChatColor.DARK_GREEN + "]");


		//getCommand("keconomy").setExecutor(new KEconomyCommand());
		//getCommand("conta").setExecutor(new AccountCommand());
		getCommand("loja").setExecutor(new ShopCommand());
		
		getServer().getPluginManager().registerEvents(new MainListener(), this);
		getServer().getPluginManager().registerEvents(new WrenchManager(), this);
		getServer().getPluginManager().registerEvents(new ShopListener(), this);

		instance = this;

		if (!setupEconomy() ) {
			Logger.getLogger("Minecraft").severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	public Economy getEconomy()
	{
		return econ;
	}
	
	/*private void createCoins()
	{
		YamlConfiguration config = manager.getConfig("Coins");
		
		for(String s : config.getKeys(false))
		{
			cs.add(new CustomCoin(s, (String) manager.get("Coins", s + ".symbol"), (boolean) manager.get("Coins", s + ".active"), createCoinTable(s)));
		}
	}*/
	
	/*private DBTable createCoinTable(String coinName)
	{
		DBTable dbt = new DBTable(coinName, this);
		
		TableCol id = new TableCol("id", SQLType.INT);
	    id.addModifier(ColModifiers.INCREMENT);
	    id.addModifier(ColModifiers.NOT_NULL);
	    id.addModifier(ColModifiers.PRIMARY_KEY);
	    
	   	TableCol uuid = new TableCol("uuid", SQLType.STRING, 255);
	   	uuid.addModifier(ColModifiers.NOT_NULL);
	   	
	   	TableCol money = new TableCol("money", SQLType.DOUBLE);
	   	money.addModifier(ColModifiers.NOT_NULL);
	   	
	   	dbt.addTableCol(id);
	   	dbt.addTableCol(uuid);
	   	dbt.addTableCol(money);

	   	if(dbm != null)
	   	{
	   		dbm.querySet(sqlgen.createTable(dbt));
	   		return dbt;
	   	} return null;
	}*/
	
	@Override
	public void onDisable()
	{
		if(Shop.getShops().size() > 0)
		{
			for(Shop shop : Shop.getShops())
			{
				shop.destroySlime();
			}
		}

		HandlerList.unregisterAll(this);

	}
	
	public static KEconomy getInstance() { return instance; }
	public FileManager getFileManager() { return manager; }
	public Messager getMessager() { return messager; }
	
}
