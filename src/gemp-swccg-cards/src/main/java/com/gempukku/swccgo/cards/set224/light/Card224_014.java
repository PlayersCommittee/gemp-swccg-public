package com.gempukku.swccgo.cards.set224.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Set 24
 * Type: Character
 * Subtype: Republic
 * Title: Corporal Rushing (V)
 */
public class Card224_014 extends AbstractRepublic {
    public Card224_014() {
        super(Side.LIGHT, 3, 2, 3, 2, 5, "Corporal Rushing", Uniqueness.UNIQUE, ExpansionSet.SET_24, Rarity.V);
        setLore("Royal Naboo security officer in charge of protecting Amidala's Throne Room. His wife and children were captured when the Trade Federation invaded the planet.");
        setGameText("Once per turn, if armed with a blaster, may cancel the game text of a character of ability < 3 here for remainder of turn. Once per turn, if armed and with Panaka, may add 1 to a just drawn battle destiny or non-lightsaber weapon destiny.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.WARRIOR, Icon.VIRTUAL_SET_24);
        addKeywords(Keyword.ROYAL_NABOO_SECURITY);
        setVirtualSuffix(true);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final Filter targetFilter = Filters.and(Filters.character, Filters.here(self), Filters.abilityLessThan(3));

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isArmedWith(game, self, Filters.blaster)
                && GameConditions.canTarget(game, self, targetFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Cancel a character's game text");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", targetFilter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard) + "'s game text",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CancelGameTextUntilEndOfTurnEffect(action, targetedCard));
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
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if ((TriggerConditions.isBattleDestinyJustDrawn(game, effectResult)
                    || TriggerConditions.isWeaponDestinyJustDrawn(game, effectResult, Filters.not(Filters.lightsaber)))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isArmedWith(game, self, Filters.weapon)
                && GameConditions.isWith(game, self, Filters.Panaka)){
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Add 1 to destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new ModifyDestinyEffect(action, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
