package com.gempukku.swccgo.cards.set305.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.BattleResultDeterminedResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: A Better Tomorrow
 * Type: Character
 * Subtype: Alien
 * Title: Syrena Valkar
 */
public class Card305_014 extends AbstractAlien {
    public Card305_014() {
        super(Side.LIGHT, 1, 4, 3, 5, 5, "Syrena Valkar", Uniqueness.UNIQUE, ExpansionSet.ABT, Rarity.U);
        setLore("Leader of the Knights of Allusis. One of the few Light-side Force users who can channel Force Lightning without becoming corrupted by the Dark side. Musician, playing the hallikset.");
        setGameText("Whenever a player wins a battle here, that player gains 1 Force. May be targeted by Uncontrolled Force Lightning.");
        addKeywords(Keyword.FEMALE, Keyword.LEADER, Keyword.MUSICIAN);
        addIcons(Icon.COU, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayBeTargetedByModifier(self, Title.Uncontrolled_Force_Lightning));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.battleResultDetermined(game, effectResult)
                && GameConditions.isDuringBattleAt(game, Filters.here(self))) {
            BattleResultDeterminedResult result = (BattleResultDeterminedResult) effectResult;
            String winner = result.getWinner();
            if (winner != null) {

                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Make " + winner + " lose 1 Force");
                // Perform result(s)
                action.appendEffect(
                        new RetrieveForceEffect(action, winner, 1));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
