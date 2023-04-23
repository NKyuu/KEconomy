package com.kyuu.KyuuKun.KEconomy.Shop;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerShopEvent extends Event
{

    private static final HandlerList handlers = new HandlerList();

    private Player p;
    private Shop s;
    private ShopAction action;

    public PlayerShopEvent(Player p, Shop s, ShopAction action)
    {
        this.p = p;
        this.s = s;
        this.action = action;
    }

    public Player getPlayer() {
        return p;
    }

    public Shop getShop() {
        return s;
    }

    public ShopAction getAction() {
        return action;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
