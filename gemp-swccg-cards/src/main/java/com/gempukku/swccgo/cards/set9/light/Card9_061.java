package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.CaptureWithSeizureEffect;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Objective
 * Title: There Is Good In Him / I Can Save Him
 */
public class Card9_061 extends AbstractObjective {
    public Card9_061() {
        super(Side.LIGHT, 0, Title.There_Is_Good_In_Him);
        setFrontOfDoubleSidedCard(true);
        setGameText("Deploy Chief Chirpa's Hut (with [Death Star II] Luke and Luke's Lightsaber there), Endor: Landing Platform and I Feel The Conflict. For remainder of game, you may not play Alter, Strangle, or Captive Fury. While this side up, your Force generation is +2 at Luke's site. While an Imperial is at Landing Platform, you may not Force drain or generate Force at Luke's location. When any Imperial is at Luke's site, Luke is captured (seized by an Imperial, if possible, even if not a warrior). Flip this card if Luke captured.");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Chief_Chirpas_Hut, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Chief Chirpa's Hut to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardToLocationFromReserveDeckEffect(action, Filters.and(Icon.DEATH_STAR_II, Filters.Luke), Filters.Chief_Chirpas_Hut, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose [Death Star II] Luke to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardToLocationFromReserveDeckEffect(action, Filters.Lukes_Lightsaber, Filters.Chief_Chirpas_Hut, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Luke's Lightsaber to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Landing_Platform, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Endor: Landing Platform to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.I_Feel_The_Conflict, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose I Feel The Conflict to deploy";
                    }
                });
        return action;
    }

    @Override
    protected RequiredGameTextTriggerAction getGameTextAfterDeploymentCompletedAction(String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.appendEffect(
                new AddUntilEndOfGameModifierEffect(action,
                        new MayNotPlayModifier(self, Filters.or(Filters.Alter, Filters.Strangle, Filters.Captive_Fury), playerId), null));
        return action;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        Condition imperialAtLandingPlatform = new AtCondition(self, Filters.Imperial, Filters.Landing_Platform);
        Filter lukesLocation = Filters.sameLocationAs(self, Filters.Luke);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceGenerationModifier(self, Filters.sameSiteAs(self, Filters.Luke), 2, playerId));
        modifiers.add(new MayNotForceDrainAtLocationModifier(self, lukesLocation, imperialAtLandingPlatform, playerId));
        modifiers.add(new GenerateNoForceModifier(self, lukesLocation, imperialAtLandingPlatform, playerId));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            final PhysicalCard luke = Filters.findFirstActive(game, self, Filters.and(Filters.Luke, Filters.at(Filters.site)));
            if (luke != null) {
                Filter imperialFilter = Filters.and(Filters.Imperial, Filters.atSameSite(luke), Filters.canEscortCaptive(luke, true));
                if (GameConditions.canSpot(game, self, imperialFilter)) {

                    final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                    action.setSingletonTrigger(true);
                    action.setText("Capture Luke");
                    // Choose target(s)
                    action.appendTargeting(
                            new ChooseCardOnTableEffect(action, opponent, "Choose Imperial to capture and seize " + GameUtils.getCardLink(luke), imperialFilter) {
                                @Override
                                protected void cardSelected(final PhysicalCard imperial) {
                                    action.addAnimationGroup(imperial);
                                    action.setActionMsg("Have " + GameUtils.getCardLink(imperial) + " capture and seize " + GameUtils.getCardLink(luke));
                                    // Perform result(s)
                                    action.appendEffect(
                                            new CaptureWithSeizureEffect(action, luke, imperial));
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeFlipped(game, self)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE_AND_EXCLUDED_FROM_BATTLE, Filters.and(Filters.Luke, Filters.captive))) {

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