package com.gempukku.swccgo.cards.set223.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromOffTableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.NoForceLossFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 23
 * Type: Defensive Shield
 * Title: You've Never Won A Race? (V)
 */
public class Card223_029 extends AbstractDefensiveShield {
    public Card223_029() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "You've Never Won A Race?", ExpansionSet.SET_23, Rarity.V);
        setLore("'Not even finished?'");
        setGameText("Plays on table. You lose no Force to Boonta Eve Podrace. Unless opponent occupies three battlegrounds, I Did It! is suspended. Double Agent may not target opponent's Undercover spies. If an Undercover spy was just lost, it is placed out of play.");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I, Icon.VIRTUAL_DEFENSIVE_SHIELD);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SuspendsCardModifier(self, Filters.I_Did_It, new UnlessCondition(new OccupiesCondition(opponent, 3, Filters.battleground))));
        modifiers.add(new NoForceLossFromCardModifier(self, Filters.Boonta_Eve_Podrace, playerId));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.opponents(self.getOwner()), Filters.undercover_spy), Title.Double_Agent));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<>();

        if (TriggerConditions.justLost(game, effectResult, Filters.undercover_spy)) {
            final PhysicalCard undercoverSpy = ((LostFromTableResult) effectResult).getCard();

            if (undercoverSpy != null) {
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Place " + GameUtils.getCardLink(undercoverSpy) + " out of play");

                // Perform result(s)
                action.appendEffect(
                        new PlaceCardOutOfPlayFromOffTableEffect(action, undercoverSpy));

                actions.add(action);
            }
        }
        return actions;
    }
}
