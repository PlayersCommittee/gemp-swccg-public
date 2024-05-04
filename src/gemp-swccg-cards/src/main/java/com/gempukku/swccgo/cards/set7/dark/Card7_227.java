package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
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
import com.gempukku.swccgo.logic.effects.choose.DeployStackedCardsEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromHandEffect;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsIfFromHandModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Effect
 * Title: Dreaded Imperial Starfleet
 */
public class Card7_227 extends AbstractNormalEffect {
    public Card7_227() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Dreaded_Imperial_Starfleet, Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("The Empire uses short-range fighters and patrol craft to maintain a presence in every system within its scope of power.");
        setGameText("Deploy on your side of table. Once per turn, you may cancel a Force drain by placing here from hand any non-unique starfighter. Starfighters may deploy here as if from hand (if Effect canceled by opponent, any starfighters here may immediately deploy for free.)");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        String opponent = game.getOpponent(playerId);
        Filter filter = Filters.and(Filters.non_unique, Filters.starfighter);

        // Check condition(s)
        if (TriggerConditions.forceDrainInitiated(game, effectResult)
                && GameConditions.canCancelForceDrain(game, self)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.hasInHand(game, playerId, filter)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel Force drain");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new StackCardFromHandEffect(action, playerId, self, filter));
            // Perform result(s)
            action.appendEffect(
                    new CancelForceDrainEffect(action));
            actions.add(action);
        }

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isAboutToBeCanceledFromTableBy(game, effectResult, opponent, self)
                && GameConditions.hasStackedCards(game, self, Filters.starfighter)) {

            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy stacked starfighters for free");
            // Perform result(s)
            action.appendEffect(
                    new DeployStackedCardsEffect(action, self, Filters.starfighter, 0, Integer.MAX_VALUE, true));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsIfFromHandModifier(self, Filters.and(Filters.stackedOn(self), Filters.starfighter)));
        return modifiers;
    }
}