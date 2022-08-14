package com.gempukku.swccgo.cards.set217.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.cards.evaluators.NegativeEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.InBattleCondition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotMoveAwayFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Character
 * SubType: Alien
 * Title: Gar Saxon
 */
public class Card217_010 extends AbstractAlien {
    public Card217_010() {
        super(Side.DARK, 2, 3, 3, 3, 5, "Gar Saxon", Uniqueness.UNIQUE);
        setArmor(5);
        setLore("Mandalorian leader. Death Watch assassin.");
        setGameText("If deployed to a site where opponent has more than four characters, opponent loses 2 Force. Jedi here are defense value -1 for each of your Mandalorians here. While armed (or with a Mandalorian), opponent may not move away from (or cancel) battles here.");
        addKeywords(Keyword.LEADER, Keyword.ASSASSIN);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.VIRTUAL_SET_17);
        setSpecies(Species.MANDALORIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Condition armedOrWithMandalorianInBattle = new AndCondition(new OrCondition(new ArmedWithCondition(self, Filters.weapon), new WithCondition(self, Filters.Mandalorian)), new InBattleCondition(self));

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotMoveAwayFromLocationModifier(self, Filters.and(Filters.opponents(self), Filters.here(self)), armedOrWithMandalorianInBattle, Filters.here(self)));
        modifiers.add(new MayNotCancelBattleModifier(self, armedOrWithMandalorianInBattle, game.getOpponent(self.getOwner())));
        modifiers.add(new DefenseValueModifier(self, Filters.and(Filters.Jedi, Filters.here(self)), new NegativeEvaluator(new HereEvaluator(self, Filters.and(Filters.your(self), Filters.Mandalorian)))));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        final String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.justDeployedToLocation(game, effectResult, self, Filters.site)
                && GameConditions.canSpot(game, self, 5, Filters.and(Filters.opponents(self), Filters.character, Filters.here(self)))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Opponent loses 2 Force");
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 2));

            actions.add(action);
        }

        return actions;
    }
}
