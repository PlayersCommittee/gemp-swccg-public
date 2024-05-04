package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 3
 * Type: Character
 * Subtype: Alien
 * Title: Jabba The Hutt (V)
 */
public class Card601_009 extends AbstractAlien {
    public Card601_009() {
        super(Side.DARK, 1, 4, 3, 4, 7, "Jabba The Hutt", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("Jabba Desilijic Tiure. Male heir to Zorba the Hutt. Gangster. Leader of one of the largest criminal organizations in the galaxy. Over six hundred years old.");
        setGameText("While with your alien leader, Jabba is immune to attrition any may not be targeted by Interrupts or weapons. May [download] one Scum And Villainy. During battle may use 1 Force to double the power of one other non-[Maintenance] alien present.");
        addIcons(Icon.JABBAS_PALACE, Icon.LEGACY_BLOCK_3);
        addPersona(Persona.JABBA);
        setSpecies(Species.HUTT);
        addKeywords(Keyword.GANGSTER, Keyword.LEADER);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition withYourAlienLeader = new WithCondition(self, Filters.and(Filters.your(self), Filters.alien_leader));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionModifier(self, withYourAlienLeader));
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, withYourAlienLeader));
        modifiers.add(new MayNotBeTargetedByModifier(self, withYourAlienLeader, Filters.Interrupt));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.JABBA_THE_HUTT__DOWNLOAD_SCUM_AND_VILLAINY;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Scum_And_Villainy)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Scum And Villainy from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Scum_And_Villainy, true));
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        Filter alienFilter = Filters.and(Filters.other(self), Filters.not(Icon.MAINTENANCE), Filters.alien, Filters.present(self));

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isInBattle(game, self)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTarget(game, self, alienFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Double power of an alien");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose alien", alienFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 1));
                            // Allow response(s)
                            action.allowResponses("Double power of " + GameUtils.getCardLink(targetedCard),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalAlien = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new AddUntilEndOfBattleModifierEffect(action,
                                                            new PowerMultiplierModifier(self, finalAlien, 2),
                                                            "Doubles power of " + GameUtils.getCardLink(finalAlien)));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}
