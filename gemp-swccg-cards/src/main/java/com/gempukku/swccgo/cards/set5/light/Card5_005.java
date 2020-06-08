package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.cards.evaluators.ControlsEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifySabaccTotalEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Character
 * Subtype: Alien
 * Title: Lando Calrissian
 */
public class Card5_005 extends AbstractAlien {
    public Card5_005() {
        super(Side.LIGHT, 1, 3, 2, 3, 6, "Lando Calrissian", Uniqueness.UNIQUE);
        setLore("Gambler. Scoundrel. Former owner of the Millennium Falcon. Despite a notorious past, has become a successful chief administrator of a Tibanna gas mining facility.");
        setGameText("Adds 2 to power of anything he pilots (3 if piloting Falcon). Power +1 for every Cloud City location you control. When playing Sabacc, may add 1 to or subtract 1 from your total.");
        addIcons(Icon.CLOUD_CITY, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.GAMBLER);
        addPersona(Persona.LANDO);
        setMatchingStarshipFilter(Filters.Falcon);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(2, 3, Filters.Falcon)));
        modifiers.add(new PowerModifier(self, new ControlsEvaluator(self.getOwner(), Filters.Cloud_City_location)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        // Check condition(s)
        if (TriggerConditions.isCalculatingSabaccTotals(game, effectResult)
                && GameConditions.isPlayingSabacc(game, self)) {

            final OptionalGameTextTriggerAction action1 = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action1.setText("Add 1 to Sabacc total");
            // Perform result(s)
            action1.appendEffect(
                    new ModifySabaccTotalEffect(action1, playerId, 1));
            actions.add(action1);

            // Check condition(s)
            float sabaccTotal = game.getModifiersQuerying().getSabaccTotal(game.getGameState(), playerId);
            if (sabaccTotal > 0) {

                final OptionalGameTextTriggerAction action2 = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action2.setText("Subtract 1 from Sabacc total");
                // Perform result(s)
                action2.appendEffect(
                        new ModifySabaccTotalEffect(action2, playerId, -1));
                actions.add(action2);
            }
        }
        return actions;
    }
}
