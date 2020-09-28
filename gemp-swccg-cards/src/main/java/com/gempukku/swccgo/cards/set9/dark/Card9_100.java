package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ArmorModifier;
import com.gempukku.swccgo.logic.modifiers.HyperspeedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Character
 * Subtype: Imperial
 * Title: Captain Godherdt
 */
public class Card9_100 extends AbstractImperial {
    public Card9_100() {
        super(Side.DARK, 3, 2, 2, 2, 3, "Captain Godherdt", Uniqueness.UNIQUE);
        setLore("Elite fleet engineer. Technically brilliant. Key operator of magnetic signature sensors monitoring Star Destroyer hulls.");
        setGameText("Adds 3 to power of any capital starship he pilots. While aboard a Star Destroyer, adds 1 to armor, adds 1 to hyperspeed and, once during each of your move phases, may cancel Landing Claw at same system or sector.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT, Icon.WARRIOR);
        addKeyword(Keyword.CAPTAIN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter starDestroyerAboard = Filters.and(Filters.Star_Destroyer, Filters.hasAboard(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3, Filters.capital_starship));
        modifiers.add(new ArmorModifier(self, starDestroyerAboard, 1));
        modifiers.add(new HyperspeedModifier(self, starDestroyerAboard, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter targetFilter = Filters.and(Filters.Landing_Claw, Filters.atSameSystemOrSector(self));

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.MOVE)
                && GameConditions.isAboard(game, self, Filters.Star_Destroyer)
                && GameConditions.canTargetToCancel(game, self, SpotOverride.INCLUDE_CONCEALED, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, SpotOverride.INCLUDE_CONCEALED, targetFilter, Title.Landing_Claw);
            return Collections.singletonList(action);
        }
        return null;
    }
}
