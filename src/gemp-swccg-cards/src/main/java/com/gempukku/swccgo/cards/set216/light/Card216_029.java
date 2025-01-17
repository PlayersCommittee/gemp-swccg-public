package com.gempukku.swccgo.cards.set216.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Character
 * Subtype: Alien
 * Title: Grogu
 */
public class Card216_029 extends AbstractAlien {
    public Card216_029() {
        super(Side.LIGHT, 2, 3, 2, 4, 4, "Grogu", Uniqueness.UNIQUE, ExpansionSet.SET_16, Rarity.V);
        setLore("");
        setGameText("May [download] (or deploy from Lost Pile) Foundling on Grogu. " +
                "Once per game, if present during battle, may cancel a non-[Immune to Sense] Interrupt or place your just lost Mandalorian in Used Pile. " +
                "Immune to attrition < 3.");
        addPersona(Persona.GROGU);
        addIcons(Icon.VIRTUAL_SET_16);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.GROGU__DOWNLOAD_FOUNDLING;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Foundling on Grogu from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(action, Filters.Foundling, Filters.sameCardId(self), true));
            actions.add(action);
        }
        if (GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Foundling on Grogu from Lost Pile");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToTargetFromLostPileEffect(action, Filters.Foundling, Filters.sameCardId(self), false));
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.GROGU__CANCEL_INTERRUPT_OR_PLACE_JUST_LOST_MANDALORIAN_IN_USED;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
            && GameConditions.isPresent(game, self)
            && GameConditions.isInBattle(game, self)
            && TriggerConditions.justLost(game, effectResult, Filters.and(Filters.your(self), Filters.Mandalorian))) {

            final PhysicalCard justLostCard = ((LostFromTableResult) effectResult).getCard();
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place " + GameUtils.getFullName(justLostCard) + " in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(justLostCard) + " in Used Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PutCardFromLostPileInUsedPileEffect(action, playerId, justLostCard, true));
            return Collections.singletonList(action);
        }

        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.GROGU__CANCEL_INTERRUPT_OR_PLACE_JUST_LOST_MANDALORIAN_IN_USED;

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.and(Filters.Interrupt, Filters.not(Filters.immune_to_Sense)))
                && GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isPresent(game, self)
                && GameConditions.isInBattle(game, self)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            action.appendUsage(new OncePerGameEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
