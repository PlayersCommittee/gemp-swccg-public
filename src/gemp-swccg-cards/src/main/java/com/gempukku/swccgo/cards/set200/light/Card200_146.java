package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.takeandputcards.StackCardFromHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerForceLossEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeDisarmedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 0
 * Type: Character
 * Subtype: Jedi Master
 * Title: Master Qui-Gon (AI) (V)
 */
public class Card200_146 extends AbstractJediMaster {
    public Card200_146() {
        super(Side.LIGHT, 1, 7, 6, 7, 7, "Master Qui-Gon", Uniqueness.UNIQUE, ExpansionSet.SET_0, Rarity.V);
        setAlternateImageSuffix(true);
        setVirtualSuffix(true);
        setLore("Jedi Master currently not on the Council. Although he serves the Council well, there have been times when he has defied their wishes to pursue a path he believes is right.");
        setGameText("Deploys -1 to [Episode I] locations. If you just lost Force from an opponent's Effect, may [upload] an [Episode I] Interrupt (except Control), or place a card from hand under Credits Will Do Fine. May not be Disarmed. Immune to You Are Beaten and attrition.");
        addPersona(Persona.QUIGON);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.WARRIOR, Icon.VIRTUAL_SET_0);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Icon.EPISODE_I));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        GameTextActionId gameTextActionId = GameTextActionId.MASTER_QUIGON__UPLOAD_INTERRUPT_OR_STACK_CARD_ON_CREDITS_WILL_DO_FINE;

        // Check condition(s)
        if (TriggerConditions.justLostForceFromCard(game, effectResult, playerId, Filters.and(Filters.opponents(self), Filters.Effect))
                && GameConditions.isOncePerForceLoss(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Take card into hand from Reserve Deck");
                action.setActionMsg("Take an [Episode I] Interrupt (except Control) into hand from Reserve Deck");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerForceLossEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.Interrupt, Icon.EPISODE_I, Filters.except(Filters.Control)), true));
                actions.add(action);
            }

            if (GameConditions.hasHand(game, playerId)) {
                PhysicalCard credits = Filters.findFirstActive(game, self, Filters.Credits_Will_Do_Fine);
                if (credits != null) {

                    final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                    action.setText("Stack a card on Credits Will Do Fine");
                    action.setActionMsg("Stack a card from hand on Credits Will Do Fine");
                    // Update usage limit(s)
                    action.appendUsage(
                            new OncePerForceLossEffect(action));
                    // Perform result(s)
                    action.appendCost(
                            new StackCardFromHandEffect(action, playerId, credits, true));
                    actions.add(action);
                }
            }
        }
        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeDisarmedModifier(self));
        modifiers.add(new ImmuneToTitleModifier(self, Title.You_Are_Beaten));
        modifiers.add(new ImmuneToAttritionModifier(self));
        return modifiers;
    }
}
