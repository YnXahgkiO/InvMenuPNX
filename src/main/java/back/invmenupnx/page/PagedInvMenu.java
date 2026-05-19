package back.invmenupnx.page;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import back.invmenupnx.InvMenu;
import back.invmenupnx.InvMenuCloseListener;
import back.invmenupnx.transaction.InvMenuTransactionResult;
import back.invmenupnx.type.InvMenuType;
import back.invmenupnx.util.ItemBuilder;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PagedInvMenu {

    public static PagedInvMenu create(InvMenuType type) {
        return new PagedInvMenu(type);
    }

    private static Item defaultPrevButton() {
        return ItemBuilder.of(ItemID.ARROW).name("§7◄ Previous page").build();
    }

    private static Item defaultNextButton() {
        return ItemBuilder.of(ItemID.ARROW).name("§7Next page ►").build();
    }

    private static Item defaultPageInfo(int page, int totalPages) {
        return ItemBuilder.of(ItemID.BOOK).name("§ePage " + (page + 1) + " / " + totalPages).build();
    }

    private static Item defaultFiller() {
        return ItemBuilder.of(ItemID.STAINED_GLASS_PANE, 7).name("§r").build();
    }

    private final InvMenuType type;
    private final List<Item> items = new ArrayList<>();
    private String title = "Menu";
    private PagedItemListener listener;
    private InvMenuCloseListener closeListener;

    private final Map<UUID, InvMenu> openMenus = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> currentPages = new ConcurrentHashMap<>();

    private PagedInvMenu(InvMenuType type) {
        this.type = type;
    }

    public PagedInvMenu setTitle(String title) {
        this.title = title;
        return this;
    }

    public PagedInvMenu addItem(Item item) {
        this.items.add(item);
        return this;
    }

    public PagedInvMenu addItems(Collection<Item> items) {
        this.items.addAll(items);
        return this;
    }

    public PagedInvMenu setItems(List<Item> items) {
        this.items.clear();
        this.items.addAll(items);
        return this;
    }

    public PagedInvMenu setListener(PagedItemListener listener) {
        this.listener = listener;
        return this;
    }

    public PagedInvMenu setCloseListener(InvMenuCloseListener closeListener) {
        this.closeListener = closeListener;
        return this;
    }

    private int getItemsPerPage() {
        int rows = type.getSize() / 9;
        if (rows <= 1) return type.getSize();
        return (rows - 1) * 9;
    }

    public int getPageCount() {
        int ipp = getItemsPerPage();
        if (ipp <= 0) return 1;
        return Math.max(1, (int) Math.ceil((double) items.size() / ipp));
    }

    // [prevSlot, infoSlot, nextSlot] — null for inventories without a nav row (e.g. HOPPER)
    @Nullable
    private int[] getNavSlots() {
        int rows = type.getSize() / 9;
        if (rows <= 1) return null;
        int baseSlot = (rows - 1) * 9;
        return new int[]{baseSlot, baseSlot + 4, baseSlot + 8};
    }

    public void send(Player player) {
        send(player, 0);
    }

    public void send(Player player, int page) {
        UUID uuid = player.getUniqueId();
        page = Math.max(0, Math.min(page, getPageCount() - 1));

        InvMenu existing = openMenus.get(uuid);
        if (existing != null) {
            if (player.getWindowId(existing.getInventory()) != -1) {
                currentPages.put(uuid, page);
                fillPage(existing, page, player);
                return;
            }
            openMenus.remove(uuid);
            currentPages.remove(uuid);
        }

        final int finalPage = page;
        InvMenu menu = InvMenu.create(type).setTitle(title);
        openMenus.put(uuid, menu);
        currentPages.put(uuid, finalPage);

        fillPage(menu, finalPage, null);

        menu.setListener(txn -> {
            int slot = txn.getSlot();
            int[] nav = getNavSlots();
            int currentPage = currentPages.getOrDefault(uuid, 0);

            if (nav != null) {
                if (slot == nav[0]) {
                    if (currentPage > 0) {
                        int newPage = currentPage - 1;
                        currentPages.put(uuid, newPage);
                        fillPage(menu, newPage, txn.getPlayer());
                    }
                    return txn.discard();
                }
                if (slot == nav[2]) {
                    if (currentPage < getPageCount() - 1) {
                        int newPage = currentPage + 1;
                        currentPages.put(uuid, newPage);
                        fillPage(menu, newPage, txn.getPlayer());
                    }
                    return txn.discard();
                }
                if (slot == nav[1] || slot > nav[0]) {
                    return txn.discard();
                }
            }

            int ipp = getItemsPerPage();
            int itemIndex = currentPage * ipp + slot;
            if (itemIndex < items.size() && listener != null) {
                return listener.onItem(txn, itemIndex, items.get(itemIndex));
            }
            return txn.discard();
        });

        menu.setCloseListener((p, inv) -> {
            openMenus.remove(uuid);
            currentPages.remove(uuid);
            if (closeListener != null) {
                closeListener.onClose(p, inv);
            }
        });

        menu.send(player);
    }

    private void fillPage(InvMenu menu, int page, Player player) {
        var inv = menu.getInventory();
        int ipp = getItemsPerPage();
        int totalPages = getPageCount();
        int[] nav = getNavSlots();

        for (int i = 0; i < type.getSize(); i++) {
            inv.setItem(i, Item.AIR);
        }

        for (int i = 0; i < ipp; i++) {
            int itemIndex = page * ipp + i;
            if (itemIndex < items.size()) {
                inv.setItem(i, items.get(itemIndex));
            }
        }

        if (nav != null) {
            for (int i = nav[0]; i < type.getSize(); i++) {
                inv.setItem(i, defaultFiller());
            }
            if (page > 0) {
                inv.setItem(nav[0], defaultPrevButton());
            }
            inv.setItem(nav[1], defaultPageInfo(page, totalPages));
            if (page < totalPages - 1) {
                inv.setItem(nav[2], defaultNextButton());
            }
        }

        if (player != null && player.getWindowId(inv) != -1) {
            inv.sendContents(player);
        }
    }
}
