package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.ExcludeFromBattleEffect;
import com.gempukku.swccgo.logic.effects.TakeFirstBattleWeaponsSegmentActionEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Character
 * Subtype: Alien
 * Title: Tobias Beckett
 */
public class Card501_024 extends AbstractAlien {
    public Card501_024() {
        super(Side.LIGHT, 0, 3, 4, 3, 5, "Tobias Beckett", Uniqueness.UNIQUE);
        setLore("Smuggler, musician, thief, and information broker. Glee Anselmian.");
        setGameText("While present at a battleground and armed with a blaster, adds one [Dark Side] icon here and if a battle was just initiated here, you may take the first weapons segment action. Unless with Aurra, opponent may use 3 Force to 'bribe' (exclude) Beckett from battle.");
        addPersona(Persona.BECKETT);
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_13);
        addKeywords(Keyword.SMUGGLER, Keyword.MUSICIAN, Keyword.THIEF, Keyword.INFORMATION_BROKER);
        setSpecies(Species.GLEE_ANSELMIAN);
        setTestingText("Tobias Beckett");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        Condition armedWithABlasterCondition = new ArmedWithCondition(self, Filters.blaster);
        Condition presentAtABattlegroundCondition = new PresentAtCondition(self, Filters.battleground);
        modifiers.add(new IconModifier(self, Filters.and(Filters.battleground_site, Filters.here(self)), new AndCondition(presentAtABattlegroundCondition, armedWithABlasterCondition), Icon.DARK_FORCE));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        List<OptionalGameTextTriggerAction> actions = new LinkedList<>();
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.here(self))
                && GameConditions.isPresentAt(game, self, Filters.battleground)
                && GameConditions.isArmedWith(game, self, Filters.blaster)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Take first weapons phase action");
            action.setActionMsg("Take first weapons phase action");
            // Perform result(s)
            action.appendEffect(
                    new TakeFirstBattleWeaponsSegmentActionEffect(action, playerId));
           actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isInBattle(game, self)
                && !GameConditions.isInBattleWith(game, self, Filters.Aurra)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canUseForce(game, playerId, 3)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("'Bribe' from battle");
            action.setActionMsg("'Bribe' " + GameUtils.getCardLink(self) + " from battle");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerBattleEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 3));
            // Perform result(s)
            action.appendEffect(
                    new ExcludeFromBattleEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
