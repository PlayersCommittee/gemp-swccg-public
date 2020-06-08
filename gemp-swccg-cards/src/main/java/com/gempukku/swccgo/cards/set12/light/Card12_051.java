package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Effect
 * Title: Your Insight Serves You Well & Staging Areas
 */
public class Card12_051 extends AbstractNormalEffect {
    public Card12_051() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Your Insight Serves You Well & Staging Areas", Uniqueness.UNIQUE);
        addComboCardTitles(Title.Your_Insight_Serves_You_Well, Title.Staging_Areas);
        setGameText("Deploy on table. Your Force generation is +1 at each docking bay you occupy. Opponent's Dark Jedi are defense value -1. Scanning Crew and 3,720 to 1 are canceled. You may place Effect in Lost Pile to take one [Endor] or [Death Star II] Effect that deploys for free into hand from Reserve Deck; reshuffle. (Immune to Alter.)");
        addIcons(Icon.CORUSCANT);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceGenerationModifier(self, Filters.and(Filters.docking_bay, Filters.occupies(playerId)), 1, playerId));
        modifiers.add(new DefenseValueModifier(self, Filters.and(Filters.opponents(playerId), Filters.Dark_Jedi), -1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        Filter filter = Filters.or(Filters.Scanning_Crew, Filters._3720_To_1);

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, filter)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        // Check condition(s)
        if (TriggerConditions.justRevealedInsertCard(game, effectResult, Filters._3720_To_1)) {
            if (GameConditions.canCancelRevealedInsertCard(game, self, effectResult)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelRevealedInsertCardAction(action, effectResult);
                actions.add(action);
            }
        }
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            if (GameConditions.canTargetToCancel(game, self, Filters.Scanning_Crew)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.Scanning_Crew, Title.Scanning_Crew);
                actions.add(action);
            }
            if (GameConditions.canTargetToCancel(game, self, Filters._3720_To_1)) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, Filters._3720_To_1, Title._3720_To_1);
                actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.YOUR_INSIGHT_SERVES_YOU_WELL_STAGING_AREAS__UPLOAD_EFFECT;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Effect into hand from Reserve Deck");
            action.setActionMsg("Take an [Endor] or [Death Star II] Effect into hand from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new LoseCardFromTableEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.or(Icon.ENDOR, Icon.DEATH_STAR_II), Filters.Effect, Filters.deploysForFree), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}