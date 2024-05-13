package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FireWeaponEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.HyperspeedModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Effect
 * Title: Evacuation Control
 */
public class Card3_035 extends AbstractNormalEffect {
    public Card3_035() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, "Evacuation Control", Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.U1);
        setLore("'Give the evacuation code signal...and get to your transports!'");
        setGameText("Deploy on your war room. Once during each of your move phases, your Planet Defender Ion Cannon at same planet may fire. Also, each of your medium transports at same planet is hyperspeed +2, is immune to attrition < 3 and may move for free.");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
        addIcons(Icon.HOTH);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.war_room);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.MOVE)) {
            Filter weaponFilter = Filters.and(Filters.your(self), Filters.Planet_Defender_Ion_Cannon, Filters.atSamePlanet(self), Filters.canBeFired(self, 0));
            if (GameConditions.canSpot(game, self, weaponFilter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Fire Planet Defender Ion Cannon");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new ChooseCardOnTableEffect(action, playerId, "Choose Planet Defender Ion Cannon", weaponFilter) {
                            @Override
                            protected void cardSelected(PhysicalCard weapon) {
                                action.addAnimationGroup(weapon);
                                action.setActionMsg("Fire " + GameUtils.getCardLink(weapon));
                                // Perform result(s)
                                action.appendEffect(
                                        new FireWeaponEffect(action, weapon, false, Filters.any));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.your(self), Filters.medium_transport, Filters.atSamePlanet(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new HyperspeedModifier(self, filter, 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, filter, 3));
        modifiers.add(new MovesForFreeModifier(self, filter));
        return modifiers;
    }
}