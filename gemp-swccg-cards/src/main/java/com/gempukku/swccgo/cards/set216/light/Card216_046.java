package com.gempukku.swccgo.cards.set216.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.AloneAtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.DeployAsCaptiveOption;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.ReactActionOption;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.CancelForceIconsModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Effect
 * Title: Wookiee Homestead
 */
public class Card216_046 extends AbstractNormalEffect {
    public Card216_046() {
        super(Side.LIGHT, 0, PlayCardZoneOption.ATTACHED, Title.Wookiee_Homestead, Uniqueness.UNIQUE, ExpansionSet.SET_16, Rarity.V);
        setLore("");
        setGameText("Deploy on Kachirho. Your Wookiees deploy -1. If you have two Wookiees in battle, draw one battle destiny if unable to otherwise. While your Wookiee alone here, [Dark Side] icons here are canceled. [Immune to Alter.]");
        addIcons(Icon.EPISODE_I, Icon.VIRTUAL_SET_16);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getValidDeployTargetFilter(String playerId, SwccgGame game, PhysicalCard self, PhysicalCard sourceCard, PlayCardOption playCardOption, boolean forFree, float changeInCost, DeploymentRestrictionsOption deploymentRestrictionsOption, DeployAsCaptiveOption deployAsCaptiveOption, ReactActionOption reactActionOption, boolean isSimDeployAttached, boolean ignorePresenceOrForceIcons) {
        return Filters.title(Title.Kachirho);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourWookiees = Filters.and(Filters.your(self), Filters.Wookiee);
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostModifier(self, yourWookiees, -1));
        modifiers.add(new CancelForceIconsModifier(self, Filters.here(self), new AloneAtCondition(self, yourWookiees, Filters.here(self)), game.getOpponent(self.getOwner())));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, Filters.and(yourWookiees, Filters.inBattleWith(yourWookiees)), 1));
        //TODO fix this

        return modifiers;
    }

}