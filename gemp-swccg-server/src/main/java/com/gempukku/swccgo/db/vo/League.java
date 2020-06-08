package com.gempukku.swccgo.db.vo;

import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.league.LeagueData;

import java.lang.reflect.Constructor;

public class League {
    private SwccgCardBlueprintLibrary _library;
    private int _cost;
    private String _name;
    private String _type;
    private String _clazz;
    private String _parameters;
    private int _status;
    private boolean _allowTimeExtensions;
    private boolean _allowSpectators;
    private boolean _showPlayerNames;
    private int _decisionTimeoutSeconds;
    private int _timePerPlayerMinutes;
    private LeagueData _leagueData;


    public League(SwccgCardBlueprintLibrary library, int cost, String name, String type, String clazz, String parameters, int status, boolean allowSpectators, boolean allowTimeExtensions, boolean showPlayerNames, int decisionTimeoutSeconds, int timePerPlayerMinutes) {
        _library = library;
        _cost = cost;
        _name = name;
        _type = type;
        _clazz = clazz;
        _parameters = parameters;
        _status = status;
        _allowTimeExtensions = allowTimeExtensions;
        _allowSpectators = allowSpectators;
        _showPlayerNames = showPlayerNames;
        _decisionTimeoutSeconds = decisionTimeoutSeconds;
        _timePerPlayerMinutes = timePerPlayerMinutes;
    }

    public int getCost() {
        return _cost;
    }

    public String getName() {
        return _name;
    }

    public String getType() {
        return _type;
    }

    public boolean getAllowSpectators() { return _allowSpectators; }

    public boolean getAllowTimeExtensions() { return _allowTimeExtensions; }

    public boolean getShowPlayerNames() { return _showPlayerNames; }

    public int getDecisionTimeoutSeconds() { return _decisionTimeoutSeconds; }

    public int getTimePerPlayerMinutes() { return _timePerPlayerMinutes; }

    public synchronized LeagueData getLeagueData() {
        if (_leagueData == null) {
            try {
                Class<?> aClass = Class.forName(_clazz);
                Constructor<?> constructor = aClass.getConstructor(SwccgCardBlueprintLibrary.class, String.class);
                _leagueData = (LeagueData) constructor.newInstance(_library, _parameters);
            } catch (Exception exp) {
                throw new RuntimeException("Unable to create LeagueData", exp);
            }
        }
        return _leagueData;
    }

    public int getStatus() {
        return _status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        League league = (League) o;

        if (_type != null ? !_type.equals(league._type) : league._type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return _type != null ? _type.hashCode() : 0;
    }
}
