package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.RemainsInPlayWhenForfeitedFromPlayModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Droid
 * Title: WED15-l7 'Septoid' Droid
 */
public class Card2_109 extends AbstractDroid {
    public Card2_109() {
        super(Side.DARK, 2, 3, 1, null, "WED15-l7 'Septoid' Droid", Uniqueness.UNIQUE);
        setLore("Multi-armed maintenance droid fiercely loyal to the Empire. Specializes in extending effective operational life of Imperial resources. Nicknamed for an insect from Eriadu.");
        setGameText("* Forfeit value begins at 7. When 'forfeited,' droid remains in play, but forfeit value is reduced by the amount of attrition or battle damage absorbed. Droid lost when forfeit value reaches zero.");
        addModelType(ModelType.MAINTENANCE);
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextForfeitModifier(self, 7));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new RemainsInPlayWhenForfeitedFromPlayModifier(self));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            float forfeitValue = game.getModifiersQuerying().getForfeit(game.getGameState(), self);
            if (forfeitValue == 0) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Make lost");
                action.setActionMsg("Make " + GameUtils.getCardLink(self) + " lost");
                // Perform result(s)
                action.appendEffect(
                        new LoseCardFromTableEffect(action, self));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
