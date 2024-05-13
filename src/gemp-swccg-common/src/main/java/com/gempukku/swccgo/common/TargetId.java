package com.gempukku.swccgo.common;

/**
 * Used to identify targets.
 */
public enum TargetId {
    DEPLOY_TARGET(101),
    EFFECT_TARGET_1(201),
    UTINNI_EFFECT_TARGET_1(301),
    UTINNI_EFFECT_TARGET_2(302),
    IMMEDIATE_EFFECT_TARGET_1(401),
    JEDI_TEST_APPRENTICE(501),
    JEDI_TEST_MENTOR(502);

    private int _value;

    TargetId(int value) {
        _value = value;
    }

    public int getIntValue() {
        return _value;
    }
}
