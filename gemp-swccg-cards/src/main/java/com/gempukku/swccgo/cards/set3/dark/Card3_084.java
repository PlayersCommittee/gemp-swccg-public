package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseTractorBeamEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Hoth
 * Type: Character
 * Subtype: Imperial
 * Title: Captain Lennox
 */
public class Card3_084 extends AbstractImperial {
    public Card3_084() {
        super(Side.DARK, 2, 3, 2, 2, 5, "Captain Lennox", Uniqueness.UNIQUE);
        setLore("Captain of the Imperial Star Destroyer Tyrant. An able leader. Unlike most Imperial officers, he is dedicated to his ship and crew. Finds political maneuvering distasteful.");
        setGameText("Adds 2 to power of anything he pilots (3 if starship is Tyrant). When on a Star Destroyer, may use its tractor beam once during each of your control phases.");
        addKeywords(Keyword.CAPTAIN, Keyword.LEADER);
        addIcons(Icon.HOTH, Icon.PILOT, Icon.WARRIOR);
        setMatchingStarshipFilter(Filters.Tyrant);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, new CardMatchesEvaluator(2, 3, Filters.Tyrant)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        Filter tractorBeamFilter = Filters.and(Filters.tractor_beam, Filters.attachedTo(Filters.hasAboard(self)));

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.CONTROL)
                && GameConditions.isAboard(game, self, Filters.Star_Destroyer)
                && GameConditions.canSpot(game, self, tractorBeamFilter)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Use tractor beam");
            action.setActionMsg("Use tractor beam");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose tractor beam to use", tractorBeamFilter) {
                        @Override
                        protected void cardSelected(final PhysicalCard tractorBeam) {
                            action.addAnimationGroup(tractorBeam);
                            // Pay cost(s)
                            action.appendEffect(
                                    new UseTractorBeamEffect(action, tractorBeam, false));
                        }
                    }
            );

            return Collections.singletonList(action);
        }
        return null;
    }
}
