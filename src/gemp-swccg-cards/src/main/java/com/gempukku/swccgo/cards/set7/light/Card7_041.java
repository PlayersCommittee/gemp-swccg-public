package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.InBattleAtCondition;
import com.gempukku.swccgo.cards.conditions.OnCloudCityCondition;
import com.gempukku.swccgo.cards.evaluators.InBattleEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Sergeant Edian
 */
public class Card7_041 extends AbstractAlien {
    public Card7_041() {
        super(Side.LIGHT, 3, 2, 2, 1, 3, "Sergeant Edian", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.U);
        setLore("Veteran of the Cloud City security forces. Loyal to Administrator Calrissian. Disdainful of the corruption of other Cloud City troopers.");
        setGameText("Deploys free on Cloud City if your Lando is at a related location. Power +2 on Cloud City. When in a battle at a Cloud City site, adds 1 to your total battle destiny for each of your other Cloud City troopers in that battle.");
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR);
        addKeywords(Keyword.CLOUD_CITY_TROOPER);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, new AtCondition(self, Filters.and(Filters.your(self), Filters.Lando),
                Filters.relatedLocationTo(self, Filters.Cloud_City_site)),
                Filters.and(Filters.Cloud_City_site, Filters.relatedSiteTo(self, Filters.and(Filters.your(self), Filters.Lando)))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new OnCloudCityCondition(self), 2));
        modifiers.add(new TotalBattleDestinyModifier(self, new InBattleAtCondition(self, Filters.Cloud_City_site),
                new InBattleEvaluator(self, Filters.and(Filters.your(self), Filters.other(self), Filters.Cloud_City_trooper)),
                self.getOwner()));
        return modifiers;
    }
}
