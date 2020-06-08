package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Theed Palace
 * Type: Character
 * Subtype: Republic
 * Title: Panaka, Protector Of The Queen
 */
public class Card14_023 extends AbstractRepublic {
    public Card14_023() {
        super(Side.LIGHT, 2, 4, 4, 3, 6, "Panaka, Protector Of The Queen", Uniqueness.UNIQUE);
        setLore("Leader of the Royal Naboo security Forces. Fought alongside Amidala in order to capture Viceroy Nute Gunray.");
        setGameText("Deploys for free to a Naboo site. While with Amidala at a Naboo site, opponent may not target Amidala with weapons. While on Naboo, immune to attrition < 5.");
        addPersona(Persona.PANAKA);
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.WARRIOR);
        addKeywords(Keyword.LEADER, Keyword.ROYAL_NABOO_SECURITY);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.Naboo_site));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeTargetedByWeaponsModifier(self, Filters.Amidala, new AndCondition(new WithCondition(self, Filters.Amidala),
                new AtCondition(self, Filters.Naboo_site))));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new OnCondition(self, Title.Naboo), 5));
        return modifiers;
    }
}
