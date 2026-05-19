# InvMenuPNX
Create and manage virtual inventories in PowerNukkitX.

Inspired by [Muqsit/InvMenu](https://github.com/Muqsit/InvMenu) for PocketMine-MP.

## Installation and setup
Place `InvMenuPNX.jar` in your server's `plugins/` folder.

> [!NOTE]
> You must declare InvMenuPNX as a dependency before you can use it.
> ```yaml
> # in plugin.yml:
> depend: [InvMenuPNX]
> ```

## Create a virtual inventory
Quick start — `InvMenu.create(InvMenuType.CHEST).send(player);` displays a virtual chest to a player.

`InvMenu.create(type)` creates an InvMenu instance. InvMenuPNX comes with pre-registered inventory types:

| Type | Slots |
|---|---|
| `InvMenuType.CHEST` | 27 |
| `InvMenuType.DOUBLE_CHEST` | 54 |
| `InvMenuType.HOPPER` | 5 |
| `InvMenuType.ENDER_CHEST` | 27 |
| `InvMenuType.SHULKER_BOX` | 27 |
| `InvMenuType.FURNACE` | 3 |
| `InvMenuType.BREWING_STAND` | 5 |
| `InvMenuType.DISPENSER` | 9 |
| `InvMenuType.DROPPER` | 9 |
| `InvMenuType.WORKBENCH` | 9 |

```java
InvMenu menu = InvMenu.create(InvMenuType.CHEST);
FakeInventory inventory = menu.getInventory();
```

As `inventory` extends PNX's base inventory, all standard inventory methods are available.
```java
menu.getInventory().setItem(0, ItemBuilder.of(ItemID.DIAMOND_SWORD).name("§bSword").build());
menu.getInventory().addItem(Item.get(ItemID.GOLD_INGOT));
menu.getInventory().setItem(3, Item.get(ItemID.ARROW));
```

To send a menu to a player:
```java
menu.send(player);
```

> [!TIP]
> One `InvMenu` can be sent to multiple players at once — they all view and edit the same inventory.
> ```java
> menu.send(player1);
> menu.send(player2);
> // or:
> menu.send(List.of(player1, player2));
> ```

## Set a custom title
```java
menu.setTitle("Custom Title");          // global title
menu.send(player, "Hello, " + player.getName()); // per-player title at send time
```

## Verify whether a menu was sent successfully
`send()` is not guaranteed to succeed — a player may be disconnected or another plugin may have cancelled the window open.
```java
menu.send(player, success -> {
    if (success) {
        // player is now viewing the menu
    }
});
```

## Monitor item clicks
InvMenu provides a listener to intercept every item interaction inside the menu.

- `txn.getPlayer()` — the player that clicked
- `txn.getItemClicked()` — the item that was clicked in the menu
- `txn.getItemClickedWith()` — the item the player held when clicking
- `txn.getSlot()` — the slot index that was clicked
- `txn.getAction()` — the underlying `InventoryTransactionEvent`

```java
menu.setListener(txn -> {
    Player player = txn.getPlayer();
    Item itemClicked = txn.getItemClicked();
    int slot = txn.getSlot();
    return txn.discard(); // or txn.proceed()
});
```

> [!TIP]
> Detecting clicks by item ID is more reliable than checking slot numbers — items can be rearranged without breaking your logic.
> ```java
> menu.setListener(txn -> {
>     if (txn.getItemClicked().getId().equals(ItemID.APPLE)) {
>         txn.getPlayer().sendMessage("You cannot take apples!");
>         return txn.discard();
>     }
>     return txn.proceed();
> });
> ```

## Make a menu read-only
Two equivalent approaches to prevent players from editing the menu:
```java
menu.setListener(txn -> txn.discard());

menu.setListener(InvMenu.readonly()); // shorthand

// readonly with a callback:
menu.setListener(InvMenu.readonly(txn -> {
    txn.getPlayer().sendMessage("Slot " + txn.getSlot() + " clicked.");
}));
```

## Execute a task after the menu closes (`then`)
Some actions — like opening another menu — must be done after the current inventory is closed.
Use `then()` on the transaction result to schedule a callback that fires once the menu is closed.
```java
menu.setListener(txn -> {
    return txn.discard().then(p -> {
        p.sendMessage("The menu is now closed.");
    });
});
```

## Monitor menu close events
```java
menu.setCloseListener((player, inventory) -> {
    player.sendMessage("You closed the menu.");
});
```

The close listener fires for both player-initiated closes and server-initiated closes (e.g. `menu.close(player)`).

## Close a menu from code
```java
menu.close(player);   // close for one player
menu.closeAll();      // close for all current viewers
```

## Register a custom inventory type
```java
CustomInvMenuType type = new CustomInvMenuType("myplugin:dispenser", FakeInventoryType.DISPENSER, 9);
InvMenuHandler.registerCustomType(type);

InvMenu menu = InvMenu.create("myplugin:dispenser");
```

You can also provide a custom `InvMenuGraphic` to control how the block is placed and removed for each player:
```java
CustomInvMenuType type = new CustomInvMenuType("myplugin:custom", FakeInventoryType.CHEST, 27, myGraphic);
```

## ItemBuilder
Utility class for building items quickly:
```java
Item item = ItemBuilder.of(ItemID.DIAMOND)
        .name("§bShiny Diamond")
        .lore("§7First line", "§7Second line")
        .count(5)
        .glow()
        .build();
```

## PagedInvMenu
Automatic pagination for large item lists, with built-in prev/next navigation.

```java
PagedInvMenu.create(InvMenuType.CHEST)
        .setTitle("§5My Catalog")
        .setItems(myItemList)
        .setListener((txn, index, item) -> {
            txn.getPlayer().sendMessage("Clicked item #" + index + ": " + item.getCustomName());
            return txn.discard();
        })
        .send(player);
```

## ConfirmMenu
Built-in yes/no dialog using a hopper inventory.

```java
ConfirmMenu.create()
        .setTitle("§cAre you sure?")
        .setQuestion(ItemBuilder.of(ItemID.FLINT_AND_STEEL).name("§cDelete everything?").build())
        .onConfirm(p -> p.sendMessage("§aConfirmed."))
        .onCancel(p -> p.sendMessage("§cCancelled."))
        .send(player);
```
