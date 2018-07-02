package com.gempukku.swccgo.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class defines static SpotOverride maps to use as overrides when determining which inactive
 * cards are visible to the action being performed.
 */
public class SpotOverride {

    public static final Map<InactiveReason, Boolean> INCLUDE_ALL = includeAllMap();
    private static Map<InactiveReason, Boolean> includeAllMap() {
        Map<InactiveReason, Boolean> aMap = new HashMap<InactiveReason, Boolean>();
        aMap.put(InactiveReason.CAPTIVE, Boolean.TRUE);
        aMap.put(InactiveReason.CONCEALED, Boolean.TRUE);
        aMap.put(InactiveReason.EXCLUDED_FROM_BATTLE, Boolean.TRUE);
        aMap.put(InactiveReason.MISSING, Boolean.TRUE);
        aMap.put(InactiveReason.STOLEN_WEAPON_DEVICE, Boolean.TRUE);
        aMap.put(InactiveReason.SUSPENDED, Boolean.TRUE);
        aMap.put(InactiveReason.UNDERCOVER, Boolean.TRUE);
        return Collections.unmodifiableMap(aMap);
    }

    public static final Map<InactiveReason, Boolean> INCLUDE_CAPTIVE = includeCaptiveMap();
    private static Map<InactiveReason, Boolean> includeCaptiveMap() {
        Map<InactiveReason, Boolean> aMap = new HashMap<InactiveReason, Boolean>();
        aMap.put(InactiveReason.CAPTIVE, Boolean.TRUE);
        return Collections.unmodifiableMap(aMap);
    }

    public static final Map<InactiveReason, Boolean> INCLUDE_CAPTIVE_AND_EXCLUDED_FROM_BATTLE = includeCaptiveAndExcludedFromBattleMap();
    private static Map<InactiveReason, Boolean> includeCaptiveAndExcludedFromBattleMap() {
        Map<InactiveReason, Boolean> aMap = new HashMap<InactiveReason, Boolean>();
        aMap.put(InactiveReason.CAPTIVE, Boolean.TRUE);
        aMap.put(InactiveReason.EXCLUDED_FROM_BATTLE, Boolean.TRUE);
        return Collections.unmodifiableMap(aMap);
    }

    public static final Map<InactiveReason, Boolean> INCLUDE_CONCEALED = includeConcealedMap();
    private static Map<InactiveReason, Boolean> includeConcealedMap() {
        Map<InactiveReason, Boolean> aMap = new HashMap<InactiveReason, Boolean>();
        aMap.put(InactiveReason.CONCEALED, Boolean.TRUE);
        return Collections.unmodifiableMap(aMap);
    }

    public static final Map<InactiveReason, Boolean> INCLUDE_EXCLUDED_FROM_BATTLE = includeExcludedFromBattleMap();
    private static Map<InactiveReason, Boolean> includeExcludedFromBattleMap() {
        Map<InactiveReason, Boolean> aMap = new HashMap<InactiveReason, Boolean>();
        aMap.put(InactiveReason.EXCLUDED_FROM_BATTLE, Boolean.TRUE);
        return Collections.unmodifiableMap(aMap);
    }

    public static final Map<InactiveReason, Boolean> INCLUDE_MISSING = includeMissingMap();
    private static Map<InactiveReason, Boolean> includeMissingMap() {
        Map<InactiveReason, Boolean> aMap = new HashMap<InactiveReason, Boolean>();
        aMap.put(InactiveReason.MISSING, Boolean.TRUE);
        return Collections.unmodifiableMap(aMap);
    }

    public static final Map<InactiveReason, Boolean> INCLUDE_MISSING_AND_UNDERCOVER = includeMissingAndUndercoverMap();
    private static Map<InactiveReason, Boolean> includeMissingAndUndercoverMap() {
        Map<InactiveReason, Boolean> aMap = new HashMap<InactiveReason, Boolean>();
        aMap.put(InactiveReason.MISSING, Boolean.TRUE);
        aMap.put(InactiveReason.UNDERCOVER, Boolean.TRUE);
        return Collections.unmodifiableMap(aMap);
    }

    public static final Map<InactiveReason, Boolean> INCLUDE_STOLEN = includeStolenMap();
    private static Map<InactiveReason, Boolean> includeStolenMap() {
        Map<InactiveReason, Boolean> aMap = new HashMap<InactiveReason, Boolean>();
        aMap.put(InactiveReason.STOLEN_WEAPON_DEVICE, Boolean.TRUE);
        return Collections.unmodifiableMap(aMap);
    }

    public static final Map<InactiveReason, Boolean> INCLUDE_SUSPENDED = includeSuspendedMap();
    private static Map<InactiveReason, Boolean> includeSuspendedMap() {
        Map<InactiveReason, Boolean> aMap = new HashMap<InactiveReason, Boolean>();
        aMap.put(InactiveReason.SUSPENDED, Boolean.TRUE);
        return Collections.unmodifiableMap(aMap);
    }

    public static final Map<InactiveReason, Boolean> INCLUDE_UNDERCOVER = includeUndercoverMap();
    private static Map<InactiveReason, Boolean> includeUndercoverMap() {
        Map<InactiveReason, Boolean> aMap = new HashMap<InactiveReason, Boolean>();
        aMap.put(InactiveReason.UNDERCOVER, Boolean.TRUE);
        return Collections.unmodifiableMap(aMap);
    }
}
