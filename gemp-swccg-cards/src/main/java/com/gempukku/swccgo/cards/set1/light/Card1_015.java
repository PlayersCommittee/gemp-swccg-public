package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ResetTotalBattleDestinyFromDrawsEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Alien
 * Title: Kal'Falnl C'ndros
 */
public class Card1_015 extends AbstractAlien {
    public Card1_015() {
        super(Side.LIGHT, 2, 0, 1, 1, 5, Title.KalFalnl_Cndros, Uniqueness.UNIQUE);
        setLore("A female Quor'sav, a warm-blooded, avian/monotreme species. 3.5 meters tall. Over-protective mother. Freelance pilot. Has custom-built ship with tall corridors. Lays eggs.");
        setGameText("When in a battle, if both players draw only one battle destiny and yours is higher, reduces opponent's destiny to zero. Landspeed = 3. Adds 2 to power of anything she pilots. May not deploy to or board starfighters or enclosed vehicles.");
        addIcons(Icon.PILOT);
        addKeywords(Keyword.FEMALE);
        setSpecies(Species.QUORSAV);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployToTargetModifier(self, Filters.or(Filters.starfighter, Filters.enclosed_vehicle)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextLandspeedModifier(self, 3));
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new MayNotExistAtTargetModifier(self, Filters.or(Filters.starfighter, Filters.enclosed_vehicle)));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isBattleDestinyDrawingJustCompletedForBothPlayers(game, effectResult)
                && GameConditions.isInBattle(game, self)
                && GameConditions.didBothPlayersDrawOneBattleDestiny(game)
                && GameConditions.hasGreaterBattleDestinyTotal(game, playerId, true)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Reduce opponent's destiny");
            action.setActionMsg("Reset opponent's total battle destiny to 0");
            // Perform result(s)
            action.appendEffect(
                    new ResetTotalBattleDestinyFromDrawsEffect(action, opponent, 0));
            return Collections.singletonList(action);
        }
        return null;
    }
}
