package com.gempukku.swccgo.cards.set211.light;

import com.gempukku.swccgo.cards.AbstractResistance;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ModifyTotalBattleDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Character
 * Subtype: Resistance
 * Title: Vice Admiral Holdo
 */
public class Card211_055 extends AbstractResistance {
    public Card211_055(){
        super(Side.LIGHT, 2, 3, 1, 3, 5, "Vice Admiral Holdo", Uniqueness.UNIQUE);
        setLore("Female leader.");
        setGameText("[Pilot] 2: any capital starship. During battle, if piloting a capital starship, may place it and Holdo out of play to add X to your total battle destiny, where X is that starship's armor + hyperspeed.");
        setPolitics(1);
        addIcons(Icon.EPISODE_VII, Icon.VIRTUAL_SET_11, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.ADMIRAL, Keyword.FEMALE, Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2, Filters.capital_starship));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isDuringBattle(game)
                && GameConditions.isPiloting(game, self, Filters.capital_starship)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Place Holdo and starship out of play");
            action.setActionMsg("Place Holdo and starship out of play");

            PhysicalCard capital = Filters.findFirstActive(game, self, Filters.and(Filters.capital_starship, Filters.hasPiloting(self)));

            final float armor = game.getModifiersQuerying().getArmor(game.getGameState(), capital);
            final float hyperspeed = game.getModifiersQuerying().getHyperspeed(game.getGameState(), capital);
            final float armorPlusHyperspeed = armor + hyperspeed;

            action.appendEffect(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            action.appendEffect(
                    new PlaceCardOutOfPlayFromTableEffect(action, capital));
            action.appendEffect(
                    new ModifyTotalBattleDestinyEffect(action, playerId, armorPlusHyperspeed));
            return Collections.singletonList(action);
        }
        return null;
    }
}