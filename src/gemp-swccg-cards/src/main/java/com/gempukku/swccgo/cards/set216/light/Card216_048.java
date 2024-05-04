package com.gempukku.swccgo.cards.set216.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RestoreCardToNormalEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Character
 * Subtype: Alien
 * Title: Wookiee Warrior
 */
public class Card216_048 extends AbstractAlien {
    public Card216_048() {
        super(Side.LIGHT, 2, 4, 2, 2, 4, "Wookiee Warrior", Uniqueness.RESTRICTED_3, ExpansionSet.SET_16, Rarity.V);
        setLore("");
        setGameText("During battle at a site, may add one destiny to total power. Once during battle, unless 'hit,' may lose this character to restore your 'hit' character here to normal.");
        setSpecies(Species.WOOKIEE);
        addIcons(Icon.WARRIOR, Icon.EPISODE_I, Icon.VIRTUAL_SET_16);
    }

    @Override
    public final boolean hasSpecialDefenseValueAttribute() {
        return true;
    }

    @Override
    public final float getSpecialDefenseValue() {
        return 4;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        //During battle, may add one destiny to total power.
        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isInBattle(game, self)
                && GameConditions.isDuringBattleAt(game, Filters.site)
                && GameConditions.canAddDestinyDrawsToPower(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Add one destiny to total power");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new AddDestinyToTotalPowerEffect(action, 1));

            actions.add(action);
        }


        //During battle, unless 'hit,' may lose this character to restore your 'hit' character here to normal.
        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        Filter hitCharacterFilter = Filters.and(Filters.your(self), Filters.here(self), Filters.hit_character);

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isInBattle(game, self)
                && !GameConditions.isHit(game, self)
                && GameConditions.canSpot(game, self, hitCharacterFilter)
        ) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Restore your 'hit' character to normal");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose 'hit' character", hitCharacterFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);
                            // Pay cost(s)
                            action.appendCost(
                                    new LoseCardFromTableEffect(action, self));

                            // Allow response(s)
                            action.allowResponses("Restore " + GameUtils.getCardLink(cardTargeted) + " to normal",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new RestoreCardToNormalEffect(action, finalTarget));
                                        }
                                    });
                        }
                    }
            );

            actions.add(action);
        }
        return actions;
    }
}
