package com.gempukku.swccgo.cards.set111.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.BlownAwayCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.AddToBlownAwayForceLossEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Third Anthology)
 * Type: Objective
 * Title: Massassi Base Operations / One In A Million
 */
public class Card111_004_BACK extends AbstractObjective {
    public Card111_004_BACK() {
        super(Side.LIGHT, 7, Title.One_In_A_Million);
        setGameText("While this side up, may deploy Death Star system without completing Death Star Plans. Once during each of your deploy phases, may take one Rebel Tech, Death Star system, Attack Run or Proton Torpedoes into hand from Reserve Deck; reshuffle. Your total power is +3 in battles at systems. If Death Star is 'blown away,' adds 3 to Force lost for each opponent's Death Star site and, for remainder of game, your Force drains at battleground systems where you have a starfighter present with a pilot character aboard are each +2.");
        addIcons(Icon.PREMIUM);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        Condition deathStarBlownAway = new BlownAwayCondition(Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Death_Star, true)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, Filters.and(Filters.your(self), Filters.Death_Star_system), ModifyGameTextType.DEATH_STAR__MAY_DEPLOY_WITHOUT_COMPLETING_DEATH_STAR_PLANS));
        modifiers.add(new TotalPowerModifier(self, Filters.and(Filters.system, Filters.battleLocation), 3, playerId));
        modifiers.add(new ForceDrainModifier(self, Filters.and(Filters.battleground_system, Filters.wherePresent(self,
                Filters.and(Filters.your(self), Filters.starfighter, Filters.hasAboard(self, Filters.pilot_character)))),
                deathStarBlownAway, 2, playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.ONE_IN_A_MILLION__UPLOAD_CARD;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a Rebel Tech, Death Star system, Attack Run, or Proton Torpedoes into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Rebel_Tech, Filters.Death_Star_system, Filters.Attack_Run, Filters.Proton_Torpedoes), true));
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isBlownAwayCalculateForceLossStep(game, effectResult, Filters.and(CardSubtype.SYSTEM, Filters.title(Title.Death_Star, true)))) {
            int amountToAddToForceLoss = 3 * Filters.countTopLocationsOnTable(game, Filters.and(Filters.opponents(self), Filters.Death_Star_site, Filters.notIgnoredDuringEpicEventCalculation(true)));
            if (amountToAddToForceLoss > 0) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.skipInitialMessageAndAnimation();
                // Perform result(s)
                action.appendEffect(
                        new AddToBlownAwayForceLossEffect(action, game.getOpponent(self.getOwner()), amountToAddToForceLoss));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}