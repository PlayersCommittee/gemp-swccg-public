package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifySabaccTotalEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.MayBeReplacedByOpponentModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Character
 * Subtype: Alien
 * Title: Lando Calrissian
 */
public class Card5_099 extends AbstractAlien {
    public Card5_099() {
        super(Side.DARK, 1, 2, 3, 3, 3, "Lando Calrissian", Uniqueness.UNIQUE);
        setLore("Scoundrel and gambler. Petty administrator of a small Tibanna gas mining operation. Easily coerced. Has problems of his own. Had dealings with the Tonnika sisters - twice.");
        setGameText("Deploys only on Cloud City. Adds 2 to power of anything he pilots. When playing Sabacc, may subtract 1 from or add 1 to your total. If present at a site, can be replaced by opponent with any Light Side Lando.");
        addIcons(Icon.CLOUD_CITY, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.GAMBLER);
        addPersona(Persona.LANDO);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Cloud_City;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new MayBeReplacedByOpponentModifier(self, new PresentAtCondition(self, Filters.site)));
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
