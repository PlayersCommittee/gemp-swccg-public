package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseDestinyCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.InitiateBattlesForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Salacious Crumb
 */
public class Card6_122 extends AbstractAlien {
    public Card6_122() {
        super(Side.DARK, 3, 1, 0, 1, 3, "Salacious Crumb", Uniqueness.UNIQUE);
        setLore("Male Kowakian. Prankster. Humiliates others for Jabba's amusement. His life depends on making Jabba laugh at least once per day.");
        setGameText("Opponent may initiate battle at same site for free. At same or adjacent site, whenever an opponent draws a card for battle destiny, if it is: Even, opponent must use 1 Force (if possible); Odd, destiny card is lost. (AH-hahahahaha!)");
        addIcons(Icon.JABBAS_PALACE);
        setSpecies(Species.KOWAKIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new InitiateBattlesForFreeModifier(self, Filters.sameSite(self), game.getOpponent(self.getOwner())));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String opponent = game.getOpponent(self.getOwner());

        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, opponent)
                && GameConditions.isDuringBattleAt(game, Filters.sameOrAdjacentSite(self))) {
            if (GameConditions.isDestinyValueEven(game)) {
                if (GameConditions.canUseForce(game, opponent, 1)) {

                    final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Make opponent use 1 Force");
                    // Perform result(s)
                    action.appendEffect(
                            new UseForceEffect(action, opponent, 1));
                    return Collections.singletonList(action);
                }
            }
            else if (GameConditions.isDestinyValueOdd(game)) {
                if (GameConditions.canMakeDestinyCardLost(game)) {

                    final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                    action.setText("Make destiny card lost");
                    // Perform result(s)
                    action.appendEffect(
                            new LoseDestinyCardEffect(action));
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}
