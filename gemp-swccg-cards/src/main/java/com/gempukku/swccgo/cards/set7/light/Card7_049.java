package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.StealCardIntoHandFromLostPileEffect;

import java.util.Collections;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Uutkik
 */
public class Card7_049 extends AbstractAlien {
    public Card7_049() {
        super(Side.LIGHT, 3, 2, 2, 2, 3, "Uutkik", Uniqueness.UNIQUE);
        setLore("Experienced Jawa thief. Pilfers equipment and hijacks vehicles from unwary bystanders in Mos Eisley. Het Nkik's ugliest cousin.");
        setGameText("Deploys only on Tatooine. Once during each of your control phases, may lose 1 Force to steal into hand one transport vehicle, character weapon or device from opponent's Lost Pile.");
        addIcons(Icon.SPECIAL_EDITION);
        addKeywords(Keyword.THIEF);
        setSpecies(Species.JAWA);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Tatooine;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.UUTKIK__STEAL_FROM_LOST_PILE;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.canStealCardsFromLostPile(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Steal from opponent's Lost Pile");
            action.setActionMsg("Steal a transport vehicle, character weapon, or device into hand from opponent's Lost Pile");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            // Perform result(s)
            action.appendEffect(
                    new StealCardIntoHandFromLostPileEffect(action, playerId, Filters.or(Filters.transport_vehicle, Filters.character_weapon, Filters.device)));
            return Collections.singletonList(action);
        }
        return null;
    }
}
