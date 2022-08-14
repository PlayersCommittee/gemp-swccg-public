package com.gempukku.swccgo.cards.set218.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 18
 * Type: Character
 * Subtype: Alien
 * Title: Sagwa
 */
public class Card218_029 extends AbstractAlien {
    public Card218_029() {
        super(Side.LIGHT, 3, 3, 4, 2, 4, "Sagwa", Uniqueness.UNIQUE);
        setLore("Wookiee slave.");
        setGameText("If drawn for destiny, each of your Wookiees is forfeit +1 for remainder of turn. While Wookiee Homestead here, opponent's spies may not deploy here and, once per turn, when your Wookiee wins a battle, may retrieve a Wookiee.");
        setSpecies(Species.WOOKIEE);
        addKeyword(Keyword.SLAVE);
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_18);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredDrawnAsDestinyTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.isDestinyCardMatchTo(game, self)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Your Wookiees are forfeit +1");
            action.setActionMsg("Your Wookiees are forfeit +1 for remainder of turn");
            action.appendEffect(new AddUntilEndOfTurnModifierEffect(action,
                    new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.Wookiee), 1)
                    , "Makes each of your Wookiees forfeit +1 for remainder of turn"));

            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotDeployToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.spy), new HereCondition(self, Filters.Wookiee_Homestead), Filters.here(self)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isHere(game, self, Filters.Wookiee_Homestead)
                && TriggerConditions.wonBattle(game, effectResult, playerId)
                && TriggerConditions.wonBattle(game, effectResult, Filters.Wookiee)
                && GameConditions.isOncePerTurn(game, self, gameTextSourceCardId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve a Wookiee");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RetrieveCardEffect(action, playerId, Filters.Wookiee));
            return Collections.singletonList(action);
        }
        return null;
    }
}
