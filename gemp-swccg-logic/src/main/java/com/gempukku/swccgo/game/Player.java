package com.gempukku.swccgo.game;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// This class represents a player in Gemp-Swccg.
//
public class Player {

    public enum Type {
        ADMIN("a"),
        DEACTIVATED("d"),
        LEAGUE_ADMIN("l"),
        PLAYTESTER("t"),
        PLAYTESTING_ADMIN("p"),
        COMMENTATOR("c"),
        COMMENTARY_ADMIN("m"),
        UNBANNED("u");

        private String _value;

        Type(String value) {
            _value = value;
        }

        public String getValue() {
            return _value;
        }

        public String toString() {
            return getValue();
        }

        public static Type getFromName(String typeString) {
            for (Type type : values()) {
                if (type.name().equalsIgnoreCase(typeString))
                    return type;
            }
            return null;
        }

        public static List<Type> getTypes(String typeString) {
            List<Type> types = new ArrayList<Type>();
            for (Type type : values()) {
                if (typeString.contains(type.getValue())) {
                    types.add(type);
                }
            }
            return types;
        }

        public static String getTypeString(List<Type> types) {
            StringBuilder sb = new StringBuilder();
            for (Type type : values()) {
                if (types.contains(type)) {
                    sb.append(type.getValue());
                }
            }
            return sb.toString();
        }
    }

    private int _id;
    private String _name;
    private String _password;
    private String _type;
    private Integer _lastLoginReward;
    private Date _bannedUntil;
    private String _createIp;
    private String _lastIp;

    public Player(int id, String name, String password, String type, Integer lastLoginReward, Date bannedUntil, String createIp, String lastIp) {
        _id = id;
        _name = name;
        _password = password;
        _type = type;
        _lastLoginReward = lastLoginReward;
        _bannedUntil = bannedUntil;
        _createIp = createIp;
        _lastIp = lastIp;
    }

    public int getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public String getPassword() {
        return _password;
    }

    public String getType() {
        return _type;
    }

    /**
     * Determines if the player has the specified type.
     * @return true or false
     */
    public boolean hasType(Type type) {
        return Type.getTypes(_type).contains(type);
    }

    public Integer getLastLoginReward() {
        return _lastLoginReward;
    }

    public void setLastLoginReward(int lastLoginReward) {
        _lastLoginReward = lastLoginReward;
    }

    public Date getBannedUntil() {
        return _bannedUntil;
    }

    public String getCreateIp() {
        return _createIp;
    }

    public String getLastIp() {
        return _lastIp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (_name != null ? !_name.equals(player._name) : player._name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return _name != null ? _name.hashCode() : 0;
    }

    public PlayerInfo GetUserInfo() {
        return new PlayerInfo(_name, _type);
    }

    public class PlayerInfo {
        public String name;
        public String type;

        public PlayerInfo(String name, String info) {
            this.name = name;
            type = info;
        }
    }
}

