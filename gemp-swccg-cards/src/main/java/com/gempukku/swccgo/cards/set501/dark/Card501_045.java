package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 0
 * Type: Effect
 * Title: Ability, Ability, Ability (V) (Errata)
 */
public class Card501_045 extends AbstractNormalEffect {
    public Card501_045() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Ability, Ability, Ability", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("Enhanced steering mechanisms on Rebel T-47s provide increased maneuverability in planetary atmospheres.");
        setGameText("Deploy on table. If you just initiated battle (or opponent just initiated lightsaber combat), retrieve the top-most non-[PW], non-[M] character of Lost Pile. Unless a Dark Jedi at a battleground, once per turn, your character or [IND] starship may move for free. [Immune to Alter.]");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_0);
        addImmuneToCardTitle(Title.Alter);
        setTestingText("Ability, Ability, Ability (V) (Errata)");
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.ABILITY_ABILITY_ABILITY__RETRIEVE_TOPMOST_CHARACTER;

        // Check condition(s)
        if ((TriggerConditions.battleInitiated(game, effectResult, playerId)
                || TriggerConditions.lightsaberCombatInitiated(game, effectResult, game.getOpponent(playerId)))
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve topmost character");
            action.setActionMsg("Retrieve topmost non-[Permanent Weapon], non-[Maintenance] character");
            // Perform result(s)
            action.appendEffect(
                    new RetrieveCardEffect(action, playerId, true, Filters.and(Filters.character, Filters.not(Filters.or(Icon.PERMANENT_WEAPON, Icon.MAINTENANCE)))) {
                        @Override
                        public boolean isDueToInitiatingBattle() {
                            return true;
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        Filter filter = Filters.or(Filters.character, Icon.INDEPENDENT);

        // Check condition(s)
        if (!GameConditions.canSpot(game, self, Filters.and(Filters.at(Filters.battleground), Filters.Dark_Jedi))
                && GameConditions.canSpot(game, self, filter)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Target character or [IND] starship may move for free this turn");
            // Choose target(s)
            action.appendUsage(
                    new OncePerTurnEffect(action)
            );
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Target character or [IND] starship", filter) {
                        @Override
                        protected void cardSelected(PhysicalCard targetedCard) {
                            action.appendEffect(
                                    new AddUntilEndOfTurnModifierEffect(action, new MovesForFreeModifier(targetedCard), "")
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}