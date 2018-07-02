package com.gempukku.swccgo.db.vo;

public final class CollectionType {
    public static final CollectionType MY_CARDS = new CollectionType("permanent", "My cards");
    public static final CollectionType ALL_CARDS = new CollectionType("default", "All cards");

    private final String _code;
    private final String _fullName;

    public CollectionType(String code, String fullName) {
        _code = code;
        _fullName = fullName;
    }

    public String getCode() {
        return _code;
    }

    public String getFullName() {
        return _fullName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CollectionType that = (CollectionType) o;

        if (_code != null ? !_code.equals(that._code) : that._code != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return _code != null ? _code.hashCode() : 0;
    }
}
