package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromUsedPileEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Interrupt
 * Subtype: Lost
 * Title: Jedi Levitation
 */
public class Card4_053 extends AbstractLostInterrupt {
    public Card4_053() {
        super(Side.LIGHT, 4, "Jedi Levitation", Uniqueness.UNRESTRICTED, ExpansionSet.DAGOBAH, Rarity.R);
        setLore("A Jedi can adjust the force within and around an object, causing it to move as the Jedi wills.");
        setGameText("Use X force, where X = (7- ability of your highest ability character on table). Search through your Used Pile and take one card into hand. Shuffle, Cut and replace.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        GameTextActionId gameTextActionId = GameTextActionId.JEDI_LEVITATION__UPLOAD_CARD_FROM_USED_PILE;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromUsedPile(game, playerId, self, gameTextActionId)) {
            PhysicalCard highestAbilityCharacter = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.highestAbilityCharacter(self, playerId)));
            if (highestAbilityCharacter != null) {
                float forceRequired = 7 - game.getModifiersQuerying().getAbility(game.getGameState(), highestAbilityCharacter);
                if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, forceRequired)) {

                    final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
                    action.setText("Take card into hand from Used Pile");
                    // Pay cost(s)
                    action.appendCost(
                            new UseForceEffect(action, playerId, forceRequired));
                    // Allow response(s)
                    action.allowResponses("Take a card into hand from Used Pile",
                            new RespondablePlayCardEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    // Perform result(s)
                                    action.appendEffect(
                                            new TakeCardIntoHandFromUsedPileEffect(action, playerId, true));
                                }
                            }
                    );
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}