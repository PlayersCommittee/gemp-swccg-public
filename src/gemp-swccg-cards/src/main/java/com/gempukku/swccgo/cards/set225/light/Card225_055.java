package com.gempukku.swccgo.cards.set225.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.CaptureCharacterOnTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RestoreCardToNormalEffect;
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToFireWeaponModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 25
 * Type: Effect
 * Title: No Disintegrations! (V)
 */
public class Card225_055 extends AbstractNormalEffect {
    public Card225_055() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.No_Disintegrations, Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setLore("'There will be a substantial reward for the one who finds the Millennium Falcon. You are free to use any methods necessary, but I want them alive.'");
        setGameText("Deploy on table. Once per opponent's turn, if their warrior present where a [Cloud City] Rebel (except Luke) is about to be lost, opponent must choose: capture and seize that Rebel or lose 2 Force. For a bounty hunter to fire a weapon, opponent must first use 1 Force (2 if Vader there). [Immune to Alter.]");
        addIcons(Icon.DAGOBAH, Icon.CLOUD_CITY, Icon.VIRTUAL_SET_25);
        setVirtualSuffix(true);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Filter weaponCardCarriedByBountyHunter = Filters.and(Filters.weapon, Filters.attachedTo(Filters.bounty_hunter));
        Filter permanentWeaponBountyHunter = Filters.and(Filters.bounty_hunter, Filters.hasPermanentWeapon);
        Filter weaponOnBountyHunter = Filters.or(weaponCardCarriedByBountyHunter, permanentWeaponBountyHunter);
        Filter withVader = Filters.with(self, Filters.Vader);

        modifiers.add(new ExtraForceCostToFireWeaponModifier(self, Filters.and(weaponOnBountyHunter, Filters.not(withVader)), 1));
        modifiers.add(new ExtraForceCostToFireWeaponModifier(self, Filters.and(weaponOnBountyHunter, withVader), 2));
        return modifiers;
    }


    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        final String opponent = game.getOpponent(playerId);
        final GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        Filter ccRebelExceptLuke = Filters.and(Filters.icon(Icon.CLOUD_CITY), Filters.Rebel, Filters.except(Filters.Luke), Filters.with(self, Filters.and(Filters.opponents(self), Filters.warrior, Filters.presentAt(Filters.site))));
        if ((TriggerConditions.isAboutToBeLost(game, effectResult, ccRebelExceptLuke) 
                || TriggerConditions.isAboutToBeForfeitedToLostPile(game, effectResult, ccRebelExceptLuke))
                && GameConditions.isOnceDuringOpponentsTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            final AboutToLeaveTableResult aboutToLeaveTableResult = (AboutToLeaveTableResult) effectResult;
            final PhysicalCard cardToBeLost = aboutToLeaveTableResult.getCardAboutToLeaveTable();
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Make opponent choose");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new PlayoutDecisionEffect(action, opponent,
                            new MultipleChoiceAwaitingDecision("Choose effect", new String[]{"Capture and seize " + GameUtils.getFullName(cardToBeLost), "Lose 2 Force"}) {
                                @Override
                                protected void validDecisionMade(int index, String result) {
                                    if (index == 0) {
                                        game.getGameState().sendMessage(opponent + " chooses to capture and seize " + GameUtils.getCardLink(cardToBeLost));

                                        aboutToLeaveTableResult.getPreventableCardEffect().preventEffectOnCard(cardToBeLost);
                                        action.appendEffect(
                                                new RestoreCardToNormalEffect(action, cardToBeLost));
                                        action.appendEffect(
                                                new CaptureCharacterOnTableEffect(action, cardToBeLost, false, null, true));
                                    }
                                    else {
                                        game.getGameState().sendMessage(opponent + " chooses to lose 2 Force");
                                        action.appendEffect(
                                                new LoseForceEffect(action, opponent, 2, true));
                                    }
                                }
                            }
                    )
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}