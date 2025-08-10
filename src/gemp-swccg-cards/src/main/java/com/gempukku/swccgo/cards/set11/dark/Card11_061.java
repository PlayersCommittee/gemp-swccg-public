package com.gempukku.swccgo.cards.set11.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseDestinyCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Tatooine
 * Type: Character
 * Subtype: Alien
 * Title: Rachalt Hyst
 */
public class Card11_061 extends AbstractAlien {
    public Card11_061() {
        super(Side.DARK, 3, 2, 2, 2, 3, "Rachalt Hyst", Uniqueness.UNIQUE, ExpansionSet.TATOOINE, Rarity.U);
        setLore("Strange female Snivvian who frequents the cantina daily. Betrayed her loved ones, who then left her on Tatooine to die. Everything she's ever cared about is now gone or dead.");
        setGameText("Adds 2 to power of anything she pilots. During a battle involving Rachalt, whenever opponent draws an Interrupt for destiny may use 2 Force to cause that Interrupt to be lost. While armed with a weapon at a site, Force drain +1 here.");
        addIcons(Icon.TATOOINE, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.FEMALE);
        setSpecies(Species.SNIVVIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ForceDrainModifier(self, Filters.sameSite(self), new ArmedWithCondition(self, Filters.weapon),
                1, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, opponent)
                && GameConditions.isInBattle(game, self)
                && GameConditions.canMakeDestinyCardLost(game)
                && GameConditions.isDestinyCardMatchTo(game, Filters.Interrupt)
                && GameConditions.canUseForce(game, playerId, 2)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make destiny card lost");
            action.setActionMsg("Make just drawn destiny card, " + GameUtils.getCardLink(((DestinyDrawnResult) effectResult).getCard()) + ", lost");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            // Perform result(s)
            action.appendEffect(
                    new LoseDestinyCardEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
