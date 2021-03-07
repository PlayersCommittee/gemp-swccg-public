package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.StackCardFromVoidEffect;
import com.gempukku.swccgo.cards.evaluators.NegativeEvaluator;
import com.gempukku.swccgo.cards.evaluators.StackedEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.StackCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.ForceRetrievalModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.RetrieveForceResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block D
 * Type: Defensive Shield
 * Title: Do They Have A Code Clearance? (V)
 */
public class Card601_034 extends AbstractDefensiveShield {
    public Card601_034() {
        super(Side.DARK, Title.Do_They_Have_A_Code_Clearance);
        setLore("Imperial officers are always on the lookout for Rebel espionage.");
        setGameText("Plays on table. Outrider does not place Utinni Effects out of play. If opponent just retrieved Force using an Interrupt or Utinni Effect, you may stack that card here. Opponent's Force retrieval is reduced by X, where X = number of cards here.");
        addIcons(Icon.REFLECTIONS_III, Icon.GRABBER, Icon.LEGACY_BLOCK_D);
        setVirtualSuffix(true);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceRetrievalModifier(self, new NegativeEvaluator(new StackedEvaluator(self)), opponent));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Outrider, ModifyGameTextType.LEGACY__OUTRIDER_DOES_NOT_PLACE_UTINNI_EFFECTS_OUT_OF_PLAY));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.justRetrievedForceUsingCard(game, effectResult, opponent, Filters.or(Filters.Interrupt, Filters.Utinni_Effect))) {
            PhysicalCard sourceCard = ((RetrieveForceResult) effectResult).getSourceCard();
            if (GameConditions.canBeGrabbed(game, self, sourceCard)) {

                OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Stack " + GameUtils.getFullName(sourceCard) + " here");
                action.setActionMsg("Stack " + GameUtils.getCardLink(sourceCard) + " on " + GameUtils.getCardLink(self));
                // Perform result(s)
                action.appendEffect(
                        new StackCardFromTableEffect(action, sourceCard, self));
                action.appendEffect(
                        new StackCardFromVoidEffect(action, sourceCard, self));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}