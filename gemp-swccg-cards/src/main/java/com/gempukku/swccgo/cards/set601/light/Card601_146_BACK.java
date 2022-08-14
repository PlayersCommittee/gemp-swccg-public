package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromBottomOfReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 4
 * Type: Objective
 * Title: Watch Your Step (V) / This Place Can Be A Little Rough (V)
 */
public class Card601_146_BACK extends AbstractObjective {
    public Card601_146_BACK() {
        super(Side.LIGHT, 7, Title.This_Place_Can_Be_A_Little_Rough);
        setVirtualSuffix(true);
        setGameText("While this side up, your Force generation is +1 at each system you control with a smuggler. Opponent's Force generation at non-battleground locations is limited to 1. When you have two or more smugglers in a battle, add one battle destiny. Each of your smugglers is forfeit +2. Once during each turn, may play one interrupt from Lost Pile as if from hand (then place that card out of play). Sense, Alter, and Keep Your Eyes Open may not be played. Flip this card if you do not occupy two battlegrounds (unless you have completed two Kessel Runs).");
        addIcons(Icon.REFLECTIONS_II, Icon.LEGACY_BLOCK_4);
        setAsLegacy(true);
        hideFromDeckBuilder();
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Corellia_site, Title.No_Escape));
        //TODO add extra cost to deploying a non-Corellian card with ability to Corellia
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.and(Filters.not(Filters.blaster), Filters.character_weapon, Filters.at(Filters.Corellia_site)), -2));
        modifiers.add(new ForceGenerationModifier(self, Filters.and(Filters.Corellia_site, Filters.spaceport_site), 1, playerId));
        modifiers.add(new MayNotDeployToLocationModifier(self, Filters.and(Filters.your(self), Filters.combat_vehicle), Filters.except(Filters.and(Filters.Corellia_site, Filters.at(Filters.and(Filters.opponents(playerId), Filters.vehicle))))));
        modifiers.add(new MayNotMoveToLocationModifier(self, Filters.and(Filters.your(self), Filters.combat_vehicle), Filters.except(Filters.and(Filters.Corellia_site, Filters.at(Filters.and(Filters.opponents(playerId), Filters.vehicle))))));
        modifiers.add(new ModifyGameTextModifier(self, Filters.title("Palejo Reshad"), ModifyGameTextType.LEGACY__PALEJO_RESHAD__TREAT_AUDIENCE_CHAMBER_AS_CORELLIA));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__THIS_PLACE_CAN_BE_A_LITTLE_ROUGH_V__DEPLOY_CHARACTER_FROM_RESERVE_DECK;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a character from Reserve Deck");
            action.setActionMsg("Deploy BoShek or a Corellian of ability < 3 from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.or(Filters.title("BoShek"), Filters.and(Filters.Corellian, Filters.abilityLessThan(3))), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isEndOfEachTurn(game, effectResult)
            && GameConditions.canUseForce(game, playerId, 1)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw bottom card of Reserve Deck");
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new DrawCardIntoHandFromBottomOfReserveDeckEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && !GameConditions.occupies(game, playerId, 2, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.battleground)) {

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