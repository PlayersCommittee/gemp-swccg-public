package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.RetargetEffectEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeForfeitedInBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * Set: Dagobah
 * Type: Character
 * Subtype: Imperial
 * Title: Captain Needa
 */
public class Card4_093 extends AbstractImperial {
    public Card4_093() {
        super(Side.DARK, 1, 3, 3, 3, 5, "Captain Needa", Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.R);
        setLore("Able leader and captain of the Avenger. Was given his command by Admiral Ozzel. Treated with suspicion by Darth Vader and the Emperor, who distrust Ozzel's close advisors.");
        setGameText("Adds 2 to power of anything he pilots. When piloting Avenger, that starship is also immune to attrition < 4. You may re-target applicable Utinni Effects to Needa. While Needa in battle, your other non-'hit' characters here may not be forfeited.");
        addKeywords(Keyword.LEADER, Keyword.CAPTAIN);
        addIcons(Icon.DAGOBAH, Icon.PILOT, Icon.WARRIOR);
        setMatchingStarshipFilter(Filters.Avenger);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.Avenger, Filters.hasPiloting(self)), 4));
        modifiers.add(new MayNotBeForfeitedInBattleModifier(self, Filters.and(Filters.other(self), Filters.your(self), Filters.here(self), Filters.not(Filters.hit), Filters.character)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;


        Collection<PhysicalCard> utinniEffects = Filters.filterActive(game, self, Filters.and(Filters.utinniEffectThatCanTarget(Filters.and(self)), Filters.not(Filters.cardOnTableTargeting(self))));
        // Check condition(s)
        if (!utinniEffects.isEmpty()) {

            Set<PhysicalCard> possibleToRetarget = new HashSet<>();
            for (PhysicalCard card : utinniEffects) {
                for (TargetId targetId : card.getTargetedCards(game.getGameState()).keySet()) {
                    if (card.getValidTargetedFilter(targetId).accepts(game, self)) {
                        possibleToRetarget.add(card);
                    }
                }
            }

            if (!possibleToRetarget.isEmpty()) {
                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Retarget a Utinni Effect");

                action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose a Utinni Effect to retarget to " + GameUtils.getCardLink(self), Filters.in(possibleToRetarget)) {
                    @Override
                    protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                        action.allowResponses(new RespondableEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                action.appendEffect(new RetargetEffectEffect(action, targetedCard, self));
                            }
                        });
                    }
                });
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
