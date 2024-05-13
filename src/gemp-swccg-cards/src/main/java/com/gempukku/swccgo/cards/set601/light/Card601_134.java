package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardAboardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToFireWeaponModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 1
 * Type: Interrupt
 * Subtype: Used
 * Title: Antilles Maneuver (V)
 */
public class Card601_134 extends AbstractUsedInterrupt {
    public Card601_134() {
        super(Side.LIGHT, 5, "Antilles Maneuver", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setVirtualSuffix(true);
        setLore("As their war with the Empire continued, Rebel pilots learned ways to counter standard Imperial tactics such as the Tallon Roll.");
        setGameText("For remainder of turn, opponent must first use 1 Force to fire a weapon and opponent's starship weapon destiny draws are -1. OR During opponent's deploy phase, [download] a Rebel of ability < 3 (except an admiral) aboard your starship.");
        addIcons(Icon.SPECIAL_EDITION, Icon.LEGACY_BLOCK_1);
        setAsLegacy(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        final PlayInterruptAction action1 = new PlayInterruptAction(game, self);
        action1.setText("Affect opponent's weapons");
        // Allow response(s)
        action1.allowResponses("Make opponent use 1 Force to fire a weapon and make opponent's starship weapon destiny draws -1 for remainder of turn",
                new RespondablePlayCardEffect(action1) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        // Perform result(s)
                        action1.appendEffect(
                                new AddUntilEndOfTurnModifierEffect(action1,
                                        new ExtraForceCostToFireWeaponModifier(self, Filters.opponents(self), 1),
                                        "Makes opponent first use 1 Force to fire a weapon"));
                        action1.appendEffect(
                                new AddUntilEndOfTurnModifierEffect(action1,
                                        new EachWeaponDestinyModifier(self, Filters.and(Filters.opponents(self), Filters.starship_weapon), -1),
                                        "Makes opponent's starship weapon destiny draws -1"));
                    }
                }
        );
        actions.add(action1);

        GameTextActionId gameTextActionId = GameTextActionId.ANTILLES_MANEUVER__DOWNLOAD_REBEL_ABOARD_YOUR_STARSHIP;

        final Filter yourStarship = Filters.and(Filters.your(self), Filters.starship);

        // Check condition(s)
        if (GameConditions.isDuringOpponentsPhase(game, self, Phase.DEPLOY)
                && GameConditions.canSpot(game, self, Filters.or(yourStarship, Filters.siteOfStarshipOrVehicle(yourStarship)))
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, true, false)) {

            final PlayInterruptAction action2 = new PlayInterruptAction(game, self, gameTextActionId);
            action2.setText("Deploy Rebel from Reserve Deck");
            // Allow response(s)
            action2.allowResponses("Deploy a Rebel of ability < 3 (except an admiral) aboard your starship from Reserve Deck",
                    new RespondablePlayCardEffect(action2) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action2.appendEffect(
                                    new DeployCardAboardFromReserveDeckEffect(action2, Filters.and(Filters.Rebel,
                                            Filters.abilityLessThan(3), Filters.except(Filters.admiral)), yourStarship, true));
                        }
                    }
            );
            actions.add(action2);
        }
        return actions;
    }
}