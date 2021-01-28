package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToAttritionEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.ResetForfeitEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 6
 * Type: Character
 * Subtype: Alien
 * Title: Ponda Baba (V)
 */
public class Card601_022 extends AbstractAlien {
    public Card601_022() {
        super(Side.DARK, 3, 2, 2, 2, 3, Title.Ponda_Baba, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("A male Quara (or fingered Aqualish). Thug, smuggler and partner of Dr. Evazan. Has a poor quality cybernetic arm replacement.");
        setGameText("[Pilot] 2. Game text of non-Jedi Luke (or a lightsaber he is using) may not target aliens here. During battle, if with a smuggler, may add a destiny to attrition (+2 if with your smuggler) or make that smuggler forfeit = 0.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.BLOCK_6);
        addKeywords(Keyword.SMUGGLER);
        setSpecies(Species.AQUALISH);
        setAsLegacy(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter nonJediLuke = Filters.and(Filters.Luke, Filters.not(Filters.Jedi));
        Filter filter = Filters.or(nonJediLuke, Filters.and(Filters.lightsaber, Filters.or(Filters.attachedTo(nonJediLuke), Filters.permanentWeaponOf(nonJediLuke))));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.and(Filters.alien, Filters.here(self)), filter));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        String opponent = game.getOpponent(playerId);
        Filter smugglerFilter = Filters.and(Filters.smuggler, Filters.with(self));

        // Check condition(s)
        if (GameConditions.isInBattle(game, self)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canTarget(game, self, smugglerFilter)) {
            if (GameConditions.canAddDestinyDrawsToAttrition(game, playerId)) {

                final TopLevelGameTextAction action1 = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action1.setText("Add one destiny to attrition");
                action1.setActionMsg("Add one destiny to attrition");
                // Update usage limit(s)
                action1.appendUsage(
                        new OncePerBattleEffect(action1));
                // Perform result(s)
                action1.appendEffect(
                        new AddDestinyToAttritionEffect(action1, 1));
                if (GameConditions.isWith(game, self, Filters.and(Filters.your(self), Filters.smuggler))) {
                    action1.appendEffect(
                            new AddUntilEndOfBattleModifierEffect(action1,
                                    new AttritionModifier(self, 2, opponent), ""));
                }
                actions.add(action1);
            }

            final TopLevelGameTextAction action2 = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action2.setText("Reset smuggler's forfeit to 0");
            // Update usage limit(s)
            action2.appendUsage(
                    new OncePerBattleEffect(action2));
            // Choose target(s)
            action2.appendTargeting(
                    new TargetCardOnTableEffect(action2, playerId, "Choose smuggler", smugglerFilter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action2.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action2.allowResponses("Reset " + GameUtils.getCardLink(targetedCard) + "'s forfeit to 0",
                                    new UnrespondableEffect(action2) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action2.appendEffect(
                                                    new ResetForfeitEffect(action2, targetedCard, 0));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action2);
        }
        return actions;
    }
}
