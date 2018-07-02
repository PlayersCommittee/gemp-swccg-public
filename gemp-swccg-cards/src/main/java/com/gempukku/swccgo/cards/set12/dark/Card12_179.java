package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameActionProxyEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Objective
 * Title: My Lord, Is That Legal? / I Will Make It Legal
 */
public class Card12_179 extends AbstractObjective {
    public Card12_179() {
        super(Side.DARK, 0, Title.My_Lord_Is_That_Legal);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Galactic Senate and any other [Episode I] location. For remainder of game, Rebel and Imperial leaders of ability < 4 are politics +2. Counter Assault and Surprise Assault are canceled. Your Republic characters are immune to Goo Nee Tay. Cards on your Political Effects may deploy to table. At Galactic Senate, weapon destiny draws are -6, creatures are lost and game text of non-Republicans is canceled. Flip this card if you have 3 senators (or 2 senators, at least 1 with a blockade agenda) at Galactic Senate.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Galactic_Senate, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Galactic Senate to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.and(Icon.EPISODE_I, Filters.location, Filters.not(Filters.Galactic_Senate)), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose [Episode 1] location to deploy";
                    }
                });
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, final int gameTextSourceCardId) {
        final Filter atGalacticSenate = Filters.at(Filters.Galactic_Senate);

        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new PoliticsModifier(self, Filters.and(Filters.or(Filters.Rebel, Filters.Imperial), Filters.leader, Filters.abilityLessThan(4)), 2), null));
        final int permCardId = self.getPermanentCardId();
        action.appendEffect(
                new AddUntilEndOfGameActionProxyEffect(action,
                        new AbstractActionProxy() {
                            @Override
                            public List<TriggerAction> getRequiredBeforeTriggers(SwccgGame game, Effect effect) {
                                List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                PhysicalCard self = game.findCardByPermanentId(permCardId);

                                // Check condition(s)
                                if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Counter_Assault, Filters.Surprise_Assault))
                                        && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

                                    RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                                    // Build action using common utility
                                    CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
                                    actions.add(action);
                                }
                                return actions;
                            }
                        }
                ));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new ImmuneToDeployCostModifiersToLocationModifier(self, Filters.and(Filters.your(self), Filters.Republic_character), Filters.Goo_Nee_Tay, Filters.any), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayDeployAsIfFromHandModifier(self, Filters.stackedOn(self, Filters.and(Filters.your(self), Filters.Political_Effect))), null));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new EachWeaponDestinyModifier(self, atGalacticSenate, -6), null));
        action.appendEffect(
                new AddUntilEndOfGameActionProxyEffect(action,
                        new AbstractActionProxy() {
                            @Override
                            public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                PhysicalCard self = game.findCardByPermanentId(permCardId);

                                // Check condition(s)
                                if (TriggerConditions.isTableChanged(game, effectResult)) {
                                    Collection<PhysicalCard> creaturesAtSenate = Filters.filterAllOnTable(game, Filters.and(Filters.creature, atGalacticSenate));
                                    if (!creaturesAtSenate.isEmpty()) {

                                        final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                                        action.setSingletonTrigger(true);
                                        action.setText("Make creatures at Galactic Senate lost");
                                        action.setActionMsg("Make " + GameUtils.getAppendedNames(creaturesAtSenate) + " lost");
                                        // Perform result(s)
                                        action.appendEffect(
                                                new LoseCardsFromTableEffect(action, creaturesAtSenate, true));
                                        actions.add(action);
                                    }
                                }
                                return actions;
                            }
                        }
                ));
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new CancelsGameTextModifier(self, Filters.and(Filters.character, Filters.not(Filters.Republic_character), atGalacticSenate)), null));
        return action;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)) {
            Collection<PhysicalCard> yourSenatorsAtGalacticSenate = Filters.filterActive(game, self,
                    SpotOverride.INCLUDE_EXCLUDED_FROM_BATTLE, Filters.and(Filters.your(self), Filters.senator, Filters.at(Filters.Galactic_Senate)));
            if (yourSenatorsAtGalacticSenate.size() >= 3
                    || (yourSenatorsAtGalacticSenate.size() >= 2 && Filters.canSpot(yourSenatorsAtGalacticSenate, game, Filters.blockade_agenda))) {

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