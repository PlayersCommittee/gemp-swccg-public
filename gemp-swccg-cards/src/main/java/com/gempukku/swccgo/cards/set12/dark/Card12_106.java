package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Alien
 * Title: Gardulla the Hutt
 */
public class Card12_106 extends AbstractAlien {
    public Card12_106() {
        super(Side.DARK, 2, 3, 3, 3, 5, "Gardulla The Hutt", Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.U);
        setLore("Well-known as a gambler and a gangster. Gardulla was the former owner of Shmi and Anakin Skywalker before she lost them in a bet to the Toydarian junk dealer, Watto.");
        setGameText("When you retrieve force from Boonta Eve Podrace, any or all of your retrieved force may be taken into hand. Once during your control phase, may use 1 force; opponent draws 1 destiny. If destiny > 2, retrieve 1 force. Otherwise, lose 1 force.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
        setSpecies(Species.HUTT);
        addKeywords(Keyword.GANGSTER, Keyword.GAMBLER, Keyword.FEMALE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.Boonta_Eve_Podrace, ModifyGameTextType.BOONTA_EVE_PODRACE__RETRIEVE_FORCE_INTO_HAND));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(playerId);
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.canDrawDestiny(game, opponent)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setActionMsg("Make opponent draw destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 1)
            );
            // Perform result(s)
            action.appendEffect(
                    new DrawDestinyEffect(action, opponent) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            if (totalDestiny > 2) {
                                action.appendEffect(
                                        new RetrieveForceEffect(action, playerId, 1)
                                );
                            } else {
                                action.appendEffect(
                                        new LoseForceEffect(action, playerId, 1)
                                );
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}