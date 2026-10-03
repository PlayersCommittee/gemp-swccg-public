package com.gempukku.swccgo.game;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Default hall bots (~OzzelBot, ~YodaBot, ~Rando_Cal) always count as playtesters
 * so a playtester can play a playtesting format against them.
 */
public class DefaultHallBotsPlaytesterTests {

    private static Player player(String name, String type) {
        return new Player(1, name, "x", type, null, null, null, null);
    }

    @Test
    public void OzzelBotIsAlwaysPlaytester() {
        Player bot = player("~OzzelBot", "");
        assertTrue(Player.isDefaultHallBot("~OzzelBot"));
        assertTrue(bot.hasType(Player.Type.PLAYTESTER));
        assertFalse(bot.hasType(Player.Type.ADMIN));
    }

    @Test
    public void YodaBotIsAlwaysPlaytester() {
        Player bot = player("~YodaBot", null);
        assertTrue(bot.hasType(Player.Type.PLAYTESTER));
        assertFalse(bot.hasType(Player.Type.PLAYTESTING_ADMIN));
    }

    @Test
    public void RandoCalIsAlwaysPlaytester() {
        Player bot = player("~Rando_Cal", "");
        assertTrue(bot.hasType(Player.Type.PLAYTESTER));
    }

    @Test
    public void HumanWithoutFlagIsNotPlaytester() {
        Player human = player("bill", "");
        assertFalse(Player.isDefaultHallBot("bill"));
        assertFalse(human.hasType(Player.Type.PLAYTESTER));
    }

    @Test
    public void HumanWithFlagIsPlaytester() {
        Player human = player("bill", "t");
        assertTrue(human.hasType(Player.Type.PLAYTESTER));
    }
}
