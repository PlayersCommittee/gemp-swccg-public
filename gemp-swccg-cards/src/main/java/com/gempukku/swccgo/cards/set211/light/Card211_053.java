package com.gempukku.swccgo.cards.set211.light;


import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipSingleSidedStackedCard;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromPileEffect;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsIfFromHandModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 11
 * Type: Effect
 * Title: Cloning Cylinders
 */
public class Card211_053 extends AbstractNormalEffect {
    public Card211_053() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Cloning_Cylinders, Uniqueness.UNIQUE, ExpansionSet.SET_11, Rarity.V);
        setGameText("Use 4 Force to deploy on table. During your draw phase, may stack a non-unique clone from your Lost Pile here face down. At the start of your turn, turn all cards here face up. You may deploy any face up card stacked here (as if from hand). [Immune to Alter]");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_11);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostModifier(self, 4));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        //
        // During draw phase, stack a non-unique clone here from lost pile (face down)
        //

        GameTextActionId gameTextActionId = GameTextActionId.CLONING_CYLINDERS_STACK_CARD;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isDuringYourPhase(game, playerId, Phase.DRAW)
                && GameConditions.hasLostPile(game, playerId))
        {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Stack clone from lost pile");
            action.setActionMsg("Stack non-unique clone from lost pile here on " + GameUtils.getCardLink(self));

            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));


            // Perform result(s) - Search lost pile for a non-unique clone
            Filter nonUniqueClone = Filters.and(Filters.clone, Filters.not(Filters.unique));
            action.appendEffect(
                    new ChooseCardFromPileEffect(action, playerId, Zone.LOST_PILE, playerId, nonUniqueClone) {
                        @Override
                        protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                            if (selectedCard != null) {

                                // Stack that card here face-down
                                action.appendEffect(
                                        new StackOneCardFromLostPileEffect(action, selectedCard, self, true, false, false));

                            }
                        }
                    }
            );

            actions.add(action);
        }


        return actions;
    }


    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();


        //
        // At the start of your turn, turn all cards here face up.
        //

        Filter faceDownCardsStackedFilter = Filters.and(Filters.stackedOn(self), Filters.face_down);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_5;

        // Check condition(s)
        if (TriggerConditions.isStartOfYourTurn(game, effectResult, self) &&
             GameConditions.hasStackedCards(game, self))
        {

            // See if any cards are face-down
            Collection<PhysicalCard> faceDownCardsStacked = Filters.filter(game.getGameState().getStackedCards(self), game, Filters.face_down);
            if (!faceDownCardsStacked.isEmpty())
            {
                // Flip over each of the cards
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Turn all cards on " + GameUtils.getCardLink(self) + " face up");
                action.setActionMsg("Turn all cards on " + GameUtils.getCardLink(self) + " face up");

                // Flip over each of the cards
                for (PhysicalCard faceDownCard: faceDownCardsStacked) {
                    action.appendEffect(
                            new FlipSingleSidedStackedCard(action, faceDownCard)
                    );
                    action.addAnimationGroup(faceDownCard);
                }

                actions.add(action);
            }

        }

        return actions;
    }


    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {

        //
        // You may deploy any face up card stacked here (as if from hand).
        //

        Filter stackedFaceUpHere = Filters.and(Filters.stackedOn(self, Filters.sameCardId(self)), Filters.not(Filters.face_down));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployAsIfFromHandModifier(self, stackedFaceUpHere));
        return modifiers;
    }
}
