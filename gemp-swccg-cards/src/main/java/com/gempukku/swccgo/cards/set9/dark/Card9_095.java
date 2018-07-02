package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractAdmiralsOrder;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ModifyDefenseValueUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Admiral's Order
 * Title: Intensify The Forward Batteries
 */
public class Card9_095 extends AbstractAdmiralsOrder {
    public Card9_095() {
        super(Side.DARK, "Intensify The Forward Batteries");
        setGameText("Each player, if that player has a capital starship armed with a starship weapon in battle, draws one battle destiny if not able to otherwise and once per battle, may reduce the defense value of one opponent's capital starship present by 4 for remainder of the battle. At sites related to systems you occupy, your non-pilot warriors are deploy - 1, defense value +2 and forfeit +1.");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter armedCapitalStarship = Filters.and(Filters.capital_starship, Filters.armedWith(Filters.starship_weapon));
        Filter sitesRelatedToSystemsYouOccupy = Filters.and(Filters.relatedSiteTo(self, Filters.and(Filters.system, Filters.occupies(playerId))));
        Filter yourNonPilotWarriors = Filters.and(Filters.your(playerId), Filters.warrior, Filters.not(Filters.pilot));
        Filter filter = Filters.and(yourNonPilotWarriors, Filters.at(sitesRelatedToSystemsYouOccupy));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, armedCapitalStarship, 1));
        modifiers.add(new DeployCostToLocationModifier(self, yourNonPilotWarriors, -1, sitesRelatedToSystemsYouOccupy));
        modifiers.add(new DefenseValueModifier(self, filter, 2));
        modifiers.add(new ForfeitModifier(self, filter, 1));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter filter = Filters.and(Filters.opponents(playerId), Filters.capital_starship, Filters.presentInBattle,
                Filters.inBattleWith(Filters.and(Filters.your(playerId), Filters.capital_starship, Filters.armedWith(Filters.starship_weapon))));

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canTarget(game, self, filter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Reduce defense value of capital starship");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose capital starship", filter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Reduce " + GameUtils.getCardLink(targetedCard) + "'s defense value by 4",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ModifyDefenseValueUntilEndOfBattleEffect(action, targetedCard, -4));
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
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter filter = Filters.and(Filters.opponents(playerId), Filters.capital_starship, Filters.presentInBattle,
                Filters.inBattleWith(Filters.and(Filters.your(playerId), Filters.capital_starship, Filters.armedWith(Filters.starship_weapon))));

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canTarget(game, self, filter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Reduce defense value of capital starship");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose capital starship", filter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Reduce " + GameUtils.getCardLink(targetedCard) + "'s defense value by 4",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ModifyDefenseValueUntilEndOfBattleEffect(action, targetedCard, -4));
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
}
