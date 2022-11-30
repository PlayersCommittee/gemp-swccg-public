package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.InSenateMajorityCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromBottomOfForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.AgendaModifier;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Character
 * Subtype: Republic
 * Title: Tikkes
 */
public class Card12_122 extends AbstractRepublic {
    public Card12_122() {
        super(Side.DARK, 2, 3, 2, 3, 6, "Tikkes", Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.C);
        setPolitics(2);
        setLore("Quarren senator elected by the people of Mon Calamari. Has amassed considerable wealth through a multitude of political deals, but still moves to gain more.");
        setGameText("Agenda: wealth. While in a senate majority, your Force generation is +2 at battlegrounds you control, and Honor Of The Jedi is suspended. Once during your deploy phase, may draw bottom card of your Force Pile into hand.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
        addKeywords(Keyword.SENATOR);
        setSpecies(Species.QUARREN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AgendaModifier(self, Agenda.WEALTH));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition inSenateMajority = new InSenateMajorityCondition(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceGenerationModifier(self, Filters.and(Filters.battleground, Filters.controls(playerId)),
                inSenateMajority, 2, playerId));
        modifiers.add(new SuspendsCardModifier(self, Filters.Honor_Of_The_Jedi, inSenateMajority));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.DEPLOY)
                && GameConditions.hasForcePile(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Draw bottom card of Force Pile");
            action.setActionMsg("Draw bottom card of Force Pile into hand");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DrawCardIntoHandFromBottomOfForcePileEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}
