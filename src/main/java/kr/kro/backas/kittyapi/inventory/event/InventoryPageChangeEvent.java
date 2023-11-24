package kr.kro.backas.kittyapi.inventory.event;

import kr.kro.backas.kittyapi.inventory.PageInventory;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class InventoryPageChangeEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final PageInventory inventory;
    private final int previousPage;
    private final int currentPage;

    public InventoryPageChangeEvent(PageInventory inventory, int previousPage, int currentPage) {
        this.inventory = inventory;
        this.previousPage = previousPage;
        this.currentPage = currentPage;
    }

    public PageInventory getInventory() {
        return inventory;
    }

    public int getPreviousPage() {
        return previousPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
