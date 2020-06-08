package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.InBattleAtCondition;
import com.gempukku.swccgo.cards.evaluators.HandSizeEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PutCardsFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Character
 * Subtype: Alien
 * Title: Loci Rosen
 */
public class Card11_005 extends AbstractAlien {
    public Card11_005() {
        super(Side.LIGHT, 2, 4, 0, 1, 2, "Loci Rosen", Uniqueness.UNIQUE);
        setLore("Good-willed Mon Calamari merchant. Looking for work. Unfortunately, everywhere he goes he comes up empty handed.");
        setGameText("Power +X in a battle at a site, where X equals the number of cards in opponent's hand. During your deploy phase, opponent may place any number of cards in their hand in their Used Pile.");
        addIcons(Icon.TATOOINE);
        setSpecies(Species.MON_CALAMARI);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new InBattleAtCondition(self, Filters.site), new HandSizeEvaluator(game.getOpponent(self.getOwner()))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)
                && GameConditions.hasHand(game, playerId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Place cards from hand in Used Pile");
            // Perform result(s)
            action.appendEffect(
                    new PutCardsFromHandOnUsedPileEffect(action, playerId, 1, Integer.MAX_VALUE));
            return Collections.singletonList(action);
        }
        return null;
    }
}
