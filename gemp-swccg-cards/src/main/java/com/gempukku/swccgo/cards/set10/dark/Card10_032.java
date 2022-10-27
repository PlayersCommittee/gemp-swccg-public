package com.gempukku.swccgo.cards.set10.dark;

import com.gempukku.swccgo.cards.AbstractAdmiralsOrder;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.MoveCardAsRegularMoveEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.*;

/**
 * Set: Reflections II
 * Type: Admiral's Order
 * Title: Black Sun Fleet
 */
public class Card10_032 extends AbstractAdmiralsOrder {
    public Card10_032() {
        super(Side.DARK, "Black Sun Fleet", ExpansionSet.REFLECTIONS_II, Rarity.PM);
        setGameText("Any [Independent] starship with an alien pilot character aboard is immune to attrition < 4. At any site related to a system you occupy, your Black Sun agents are also information brokers. Information Exchange is immune to Alter. You may not retrieve Force for initiating a battle. During your control phase, one of your [Independent] starships may make a regular move.");
        addIcons(Icon.REFLECTIONS_II);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.starship, Icon.INDEPENDENT,
                Filters.hasAboard(self, Filters.and(Filters.alien, Filters.pilot_character))), 4));
        modifiers.add(new KeywordModifier(self, Filters.and(Filters.your(self), Filters.Black_Sun_agent,
                Filters.at(Filters.relatedSiteTo(self, Filters.and(Filters.system, Filters.occupies(playerId))))), Keyword.INFORMATION_BROKER));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Information_Exchange, Title.Alter));
        modifiers.add(new SpecialFlagModifier(self, ModifierFlag.MAY_NOT_RETRIEVE_FORCE_FOR_INITIATING_BATTLE, playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)) {
            Collection<PhysicalCard> starships = Filters.filterActive(game, self, Filters.and(Filters.your(self), Filters.starship, Icon.INDEPENDENT));
            if (!starships.isEmpty()) {
                List<PhysicalCard> validStarships = new ArrayList<PhysicalCard>();
                for (PhysicalCard starship : starships) {
                    if (Filters.movableAsRegularMove(playerId, false, 0, false, Filters.any).accepts(game, starship)) {
                        validStarships.add(starship);
                    }
                }
                if (!validStarships.isEmpty()) {

                    final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                    action.setText("Have a starship make a regular move");
                    // Update usage limit(s)
                    action.appendUsage(
                            new OncePerPhaseEffect(action));
                    // Choose target(s)
                    action.appendTargeting(
                            new ChooseCardOnTableEffect(action, playerId, "Choose [Independent] starship to move", Filters.in(validStarships)) {
                                @Override
                                protected void cardSelected(PhysicalCard starship) {
                                    action.addAnimationGroup(starship);
                                    action.setActionMsg("Have " + GameUtils.getCardLink(starship) + " make a regular move");
                                    // Perform result(s)
                                    action.appendEffect(
                                            new MoveCardAsRegularMoveEffect(action, playerId, starship, false, false, Filters.any));
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
