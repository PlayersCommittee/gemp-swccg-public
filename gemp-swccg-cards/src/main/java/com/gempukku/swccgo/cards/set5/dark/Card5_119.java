package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ExcludeFromBattleEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Effect
 * Title: I Had No Choice
 */
public class Card5_119 extends AbstractNormalEffect {
    public Card5_119() {
        super(Side.DARK, 3, PlayCardZoneOption.ATTACHED, "I Had No Choice", Uniqueness.UNIQUE);
        setLore("Gamblers are vulnerable to bribery, extortion and other forms of manipulation. Their notorious dealings can be easily taken advantage of.");
        setGameText("Deploy on an opponent's gambler. Cancels gambler's game text. If a battle was just initiated, you may use X Force to exclude gambler from that battle, where X = gambler's ability.");
        addKeywords(Keyword.DEPLOYS_ON_CHARACTERS);
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.opponents(self), Filters.gambler);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelsGameTextModifier(self, Filters.hasAttached(self)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.hasAttached(self))) {
            PhysicalCard gambler = self.getAttachedTo();
            float ability = game.getModifiersQuerying().getAbility(game.getGameState(), gambler);
            if (GameConditions.canUseForce(game, playerId, ability)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Exclude " + GameUtils.getFullName(gambler) + " from battle");
                action.setActionMsg("Exclude " + GameUtils.getCardLink(gambler) + " from battle");
                // Pay cost(s)
                action.appendCost(
                        new UseForceEffect(action, playerId, ability));
                // Perform result(s)
                action.appendEffect(
                        new ExcludeFromBattleEffect(action, gambler));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}