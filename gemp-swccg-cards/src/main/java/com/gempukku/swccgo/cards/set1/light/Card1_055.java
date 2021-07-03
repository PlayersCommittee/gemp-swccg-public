package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ForfeitCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Effect
 * Title: Mantellian Savrip
 */
public class Card1_055 extends AbstractNormalEffect {
    public Card1_055() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Mantellian_Savrip, Uniqueness.UNIQUE);
        setLore("Dejarik game uses mythological and real creatures from across the galaxy. The hulking Mantellian savrip is a nasty predator from Ord Mantell.");
        setGameText("Use 3 Force to deploy on your side of table (free if C-3PO on table). After losing any battle: characters, starships and vehicles may be forfeited directly from your hand (for forfeit value) to reduce attrition or battle damage.");
        addKeywords(Keyword.DEJARIK);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 3));
        modifiers.add(new DeploysFreeModifier(self, new OnTableCondition(self, Filters.C3PO)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && GameConditions.isDuringBattleLostBy(game, playerId)
                && (GameConditions.isBattleDamageRemaining(game, playerId) || GameConditions.isAttritionRemaining(game, playerId))) {
            final Collection<PhysicalCard> forfeitableCards = Filters.filter(game.getGameState().getHand(playerId), game,
                    Filters.or(Filters.character, Filters.starship, Filters.vehicle));
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