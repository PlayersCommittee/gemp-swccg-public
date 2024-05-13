package com.gempukku.swccgo.cards.set209.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ForfeitCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 9
 * Type: Character
 * Subtype: Imperial
 * Title: Chief Bast (V)
 */

public class Card209_034 extends AbstractImperial {
    public Card209_034() {
        super(Side.DARK, 2, 2, 2, 2, 3, Title.Chief_Bast, Uniqueness.UNIQUE, ExpansionSet.SET_9, Rarity.V);
        setLore("Aide to Grand Moff Tarkin. Rarely underestimates enemies. Learned cunning and patience hunting big game as a youth.");
        setGameText("[Pilot]2.  While with Vader in a battle you lost, Imperials and Imperial starships may be forfeited directly from your hand (for printed forfeit value) to reduce attrition and/or battle damage.");
        addIcons(Icon.VIRTUAL_SET_9, Icon.PILOT);
        setVirtualSuffix(true);
    }
    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {

        Filter withVaderInBattleFilter = Filters.and(self, Filters.inBattleWith(Filters.Vader));

        // Check condition(s) - With vader in battle and just forfeited card from hand
        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && GameConditions.canSpot(game, self, withVaderInBattleFilter)
                && GameConditions.isDuringBattleLostBy(game, playerId)
                && (GameConditions.isBattleDamageRemaining(game, playerId) || GameConditions.isAttritionRemaining(game, playerId))) {

            // Everything below is directly copied from Mantellian Savrip (just filter changes)
            final Collection<PhysicalCard> forfeitableCards = Filters.filter(game.getGameState().getHand(playerId), game,
                    Filters.or(Filters.Imperial, Filters.Imperial_starship));

            if (!forfeitableCards.isEmpty()) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Forfeit a card from hand");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardFromHandEffect(action, playerId, Filters.in(forfeitableCards)) {
                            @Override
                            protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                                // Perform result(s)
                                action.appendEffect(
                                        new ForfeitCardFromHandEffect(action, selectedCard));
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        // Adds 2 to power of anything he pilots
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

}
