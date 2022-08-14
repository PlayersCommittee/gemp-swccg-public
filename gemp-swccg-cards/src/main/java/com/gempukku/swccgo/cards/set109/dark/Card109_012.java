package com.gempukku.swccgo.cards.set109.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToDeployCostModifiersToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Enhanced Cloud City)
 * Type: Objective
 * Title: This Deal Is Getting Worse All The Time / Pray I Don't Alter It Any Further
 */
public class Card109_012 extends AbstractObjective {
    public Card109_012() {
        super(Side.DARK, 0, Title.This_Deal_Is_Getting_Worse_All_The_Time);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy one Cloud City battleground site. May deploy Secret Plans and/or All Wrapped Up. While this side up, once during each of your deploy phases, may take Bespin system, Bespin: Cloud City, Dark Deal or Cloud City Occupation into hand from Reserve Deck; reshuffle. Your [Cloud City], [Jabba's Palace] and [Special Edition] characters are immune to Goo Nee Tay when deploying to Bespin Locations. Flip this card if Dark Deal on table and you occupy Bespin System and Bespin: Cloud City.");
        addIcons(Icon.PREMIUM);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Cloud_City_battleground_site), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Cloud City battleground site to deploy";
                    }
                });
        action.appendOptionalEffect(
                new DeployCardsFromReserveDeckEffect(action, Filters.Secret_Plans, 0, 1, true, false) {
                    public String getChoiceText(int numCardsToChoose) {
                        return "Choose Secret Plans to deploy";
                    }
                });
        action.appendOptionalEffect(
                new DeployCardsFromReserveDeckEffect(action, Filters.All_Wrapped_Up, 0, 1, true, false) {
                    public String getChoiceText(int numCardsToChoose) {
                        return "Choose All Wrapped Up to deploy";
                    }
                });
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.THIS_DEAL_IS_GETTING_WORSE_ALL_THE_TIME__UPLOAD_CARD;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a Bespin system, Bespin: Cloud City, Dark Deal, or Cloud City Occupation into hand from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Bespin_system, Filters.Bespin_Cloud_City, Filters.Dark_Deal, Filters.Cloud_City_Occupation), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToDeployCostModifiersToLocationModifier(self, Filters.and(Filters.your(self),
                Filters.or(Icon.CLOUD_CITY, Icon.JABBAS_PALACE, Icon.SPECIAL_EDITION), Filters.character),
                Filters.Goo_Nee_Tay, Filters.Bespin_location));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Dark_Deal)
                && GameConditions.occupies(game, playerId, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Bespin_system)
                && GameConditions.occupies(game, playerId, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.Bespin_Cloud_City)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Flip");
            action.setActionMsg(null);
            // Perform result(s)
            action.appendEffect(
                    new FlipCardEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
