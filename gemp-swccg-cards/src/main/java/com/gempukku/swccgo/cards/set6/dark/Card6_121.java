package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Character
 * Subtype: Alien
 * Title: Ree-Yees
 */
public class Card6_121 extends AbstractAlien {
    public Card6_121() {
        super(Side.DARK, 3, 3, 3, 3, 3, Title.ReeYees, Uniqueness.UNIQUE);
        setLore("Gran convicted of murder. Exiled from his homeworld. Smuggler and bounty hunter. Slowly going insane. Fond of making things explode. Plotting to kill Jabba.");
        setGameText("Thrice per battle at same site, if you just drew a battle destiny of 3, may use 3 Force to add 3 to that destiny.");
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR);
        addKeywords(Keyword.SMUGGLER, Keyword.BOUNTY_HUNTER);
        setSpecies(Species.GRAN);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, playerId)
                && GameConditions.isDuringBattleAt(game, Filters.sameSite(self))
                && GameConditions.isNumTimesPerBattle(game, self, playerId, 3, gameTextSourceCardId)
                && GameConditions.isDestinyValueEqualTo(game, 3)
                && GameConditions.canUseForce(game, playerId, 3)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Add " + 3 + " to battle destiny");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 3));
            // Perform result(s)
            action.appendEffect(
                    new ModifyDestinyEffect(action, 3));
            return Collections.singletonList(action);
        }
        return null;
    }
}
