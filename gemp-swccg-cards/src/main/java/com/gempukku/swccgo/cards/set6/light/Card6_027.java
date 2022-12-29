package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
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
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Loje Nella
 */
public class Card6_027 extends AbstractAlien {
    public Card6_027() {
        super(Side.LIGHT, 3, 2, 1, 2, 3, "Loje Nella", Uniqueness.UNIQUE, ExpansionSet.JABBAS_PALACE, Rarity.R);
        setLore("Accountant. Descended from a species of cliff boring worms. Reluctant assistant to Mosep. Purposefully mismanages funds to obstruct Jabba's nefarious activities.");
        setGameText("While at Audience Chamber, suspends Gailid's and Mosep's game text and allows you to activate 1 Force whenever you Force drain with an alien.");
        addIcons(Icon.JABBAS_PALACE);
        addKeywords(Keyword.ACCOUNTANT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelsGameTextModifier(self, Filters.or(Filters.Gailid, Filters.Mosep), new AtCondition(self, Filters.Audience_Chamber)));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, playerId, Filters.sameLocationAs(self, Filters.and(Filters.your(playerId), Filters.alien)))
                && GameConditions.isAtLocation(game, self, Filters.Audience_Chamber)
                && GameConditions.canActivateForce(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Activate 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new ActivateForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
