package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Type: Effect
 * Title: Quick Reflexes
 */
public class Card6_146 extends AbstractNormalEffect {
    public Card6_146() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Quick Reflexes", Uniqueness.UNRESTRICTED, ExpansionSet.JABBAS_PALACE, Rarity.C);
        setLore("Boba Fett's helmet has infrared capabilities, a motion tracking system, a macrobinocular viewer, an internal comlink and a broadband antenna. He doesn't miss a thing.");
        setGameText("Deploy on your side of table. During your draw phase, you may use 2 Force to search your Lost Pile. Take one Hidden Weapons into hand or take any one blaster and immediately deploy it (for free).");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.QUICK_REFLEXES__UPLOAD_HIDDEN_WEAPONS_OR_DOWNLOAD_BLASTER_FROM_LOST_PILE;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DRAW)
                && GameConditions.canUseForce(game, playerId, 2)
                && (GameConditions.canDeployCardFromLostPile(game, playerId, self, gameTextActionId, true)
                || GameConditions.canTakeCardsIntoHandFromLostPile(game, playerId, self, gameTextActionId))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Search Lost Pile");
            action.setActionMsg("Take Hidden Weapons into Hand (or deploy a blaster) from Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            // Perform result(s)
            action.appendEffect(
                    new ChooseCardFromLostPileEffect(action, playerId, Filters.or(Filters.Hidden_Weapons, Filters.and(Filters.blaster, Filters.deployable(self, null, true, 0)))) {
                        @Override
                        protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                            if (Filters.Hidden_Weapons.accepts(game, selectedCard)) {
                                action.appendEffect(
                                        new TakeCardIntoHandFromLostPileEffect(action, playerId, selectedCard, false, false));
                            }
                            else {
                                action.appendEffect(
                                        new DeployCardFromLostPileEffect(action, selectedCard, true, false));
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}