package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.DeployOnlyUsingOwnForceToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetDeployCostToLocationModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Jawa Camp
 */
public class Card1_131 extends AbstractSite {
    public Card1_131() {
        super(Side.LIGHT, Title.Jawa_Camp, Title.Tatooine);
        setLocationLightSideGameText("Your Jawas deploy here for 1 Force from you only, for free if Sandcrawler present.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self) {
        Filter yourJawas = Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.Jawa);
        Condition sandcrawlerPresent = new PresentCondition(self, Filters.sandcrawler);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployOnlyUsingOwnForceToLocationModifier(self, yourJawas, self));
        modifiers.add(new ResetDeployCostToLocationModifier(self, yourJawas, new NotCondition(sandcrawlerPresent), 1, self));
        modifiers.add(new DeploysFreeToLocationModifier(self, yourJawas, sandcrawlerPresent, self));
        return modifiers;
    }
}