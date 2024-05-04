package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Starship
 * Subtype: Starfighter
 * Title: TIE Avenger
 */
public class Card4_172 extends AbstractStarfighter {
    public Card4_172() {
        super(Side.DARK, 3, 3, 2, null, 4, 2, 2, "TIE Avenger", Uniqueness.UNRESTRICTED, ExpansionSet.DAGOBAH, Rarity.C);
        setLore("Design based on Lord Vader's prototype Advanced x1. Manufactured until replaced by the more economical TIE Interceptor. Equipped with deflector shields and a hyperdrive.");
        setGameText("Deploy -1 to same system as any Imperial capital starship. May add 1 pilot. Boosted TIE Cannon may deploy aboard.");
        addIcons(Icon.DAGOBAH, Icon.NAV_COMPUTER);
        addModelType(ModelType.TIE_AD);
        setPilotCapacity(1);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.sameSystemAs(self, Filters.and(Filters.Imperial_starship, Filters.capital_starship))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToTargetModifier(self, Filters.and(Filters.your(self), Filters.Boosted_TIE_Cannon), self));
        return modifiers;
    }
}
