package com.gempukku.swccgo.cards.set213.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Effect
 * Title: Meditation (V)
 */
public class Card213_047 extends AbstractNormalEffect {
    public Card213_047() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.Meditation, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("To prepare for the inevitable conflict with Jabba's minions, Luke entered a calm state of mental preparation.");
        setGameText("Deploy on your character of ability > 3. Opponent’s aliens are power -1 here (and forfeit -2 if a leader or [PW] or [M] card). If opponent has more characters at same site, may add one battle destiny (then lose Effect at end of battle).");
        addIcons(Icon.SPECIAL_EDITION, Icon.VIRTUAL_SET_13);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.character, Filters.abilityMoreThan(3));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.battleEnded(game, effectResult)
                && GameConditions.cardHasWhileInPlayDataSet(self)) {
            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            self.setWhileInPlayData(null);
            action.appendEffect(
                    new LoseCardFromTableEffect(action, self)
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && (Filters.countActive(game, self, Filters.and(Filters.opponents(playerId), Filters.character, Filters.at(Filters.sameSite(self))))
                > Filters.countActive(game, self, Filters.and(Filters.your(playerId), Filters.character, Filters.at(Filters.sameSite(self)))))
                && GameConditions.canAddBattleDestinyDraws(game, self)
                && !GameConditions.cardHasWhileInPlayDataSet(self)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Add one battle destiny");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new SetWhileInPlayDataEffect(action, self, new WhileInPlayData()));
            action.appendEffect(
                    new AddBattleDestinyEffect(action, 1));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter opponentsAliensHere = Filters.and(Filters.opponents(self.getOwner()), Filters.alien, Filters.here(self.getAttachedTo()));
        Filter leaderPWorMCard = Filters.and(opponentsAliensHere, Filters.or(Filters.leader, Filters.icon(Icon.PERMANENT_WEAPON), Filters.icon(Icon.MAINTENANCE)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, opponentsAliensHere, -1));
        modifiers.add(new ForfeitModifier(self, leaderPWorMCard, -2));
        return modifiers;
    }
}
