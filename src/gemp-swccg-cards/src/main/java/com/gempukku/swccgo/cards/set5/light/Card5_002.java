package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.effects.ConvertLocationByRaisingToTopEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Character
 * Subtype: Alien
 * Title: Cloud City Technician
 */
public class Card5_002 extends AbstractAlien {
    public Card5_002() {
        super(Side.LIGHT, 2, 2, 1, 1, 2, "Cloud City Technician", Uniqueness.RESTRICTED_3, ExpansionSet.CLOUD_CITY, Rarity.C);
        setLore("Former Imperial technician disenchanted with the New Order. Sympathetic to the Alliance. His knowledge of Imperial computer systems makes him a valuable ally.");
        setGameText("When present at a converted site, may use 2 Force to raise your site to the top. Also, when present at an opponent's site that has a Scomp link, your Force drains are +1 there.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.canUseForce(game, playerId, 2)
                && GameConditions.isPresentAt(game, self, Filters.and(Filters.site, Filters.canBeConvertedByRaisingYourLocationToTop(playerId)))) {
            final PhysicalCard location = game.getModifiersQuerying().getLocationThatCardIsPresentAt(game.getGameState(), self);
            if (location != null) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Raise converted site to the top");
                action.setActionMsg("Raise converted site to the top to convert " + GameUtils.getCardLink(location));
                action.addAnimationGroup(location);
                // Pay cost(s)
                action.appendCost(
                        new UseForceEffect(action, playerId, 2));
                // Perform result(s)
                action.appendEffect(
                        new ConvertLocationByRaisingToTopEffect(action, location, true));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.here(self), new PresentAtCondition(self,
                Filters.and(Filters.opponents(self), Filters.site, Filters.has_Scomp_link)), 1, self.getOwner()));
        return modifiers;
    }
}
