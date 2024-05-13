package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.OnCloudCityCondition;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.conditions.PresentAtScompLink;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.MayBeReplacedByOpponentModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeReplacedByOpponentModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Lobot
 */
public class Card7_187 extends AbstractAlien {
    public Card7_187() {
        super(Side.DARK, 1, 2, 2, 2, 3, "Lobot", Uniqueness.UNIQUE, ExpansionSet.SPECIAL_EDITION, Rarity.R);
        setLore("Son of a traveling slaver. Helps run Cloud City with Administrator Lando Calrissian. Speech capability worn away by constant cyborg neural connection.");
        setGameText("Deploys only on Cloud City. Power +2 when present at a Scomp Link. If present at a site, can be replaced by opponent with any Light Side Lobot. While present on Cloud City with your Lando, prevents replacement of Lobot and Lando.");
        addPersona(Persona.LOBOT);
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Deploys_on_Cloud_City;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition presentOnCloudCityWithYourLando = new AndCondition(new PresentCondition(self), new OnCloudCityCondition(self),
                new PresentWithCondition(self, Filters.and(Filters.your(self), Filters.Lando)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new PresentAtScompLink(self), 2));
        modifiers.add(new MayBeReplacedByOpponentModifier(self, new PresentAtCondition(self, Filters.site)));
        modifiers.add(new MayNotBeReplacedByOpponentModifier(self, Filters.or(Filters.Lobot, Filters.Lando), presentOnCloudCityWithYourLando));
        return modifiers;
    }
}
