package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.ModifyPowerEffect;
import com.gempukku.swccgo.logic.effects.CancelGameTextEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToDagobahLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Character
 * Subtype: Alien
 * Title: Daughter Of Lap'lamiz
 */
public class Card304_147 extends AbstractAlien {
    public Card304_147() {
        super(Side.LIGHT, 1, 7, 4, 5, 8, "Daughter Of Lap'lamiz", Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLore("After recovering her memories stolen in the attack by Sykes, Komilia has decided to return to her free-style wanderings away from both of her parents and their politics.");
        setGameText("Deploys -3 on Seraph, Ulress, or Koudooine. May be targeted instead of Locita by Hostile Takeover. During battle, may target one opponent's [CSP] character present. Draw destiny. If destiny > ability, target is power -1 and its game text is canceled. Immune to attrition < 4.");
        addPersona(Persona.KOMILIA);
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.FEMALE);
        setSpecies(Species.ALDERAANIAN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToTargetModifier(self, -3, Filters.or(Filters.Deploys_on_Seraph, Filters.Deploys_at_Ulress, Filters.Deploys_at_Koudooine)));
        modifiers.add(new MayDeployToDagobahLocationModifier(self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        // Check condition(s) for the first action
        if (GameConditions.canSpot(game, self, Filters.and(Filters.Hostile_Takeover, Filters.not(Filters.hasGameTextModification(ModifyGameTextType.HOSTILE_TAKEOVER__TARGETS_KOMILIA_INSTEAD_OF_LOCITA))))) {
            final TopLevelGameTextAction action1 = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action1.setText("Hostile Takeover target Komilia");
            action1.setActionMsg("Hostile Takeover / Usurped target Komilia instead of Locita for remainder of game");
            // Perform result(s)
            action1.appendEffect(
                    new AddUntilEndOfGameModifierEffect(action1, new ModifyGameTextModifier(self,
                            Filters.or(Filters.Hostile_Takeover, Filters.Usurped), ModifyGameTextType.HOSTILE_TAKEOVER__TARGETS_KOMILIA_INSTEAD_OF_LOCITA),
                            "Hostile Takeover / Usurped target Komilia instead of Locita for remainder of game"));
            actions.add(action1);
        }

        // once per battle
        GameTextActionId gameTextActionId = GameTextActionId.DAUGHTER_OF_LAPLAMIZ_GAMETEXT_ONCE_PER_BATTLE;

        // "target one opponent's character present"
        Filter targetFilter = Filters.and(Filters.opponents(self), Filters.CSP_character, Filters.present(self));

        if (GameConditions.isInBattle(game, self)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canTarget(game, self, targetFilter)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Target opponent's [CSP] character");

            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Pay cost(s)
                            // Once Per battle
                            action.appendUsage(new OncePerBattleEffect(action));
                            // Allow response(s)
                            action.allowResponses("Targeting " + GameUtils.getCardLink(targetedCard),
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                            return Collections.singletonList(finalTarget);
                                                        }

                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            GameState gameState = game.getGameState();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }

                                                            float ability = game.getModifiersQuerying().getAbility(game.getGameState(), finalTarget);
                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                            gameState.sendMessage("Ability: " + GuiUtils.formatAsString(ability));
                                                            if (totalDestiny > ability) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(new ModifyPowerEffect(action, targetedCard, -2));
                                                                action.appendEffect(new CancelGameTextEffect(action, finalTarget));
                                                            } else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                    }
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 4));
        return modifiers;
    }
}