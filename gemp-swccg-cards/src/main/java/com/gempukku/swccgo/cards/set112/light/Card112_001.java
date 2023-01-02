package com.gempukku.swccgo.cards.set112.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.LeaderModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsIfFromHandModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Premium (Jabba's Palace Sealed Deck)
 * Type: Objective
 * Title: Agents In The Court / No Love For The Empire
 */
public class Card112_001 extends AbstractObjective {
    public Card112_001() {
        super(Side.LIGHT, 0, Title.Agents_In_The_Court, ExpansionSet.JPSD, Rarity.PM);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Hutt Trade Route and a Jabba's Palace site. May deploy Yarna d'al' Gargan. Reveal one unique (•) alien from your deck whose lore specifies its species. This card is your Rep. For remainder of game, your Rep is a leader. Yarna d'al' Gargan is immune to Alter. You may not deploy 'insert' cards or operatives. While a rancor is at Rancor Pit, Trap Door is immune to Bo Shuda. Flip this card if you occupy two battleground sites (must occupy a third with a non-unique alien of your Rep's species if a non-Tatooine location is on table).");
        addIcons(Icon.PREMIUM);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Hutt_Trade_Route, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Hutt Trade Route to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Jabbas_Palace_site, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Jabba's Palace site to deploy";
                    }
                });
        action.appendRequiredEffect(
                new ChooseCardFromReserveDeckEffect(action, playerId, Filters.and(Filters.unique, Filters.alien, Filters.hasSpecies)) {
                    @Override
                    protected void cardSelected(SwccgGame game, PhysicalCard rep) {
                        GameState gameState = game.getGameState();
                        gameState.setRep(playerId, rep);
                        gameState.sendMessage(playerId + " reveals " + GameUtils.getCardLink(rep) + " as Rep");
                        gameState.showCardOnScreen(rep);
                        self.setWhileInPlayData(new WhileInPlayData(rep));
                    }
                    @Override
                    public String getChoiceText(int numCardsToChoose) {
                        return "Choose Rep";
                    }
                }
        );
        action.appendOptionalEffect(
                new DeployCardsFromReserveDeckEffect(action, Filters.Yarna_dal_Gargan, 0, 1, true, false) {
                    public String getChoiceText(int numCardsToChoose) {
                        return "Choose Yarna d'al' Gargan to deploy";
                    }
                });
        return action;
    }

    @Override
    public String getDisplayableInformation(SwccgGame game, PhysicalCard self) {
        PhysicalCard rep = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getPhysicalCard() : null;
        if (rep != null) {
            return  "Rep is " + GameUtils.getCardLink(rep);
        }
        return null;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        PhysicalCard rep = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getPhysicalCard() : null;
        if (rep == null) {
            return null;
        }

        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new LeaderModifier(self, Filters.and(Filters.your(playerId), Filters.sameTitle(rep))), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new ImmuneToTitleModifier(self, Filters.Yarna_dal_Gargan, Title.Alter), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new SpecialFlagModifier(self, ModifierFlag.MAY_NOT_DEPLOY_INSERT_CARDS, playerId), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotDeployModifier(self, Filters.operative, playerId), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new ImmuneToTitleModifier(self, Filters.Trap_Door,
                                new AtCondition(self, Filters.Rancor, Filters.Rancor_Pit), Title.Bo_Shuda), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayDeployAsIfFromHandModifier(self, Filters.and(Filters.stackedOn(self), Filters.your(playerId), Filters.sameTitle(rep))), null));
        return action;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        PhysicalCard rep = self.getWhileInPlayData() != null ? self.getWhileInPlayData().getPhysicalCard() : null;
        if (rep == null) {
            return null;
        }
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)) {
            boolean flipIt = false;
            if (GameConditions.canSpotLocation(game, Filters.non_Tatooine_location)) {
                if (GameConditions.occupies(game, playerId, 3, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.battleground_site)
                        && GameConditions.occupiesWith(game, self, playerId, Filters.battleground_site, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.non_unique, Filters.alien, Filters.species(rep.getBlueprint().getSpecies())))) {
                    flipIt = true;
                }
            }
            else if (GameConditions.occupies(game, playerId, 2, SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.battleground_site)) {
                flipIt = true;
            }

            if (flipIt) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Flip");
                action.setActionMsg(null);
                // Perform result(s)
                action.appendEffect(
                        new FlipCardEffect(action, self));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}