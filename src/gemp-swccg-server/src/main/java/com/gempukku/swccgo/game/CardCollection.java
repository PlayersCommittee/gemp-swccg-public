package com.gempukku.swccgo.game;

import java.util.Map;

public interface CardCollection extends OwnershipCheck {
    int getCurrency();

    Map<String, Item> getAll();

    int getItemCount(String blueprintId);
    Map<String, Object> getExtraInformation();
    void setExtraInformation(Map<String, Object> newExtraInformation);

    boolean excludePackDuplicates();

    class Item implements CardItem {
        public enum Type {
            PACK, CARD, SELECTION
        }

        private Type _type;
        private int _count;
        private String _blueprintId;

        private Item(Type type, int count, String blueprintId) {
            _type = type;
            _count = count;
            _blueprintId = blueprintId;
        }

        public static Item createItem(String blueprintId, int count) {
            if (blueprintId.startsWith("(S)"))
                return new Item(Type.SELECTION, count, blueprintId);
            else if (!blueprintId.contains("_"))
                return new Item(Item.Type.PACK, count, blueprintId);
            else
                return new Item(Item.Type.CARD, count, blueprintId);
        }

        public Type getType() {
            return _type;
        }

        public int getCount() {
            return _count;
        }

        @Override
        public String getBlueprintId() {
            return _blueprintId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Item item = (Item) o;

            if (_count != item._count) return false;
            if (_blueprintId != null ? !_blueprintId.equals(item._blueprintId) : item._blueprintId != null)
                return false;
            if (_type != item._type) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = _type != null ? _type.hashCode() : 0;
            result = 31 * result + _count;
            result = 31 * result + (_blueprintId != null ? _blueprintId.hashCode() : 0);
            return result;
        }
    }
}
