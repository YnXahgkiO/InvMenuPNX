package back.invmenupnx.util;

import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ItemBuilder {

    private final String id;
    private int meta = 0;
    private int count = 1;
    private String name = null;
    private final List<String> lore = new ArrayList<>();
    private final List<int[]> enchantments = new ArrayList<>();
    private boolean glow = false;

    private ItemBuilder(String id) {
        this.id = id;
    }

    private ItemBuilder(String id, int meta) {
        this.id = id;
        this.meta = meta;
    }

    public static ItemBuilder of(String id) {
        return new ItemBuilder(id);
    }

    public static ItemBuilder of(String id, int meta) {
        return new ItemBuilder(id, meta);
    }

    public static ItemBuilder of(Item base) {
        ItemBuilder builder = new ItemBuilder(base.getId(), base.getDamage());
        builder.count = base.getCount();
        if (base.hasCustomName()) {
            builder.name = base.getCustomName();
        }
        if (base.getLore() != null && base.getLore().length > 0) {
            builder.lore.addAll(Arrays.asList(base.getLore()));
        }
        return builder;
    }

    public ItemBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ItemBuilder lore(String... lines) {
        this.lore.clear();
        this.lore.addAll(Arrays.asList(lines));
        return this;
    }

    public ItemBuilder lore(List<String> lines) {
        this.lore.clear();
        this.lore.addAll(lines);
        return this;
    }

    public ItemBuilder addLore(String line) {
        this.lore.add(line);
        return this;
    }

    public ItemBuilder amount(int count) {
        this.count = Math.max(1, Math.min(64, count));
        return this;
    }

    public ItemBuilder meta(int meta) {
        this.meta = meta;
        return this;
    }

    public ItemBuilder enchant(int enchantmentId, int level) {
        this.enchantments.add(new int[]{enchantmentId, level});
        return this;
    }

    // glow uses Unbreaking I — visible enchantment shimmer without gameplay effect
    public ItemBuilder glow() {
        this.glow = true;
        return this;
    }

    public Item build() {
        Item item = Item.get(id, meta, count);

        if (name != null) {
            item.setCustomName(name);
        }

        if (!lore.isEmpty()) {
            item.setLore(lore.toArray(new String[0]));
        }

        if (glow) {
            Enchantment unbreaking = Enchantment.getEnchantment(Enchantment.ID_DURABILITY);
            if (unbreaking != null) {
                unbreaking.setLevel(1);
                item.addEnchantment(unbreaking);
            }
        }

        for (int[] ench : enchantments) {
            Enchantment enchantment = Enchantment.getEnchantment(ench[0]);
            if (enchantment != null) {
                enchantment.setLevel(ench[1]);
                item.addEnchantment(enchantment);
            }
        }

        return item;
    }
}
