package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.evaluators.AddEvaluator;
import com.gempukku.swccgo.cards.evaluators.StackedEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromHandOrForcePileEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextPowerModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Character
 * Subtype: Alien
 * Title: Hem Dazon
 */
public class Card2_090 extends AbstractAlien {
    public Card2_090() {
        super(Side.DARK, 4, 2, null, 2, 3, "Hem Dazon", Uniqueness.UNIQUE, ExpansionSet.A_NEW_HOPE, Rarity.R1);
        setLore("Male scout from Cona. As with many Arconas, Hem has succumbed to the power of salt, and addiction indicated by his gold eye color.");
        setGameText("Once during each of your deploy phases, may place, face down, one card from hand or Force Pile under Hem, as 'salt consumption.' If Hem lost, cards underneath also lost. *Power = 1 + number of cards underneath.");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.SCOUT);
        setSpecies(Species.ARCONA);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.DEPLOY)
                && (GameConditions.hasHand(game, playerId) || GameConditions.hasForcePile(game, playerId))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Stack a card as 'salt consumption'");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new StackCardFromHandOrForcePileEffect(action, playerId, self, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextPowerModifier(self, new AddEvaluator(new StackedEvaluator(self), 1)));
        return modifiers;
    }
}
