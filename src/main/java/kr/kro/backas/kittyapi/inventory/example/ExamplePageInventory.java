package kr.kro.backas.kittyapi.inventory.example;

import kr.kro.backas.kittyapi.inventory.Content;
import kr.kro.backas.kittyapi.inventory.PageInventory;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

public class ExamplePageInventory extends PageInventory {
    public ExamplePageInventory() {
        super(54); // inventorySize == contentSize

        // 테스트 인벤토리 [1/3]
        this.setTitleSupplier(() -> Component.text(
                "테스트 인벤토리 ["
                        + this.getCurrentPage()
                        + "/"
                        + this.getMaxPage()
                        + "]"));

        this.setContent(1, 0, new ExampleContent(
                Material.STONE,
                Component.text("돌 입니다"))
        );
        // 2 페이지 3번째 슬롯
        // page = index / contentSize + 1  => 57 / 54 = 1
        // slot = index % contentSize      => 57 % 54 => 3
        this.setContent(57,  new ExampleContent(
                Material.BRICK,
                Component.text("택배로 벽돌이 도착했습니다"))
        );
        this.setContent(3, 0, new ExampleContent(
                Material.BRICK,
                Component.text("택배로 벽돌이 도착했습니다"))
        );
        // 이전페이지 아이템
        this.setContent(45, Content.previousPage(Material.PAPER, this));
        // 다음페이지 아이템
        this.setContent(53, Content.nextPage(Material.PAPER, this));

        // 커스텀 content
        Content content = new Content(Material.APPLE);
        content.setPinned(true); // 페이지가 바뀌어도 아이템을 고정
        content.setOnClick(event -> {
            event.getOriginEvent().getWhoClicked()
                    .sendMessage(Component.text("사과"));
        });
        this.setContent(1, 1, content);
    }
}
