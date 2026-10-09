package com.gempukku.swccgo.cards.set227.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.RaceDestinyStackedResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 27
 * Type: Character
 * Subtype: Jedi Master
 * Title: Qui-Gon Jinn (V)
 */
public class Card227_022 extends AbstractJediMaster {
    public Card227_022() {
        super(Side.LIGHT, 1, 7, 6, 7, 9, Title.QuiGon_Jinn, Uniqueness.UNIQUE, ExpansionSet.SET_27, Rarity.V);
        setVirtualSuffix(true);
        setLore("Accepted Obi-Wan Kenobi as his Padawan learner. Was given orders to protect Queen Amidala at all costs.");
        setGameText("Where present, [Tatooine] Anakin deploys -2 and, while with Qui-Gon, may not be targeted by weapons and adds one [Dark Side] icon and one [Light Side] icon here. Once per game, may stack top card of Lost Pile on your Podracer. Immune to Dark Strike, Maul Strikes, and attrition.");
        addPersona(Persona.QUIGON);
        addIcons(Icon.TATOOINE, Icon.EPISODE_I, Icon.WARRIOR, Icon.VIRTUAL_SET_27);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter tatooineAnakin = Filters.and(Icon.TATOOINE, Filters.Anakin);
        Condition quiGonPresent = new PresentCondition(self);
        Condition presentAtSiteWithAnakin = new AndCondition(quiGonPresent, new AtCondition(self, Filters.site), new WithCondition(self, tatooineAnakin));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, tatooineAnakin, quiGonPresent, -2, Filters.here(self)));
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, Filters.and(tatooineAnakin, Filters.here(self)), quiGonPresent));
        modifiers.add(new IconModifier(self, Filters.and(Filters.location, Filters.here(self)), presentAtSiteWithAnakin, Icon.DARK_FORCE, 1));
        modifiers.add(new IconModifier(self, Filters.and(Filters.location, Filters.here(self)), presentAtSiteWithAnakin, Icon.LIGHT_FORCE, 1));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Dark_Strike));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Maul_Strikes));
        modifiers.add(new ImmuneToAttritionModifier(self));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.QUI_GON_JINN_V__STACK_TOP_OF_LOST_PILE_ON_PODRACER;

        Filter yourPodracer = Filters.and(Filters.your(self), Filters.Podracer);

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
            && GameConditions.isDuringPodrace(game)
            && GameConditions.hasLostPile(game, playerId)
            && GameConditions.canTarget(game, self, yourPodracer)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Stack top card of Lost Pile on podracer");
            // Update usage limit(s)
            action.appendUsage(
                new OncePerGameEffect(action));
            // Choose target(s)
            action.appendTargeting(
                new TargetCardOnTableEffect(action, playerId, "Choose podracer", yourPodracer) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard podracer) {
                        action.addAnimationGroup(podracer);
                        // Allow response(s) — a targeting action must set a RespondableEffect
                        action.allowResponses("Stack top card of Lost Pile on " + GameUtils.getCardLink(podracer),
                            new RespondableEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    // Re-fetch the target in case it was changed during responses
                                    final PhysicalCard targetedPodracer = action.getPrimaryTargetCard(targetGroupId);
                                    final PhysicalCard topCardOfLostPile = game.getGameState().getTopOfLostPile(playerId);
                                    if (topCardOfLostPile != null) {
                                        // Stack face up (not inactive) on the podracer, then mark it as a race destiny so it
                                        // counts toward the podracing total like a normal drawn race destiny. Emit
                                        // RaceDestinyStackedResult and set the race-destiny marker to match DrawDestinyEffect's path.
                                        action.appendEffect(
                                            new StackOneCardFromLostPileEffect(action, topCardOfLostPile, targetedPodracer, false, false, false));
                                        action.appendEffect(
                                            new PassthruEffect(action) {
                                                @Override
                                                protected void doPlayEffect(SwccgGame game) {
                                                    game.getActionsEnvironment().emitEffectResult(
                                                        new RaceDestinyStackedResult(action, topCardOfLostPile, targetedPodracer));
                                                    topCardOfLostPile.setRaceDestinyForPlayer(playerId);
                                                }
                                            });
                                    }
                                }
                            });
                    }
                }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
