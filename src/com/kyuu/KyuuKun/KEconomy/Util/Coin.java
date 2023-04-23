package com.kyuu.KyuuKun.KEconomy.Util;

import com.kyuu.KyuuKun.Core.Util.DB.DBTable;

public interface Coin 
{	
	public String getName();
	public String getSymbol();
	public boolean isActive();
	public DBTable getTable();
	public void turnOff();
	public void turnOn();
	public String getConfigName();
	
	public default String getPermission()
	{
		return "kyuu.KEconomy.use." + getName();
	}
}
