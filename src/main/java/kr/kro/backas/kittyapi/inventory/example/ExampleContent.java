package kr.kro.backas.kittyapi.inventory.example;

import kr.kro.backas.kittyapi.inventory.Content;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ExampleContent extends Content {
    public ExampleContent(@NotNull Material material, Component toSend) {
        super(material);
        this.setOnClick(event -> {
            if (!(event.getOriginEvent().getWhoClicked() instanceof Player player)) {
                return;
            }
            player.sendMessage(toSend);
        });
    }
}
