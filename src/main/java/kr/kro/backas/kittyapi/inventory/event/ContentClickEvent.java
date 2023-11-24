package kr.kro.backas.kittyapi.inventory.event;

import kr.kro.backas.kittyapi.inventory.Content;
import kr.kro.backas.kittyapi.inventory.PageInventory;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class ContentClickEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final InventoryClickEvent originEvent;
    private final PageInventory pageInventory;
    private final Content content;

    public ContentClickEvent(
            @NotNull InventoryClickEvent event,
            @NotNull PageInventory pageInventory,
            @NotNull Content content) {
        this.originEvent = event;
        this.pageInventory = pageInventory;
        this.content = content;
    }

    public InventoryClickEvent getOriginEvent() {
        return originEvent;
    }

    public PageInventory getPageInventory() {
        return pageInventory;
    }

    public Content getContent() {
        return content;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
