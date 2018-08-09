package com.gempukku.swccgo.cards.set209.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ForfeitCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;


import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 •Chief Bast [Chief Bast (V)] 2
 Lore: Aide to Grand Moff Tarkin. Rarely underestimates enemies. Learned cunning and patience hunting big game as a youth.
 CHARACTER - Imperial
 POWER 2 ABILITY 2
 Text: [Pilot]2. While with Vader in a battle you lost, Imperials and Imperial starships may be forfeited directly from your hand (for printed forfeit value) to reduce attrition and/or battle damage.
 DEPLOY 2 FORFEIT 3
 [Pilot] [Warrior] [Set 9]
 */

public class Card209_034 extends AbstractImperial {
    public Card209_034() {
        super(Side.DARK, 2, 2, 2, 2, 3, Title.Chief_Bast, Uniqueness.UNIQUE);
        setLore("Aide to Grand Moff Tarkin. Rarely underestimates enemies. Learned cunning and patience hunting big game as a youth.");
        setGameText("[Pilot]2.  While with Vader in a battle you lost, Imperials and Imperial starships may be forfeited directly from your hand (for printed forfeit value) to reduce attrition and/or battle damage.");
        addIcons(Icon.VIRTUAL_SET_9, Icon.PILOT, Icon.WARRIOR);
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

}
