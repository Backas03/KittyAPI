package kr.kro.backas.kittyapi.inventory;

import com.google.common.base.Preconditions;
import kr.kro.backas.kittyapi.inventory.event.ContentClickEvent;
import moe.caramel.acacia.api.inventory.AbstractInventory;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PageInventory extends AbstractInventory {
    private final Map<Integer, Content> contents;
    private final int inventorySize;
    private final int contentSize;
    private int currentPage;
    private Supplier<Component> titleSupplier;

    public PageInventory(
            final int inventorySize,
            final int contentSize,
            final @Nullable Supplier<@NotNull Map<@NotNull Integer, @NotNull Content>> contentsMap)
    {
        super(inventorySize);
        Preconditions.checkArgument(
                contentSize <= inventorySize,
                "content size cannot upper than inventory size! ContentData build failed"
        );
        this.contents = contentsMap != null ?
                contentsMap.get() : new HashMap<>();
        this.inventorySize = inventorySize;
        this.contentSize = contentSize;
        this.currentPage = 1;

        this.preInitializeInventory(); // 아이템 배치전에 호출
        /* inventory 아이템 배치 start */
        final Map<Integer, ? extends Content> contents = this.getContents(currentPage);
        for (final var entry : contents.entrySet()) {
            final int slot = entry.getKey();
            this.inventory.setItem(slot, this.onInventoryUpdate(currentPage, slot, entry.getValue()));
        }
    }

    public PageInventory(
            final int inventorySize,
            final int contentSize) {
        this(inventorySize, contentSize, null);
    }

    protected PageInventory(final int inventorySize) {
        this(inventorySize, inventorySize, null);
    }

    /**
     * 인벤토리 업데이트 또는 인벤토리가 열릴 때 <br>
     * 변경될 타이틀을 리턴해주는 supplier를 설정합니다 <br>
     * 파라메터가 null 이거나 supplier의 리턴값이 null 일 시 타이틀이 변경되지 않습니다.<br>
     * 타이틀 업데이트는 인벤토리가 열릴때 수행됩니다.
     * @param titleSupplier title 서플라이어
     */
    public final void setTitleSupplier(@Nullable Supplier<Component> titleSupplier) {
        this.titleSupplier = titleSupplier;
    }

    /**
     * 인벤토리에 아이템이 배치되기 전에 호출됩니다.
     * <br>(자유롭게 오버라이딩하여 코드를 작성해주세요)
     */
    protected void preInitializeInventory() { }

    /**
     * 컨텐츠 콜백 실행을 위해 내부적으로 사용합니다.
     *
     * @param event 이벤트
     */
    @Override
    public final void process(final @NotNull InventoryClickEvent event) {
        this.onClick(event);
        final Content content = this.getContent(currentPage, event.getRawSlot());
        final Consumer<ContentClickEvent> callback = content.getOnClick();
        ContentClickEvent contentClickEvent = new ContentClickEvent(
                event,
                this,
                content
        );
        Bukkit.getPluginManager().callEvent(contentClickEvent);
        if (callback != null) {
            // 콜백 실행
            callback.accept(contentClickEvent);
        }
    }

    /**
     * {@link #process(InventoryClickEvent)}의 작업을 수행하기 전, 수행할 작업을 작성합니다.
     *
     * @param event 이벤트
     */
    protected void onClick(final @NotNull InventoryClickEvent event) {

    }

    /**
     * 인벤토리가 열릴 때 인벤토리 제목 업데이트를 위해 내부적으로 사용합니다. <br>
     * 만약 추가 작업이 필요하다면 오버라이딩 후, 최상단에 {@code super.process(event);} 를 작성해주세요.
     *
     * @param event 이벤트
     */
    @Override
    public void process(final @NotNull InventoryOpenEvent event) {
        final Component title = titleSupplier != null ?
                titleSupplier.get() :
                null;
        if (title != null) event.titleOverride(title);
    }

    /**
     * {@link #updateInventory()}에서 아이템을 배치할 때 호출됩니다. <br>
     * 아이템을 수정하고 싶다면 이 메서드를 오버라이딩 하세요.
     *
     * @param page 페이지 번호
     * @param slot 슬롯 번호
     * @param content 배치할 아이템
     */
    @NotNull // TODO: 해당 API가 쓰일 일이 있을까?
    protected ItemStack onInventoryUpdate(final int page, final int slot, final @NotNull Content content) {
        return content.getItemStack();
    }

    /**
     * 최대 페이지 번호를 가져옵니다.
     * <br>
     * <p>
     * 1(페이지) + 마지막 아이템의 인덱스 / 한 페이지당 표현할 최대 컨텐츠 슬롯
     * <br>
     * <strong>예시)</strong> 한 페이지당 출력되는 최대 컨텐츠 슬롯이 45이며
     * 마지막 아이템의 인덱스가 47 일 때, 총 페이지 수는 2이다.
     * </p>
     *
     * @return 최대 페이지 번호
     */
    public final int getMaxPage() {
        if (this.contents.keySet().isEmpty()) return 1;
        return 1 + Collections.max(this.contents.keySet()) / (this.inventorySize);
    }

    /**
     * 이동할 수 있는 다음 페이지가 존재하지 확인합니다.
     *
     * @return 다음 페이지가 존재하는 경우 {@code true}를 반환
     */
    public final boolean hasNextPage() {
        return (this.currentPage < this.getMaxPage());
    }

    /**
     * 이동할 수 있는 이전 페이지가 존재하지 확인합니다.
     *
     * @return 이전 페이지가 존재하는 경우 {@code true}를 반환
     */
    public final boolean hasPreviousPage() {
        return (this.currentPage > 1);
    }

    /**
     * 이동할 수 있는 이전 페이지가 존재하는 경우, 이전 페이지로 이동합니다.
     * @return 이전 페이지로 이동되었다면 true 아니면 false
     */
    public final boolean previousPage() {
        if (!this.hasPreviousPage()) return false;
        this.currentPage--;
        this.updateInventory();
        return true;
    }

    /**
     * 이동할 수 있는 다음 페이지가 존재하는 경우, 다음 페이지로 이동합니다.
     * @return 다음페이지로 이동되었다면 true 아니면 false
     */
    public final boolean nextPage() {
        if (!this.hasNextPage()) return false;
        this.currentPage++;
        this.updateInventory();
        return true;
    }

    /**
     * 현재 페이지 번호를 가져옵니다.
     * @return 현재 페이지 번호
     */
    public final int getCurrentPage() {
        return currentPage;
    }

    /**
     * page 에 있는 모든 content를 map 에 담아 리턴합니다 <br>
     * <br>
     * key: 인벤토리 슬롯 번호 <br>
     * value: content 객체
     * @param page 불러올 page
     * @return page 에 있는 모든 content (air인 content 포함)
     */
    @NotNull
    public final Map<Integer, Content> getContents(final int page) {
        final int add = (page - 1) * (this.inventory.getSize() - this.contentSize);
        final int min = (page - 1) * (this.contentSize) + add; // page 변수에 따른 index 의 최솟값
        final int max = page * (this.contentSize) - 1 + add; // page 변수에 따른 index 의 최댓값
        final Map<Integer, Content> value = new HashMap<>();
        for (final var entry : this.contents.entrySet()) {
            final int index = entry.getKey();
            final Content content = entry.getValue();
            final int key = index % (this.inventory.getSize()); // 0 ~ 인벤토리 크기 (index 를 인벤토리 슬롯으로 변환)
            if (content.isPinned()) {  // 아이템이 고정 되어 있다면
                value.put(key, content);
                continue;
            }
            if (index >= min && index <= max && !value.containsKey(key)) { // 아이템이 key 슬롯에 없고 index 가 min ~ max 라면
                value.put(key, content);
            }
        }
        return value;
    }

    /**
     * 주어진 페이지 번호 및 슬롯의 컨텐츠를 가져옵니다.
     *
     * @param page 가져올 컨텐츠의 페이지 번호
     * @param slot 가져올 컨텐츠의 슬롯
     * @return 주어진 페이지 번호, 슬롯의 컨텐츠
     */
    @NotNull
    public final Content getContent(final int page, final int slot) {
        return this.getContents(page)
                .getOrDefault(slot, Content.EMPTY);
    }

    /**
     * 주어진 페이지 번호 및 슬롯에 이벤트가 없는 아이템을 배치합니다.
     *
     * @param page 아이템을 배치할 페이지의 번호
     * @param slot 아이템이 배치될 슬롯
     * @param content 정할 content설정할 content
     * @return this
     */
    @NotNull
    public final PageInventory setContent(final int page, final int slot, final @NotNull Content content) {
        final int index = (page - 1) * (this.contentSize) + slot;
        return this.setContent(index, content);
    }

    /**
     * 주어진 인덱스에 페이지와 상관없이 이벤트가 없는 아이템을 배치합니다.
     *
     * @param index 업데이트할 인덱스
     * @param content 설정할 content
     * @return this
     */
    @NotNull
    public final PageInventory setContent(final int index, final @NotNull Content content) {
        final int page = index / this.contentSize + 1;
        final int slot = index % this.contentSize;
        this.update0(index, this.onInventoryUpdate(page, slot, content));
        this.contents.put(index, content);
        return this;
    }

    /**
     * 현재 페이지의 아이템 목록으로 인벤토리를 업데이트합니다. <br>
     * <strong>주의:</strong> 트래픽을 많이 소모하는 작업입니다.
     */
    public final void updateInventory() {
        // Clear Inventory
        this.inventory.clear();

        // Update Title
        // 대부분 이벤트 캔슬이 발생했을 것으로, 트래픽 소모 방지는 포기해야하는 구조
        // (타이틀 업데이트 패킷, 인벤토리 이벤트 캔슬 패킷, StateId 증가로 인한 추가 패킷
        final Component title = titleSupplier != null ?
                titleSupplier.get() :
                null;
        if (title != null) {
            this.inventory.getViewers().forEach(viewer -> {
                viewer.getOpenInventory().sendTitleUpdate(title);
            });
        }

        // Fill Item
        final Map<Integer, ? extends Content> contents = this.getContents(currentPage);
        for (final var entry : contents.entrySet()) {
            final int slot = entry.getKey();
            this.inventory.setItem(slot, this.onInventoryUpdate(currentPage, slot, entry.getValue()));
        }
    }

    /**
     * index에 따라 인벤토리에 아이템을 슬롯에 맞게 배치합니다
     * @param index item의 인덱스
     * @param item 설정할 아이템
     */
    final void update0(final int index, final @NotNull ItemStack item) {
        final int idx = index % (this.inventory.getSize());
        this.inventory.setItem(idx, item);
    }
}
