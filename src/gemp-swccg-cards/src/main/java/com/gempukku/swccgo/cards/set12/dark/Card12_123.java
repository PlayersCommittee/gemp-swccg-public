package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddToAttritionEffect;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Republic
 * Title: Toonbuck Toora
 */
public class Card12_123 extends AbstractRepublic {
    public Card12_123() {
        super(Side.DARK, 3, 2, 2, 3, 3, "Toonbuck Toora", Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.U);
        setPolitics(2);
        setLore("Female senator and member of her planet's ruling class. Her original optimism in the political process has been eroded by witnessing bribery, corruption and petty bickering.");
        setGameText("Agendas: ambition, taxation, wealth. While in a senate majority, once per turn may add 1 to your total attrition in battle for each character with an ambition agenda at Galactic Senate.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
        addKeywords(Keyword.FEMALE, Keyword.SENATOR);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.AMBITION, Agenda.TAXATION, Agenda.WEALTH));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isInitialAttritionJustCalculated(game, effectResult)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isInSenateMajority(game, self)
                && GameConditions.canModifyAttritionAgainst(game, opponent)) {
            int numToAdd = Filters.countActive(game, self,
                    Filters.and(Filters.character, Filters.ambition_agenda, Filters.at(Filters.Galactic_Senate)));
            if (numToAdd > 0) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Add " + numToAdd + " to attrition");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new AddToAttritionEffect(action, opponent, numToAdd));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
