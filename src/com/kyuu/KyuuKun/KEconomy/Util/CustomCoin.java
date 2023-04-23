package com.kyuu.KyuuKun.KEconomy.Util;

import com.kyuu.KyuuKun.Core.Util.DB.DBTable;

public class CustomCoin implements Coin
{

	private String name;
	private String displayName;
	private String symbol;
	private boolean isActive;
	private DBTable dbt;
	
	public CustomCoin(String configName, String symbol, boolean is, DBTable dbt)
	{
		this.name = configName;
		this.symbol = symbol;
		this.isActive = is;
		this.dbt = dbt;

		if(configName.contains("_"))
		{
			this.displayName = configName.replaceAll("_", " ");
		} else this.displayName = configName;
	}
	
	@Override
	public String getName() {
		return displayName;
	}

	@Override
	public String getSymbol() {
		return symbol;
	}

	@Override
	public boolean isActive() {
		return isActive;
	}

	@Override
	public DBTable getTable() {
		return dbt;
	}

	@Override
	public void turnOff() {
		isActive = false;
	}

	@Override
	public void turnOn() {
		isActive = true;
	}

	@Override
	public String getConfigName() {
		return name;
	}

}
