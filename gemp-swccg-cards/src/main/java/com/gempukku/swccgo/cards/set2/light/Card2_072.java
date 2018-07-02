package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Starship
 * Subtype: Starfighter
 * Title: Red 6
 */
public class Card2_072 extends AbstractStarfighter {
    public Card2_072() {
        super(Side.LIGHT, 6, 2, 3, null, 4, 5, 5, Title.Red_6, Uniqueness.UNIQUE);
        setLore("Jek Porkins' X-wing at Battle of Yavin. Instrumental in success of strafing attacks against Death Star. Skipped last inspection of computer and flight control systems.");
        setGameText("May add 1 pilot. Opponent may add 2 to destiny drawn for I've Got A Problem Here when targeting Red 6.");
        addIcons(Icon.A_NEW_HOPE, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.X_WING);
        setPilotCapacity(1);
        addKeywords(Keyword.RED_SQUADRON);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getOpponentsCardGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnTargeting(game, effectResult, Filters.Ive_Got_A_Problem_Here, self)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId);
            action.setText("Add 2 to destiny");
            // Perform result(s)
            action.appendEffect(
                    new ModifyDestinyEffect(action, 2));
            return Collections.singletonList(action);
        }
        return null;
    }
}
