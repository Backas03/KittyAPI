package kr.kro.backas.kittyapi.inventory;

import com.google.common.base.Preconditions;
import kr.kro.backas.kittyapi.inventory.event.ContentClickEvent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Content {
    public static final Content EMPTY = new Content(Material.AIR);

    /**
     * 클릭시 다음 페이지로 이동하는 content를 생성합니다 <br>
     * <strong>주의:</strong> setOnClick 으로 callback 재정의시 다음페이지로 이동하지 않습니다.
     * @param item ItemStack Supplier
     * @param inventory next page 를 수행할 page inventory
     * @return content
     */
    @NotNull
    public static Content nextPage(@NotNull Supplier<@NotNull ItemStack> item, @NotNull PageInventory inventory) {
        final ItemStack itemStack = item.get();
        final Content content = new Content(itemStack);
        setupNextPageContent(content, inventory);
        return content;
    }

    /**
     * 클릭시 다음 페이지로 이동하는 content를 생성합니다 <br>
     * <strong>주의:</strong> setOnClick 으로 callback 재정의시 다음페이지로 이동하지 않습니다.
     * @param material material
     * @param inventory next page 를 수행할 page inventory
     * @return content
     */
    @NotNull
    public static Content nextPage(@NotNull Material material, @NotNull PageInventory inventory) {
        final Content content = new Content(new ItemStack(material));
        setupNextPageContent(content, inventory);
        return content;
    }

    /**
     * 클릭시 이전 페이지로 이동하는 content를 생성합니다 <br>
     * <strong>주의:</strong> setOnClick 으로 callback 재정의시 이전페이지로 이동하지 않습니다.
     * @param item ItemStack Supplier
     * @param inventory previous page 를 수행할 page inventory
     * @return content
     */
    @NotNull
    public static Content previousPage(@NotNull Supplier<@NotNull ItemStack> item, @NotNull PageInventory inventory) {
        final ItemStack itemStack = item.get();
        final Content content = new Content(itemStack);
        setupPreviousPageContent(content, inventory);
        return content;
    }

    /**
     * 클릭시 이전 페이지로 이동하는 content를 생성합니다 <br>
     * <strong>주의:</strong> setOnClick 으로 callback 재정의시 이전페이지로 이동하지 않습니다.
     * @param material material
     * @param inventory previous page 를 수행할 page inventory
     * @return content
     */
    @NotNull
    public static Content previousPage(@NotNull Material material, @NotNull PageInventory inventory) {
        final Content content = new Content(new ItemStack(material));
        setupPreviousPageContent(content, inventory);
        return content;
    }

    static void setupNextPageContent(Content content, PageInventory inventory) {
        content.setOnClick(event -> {
            inventory.nextPage();
        });
        content.setPinned(true);
    }

    static void setupPreviousPageContent(Content content, PageInventory inventory) {
        content.setOnClick(event -> {
            inventory.previousPage();
        });
        content.setPinned(true);
    }

    private final @NotNull ItemStack itemStack;
    private @Nullable Consumer<ContentClickEvent> onClick;
    private boolean pinned;

    public Content(@NotNull ItemStack itemStack, boolean pinned) {
        Preconditions.checkNotNull(
                itemStack,
                "ItemStack of Content cannot be null."
        );
        this.itemStack = itemStack;
        this.pinned = pinned;
    }

    public Content(@NotNull ItemStack itemStack) {
        this(itemStack, false);
    }

    public Content(@NotNull Material material) {
        this(new ItemStack(material));
    }

    public Content(@NotNull Material material, int amount) {
        this(new ItemStack(material, amount));
    }

    /**
     * 해당 content의 Bukkit ItemStack을 가져옵니다
     * @return 아이템
     */
    public @NotNull ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * 해당 content를 클릭 시 page inventory 에서 내부적으로<br>
     * 해당 content의 클릭 콜백 함수를 호출하기 위해 사용됩니다
     * @return callback
     */
    protected @Nullable Consumer<ContentClickEvent> getOnClick() {
        return this.onClick;
    }

    /**
     * 해당 content 클릭 시 호출될 콜백을 설정합니다 <br>
     * 콜백이 필요없을 시 null 파라메터로 제거할 수 있습니다 <br>
     * 해당 콜백은 Bukkit Event Listener가 호출되고 난 후 실행됩니다
     * @param callback 호출될 consumer
     */
    public void setOnClick(@Nullable Consumer<ContentClickEvent> callback) {
        this.onClick = callback;
    }

    /**
     * page inventory 에서 페이지 변경시 <br>
     * 해당 아이템을 자리에 그대로 고정할지의 여부를 가져옵니다
     * @return 고정되어 있으면 true, 아니면 false (기본 false)
     */
    public boolean isPinned() {
        return pinned;
    }

    /**
     * page inventory 에서 페이지 변경시 <br>
     * 해당 아이템을 자리에 그대로 고정할지의 여부를 설정합니다
     * @param pinned 고정하려면　true, 아니면 false
     */
    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }
}
