package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.effects.CancelCardResultEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetPowerModifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Taym Dren-garen
 */
public class Card6_124 extends AbstractAlien {
    public Card6_124() {
        super(Side.DARK, 3, 1, 2, 1, 2, "Taym Dren-garen", Uniqueness.UNIQUE);
        setLore("Instigator of several Sand People raids on Tatooine. Keeps the Tusken Raiders well supplied so they can keep local authorities distracted from Jabba's activities.");
        setGameText("When on Tatooine, may cancel any result of Krayt Dragon Bones. While at Audience Chamber, all your Tusken Raiders are power = 3 and forfeit +2.");
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPerformingGameTextAction(game, effect, Filters.Krayt_Dragon_Bones)
                && GameConditions.isOnSystem(game, self, Title.Tatooine)) {

            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel result of Krayt Dragon Bones");
            // Perform result(s)
            action.appendEffect(
                    new CancelCardResultEffect(action, effect));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atAudienceChamber = new AtCondition(self, Filters.Audience_Chamber);
        Filter yourTuskenRaiders = Filters.and(Filters.your(self), Filters.Tusken_Raider);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ResetPowerModifier(self, yourTuskenRaiders, atAudienceChamber, 3));
        modifiers.add(new ForfeitModifier(self, yourTuskenRaiders, atAudienceChamber, 2));
        return modifiers;
    }
}
