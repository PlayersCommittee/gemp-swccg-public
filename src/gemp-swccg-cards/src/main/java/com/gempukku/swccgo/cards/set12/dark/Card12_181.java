package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Starship
 * Subtype: Starfighter
 * Title: Droid Starfighter
 */
public class Card12_181 extends AbstractStarfighter {
    public Card12_181() {
        super(Side.DARK, 2, 2, 3, null, 3, null, 2, "Droid Starfighter", Uniqueness.UNRESTRICTED, ExpansionSet.CORUSCANT, Rarity.C);
        setLore("These automated starfighters are managed by the Droid Control Ship and are used en masse by the Trade Federation to overwhelm an opponent with sheer numbers.");
        setGameText("Deploys -1 to same location as your battleship. Power -1 unless your droid control ship present. While another droid starfighter here, opponent's starfighters present are each power -1.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.TRADE_FEDERATION, Icon.PILOT, Icon.PRESENCE);
        addKeywords(Keyword.NO_HYPERDRIVE);
        addModelType(ModelType.DROID_STARFIGHTER);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot() {});
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.battleship))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new UnlessCondition(new PresentCondition(self, Filters.and(Filters.your(self), Filters.droid_control_ship))), -1));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.opponents(self), Filters.starfighter, Filters.present(self)),
                new HereCondition(self, Filters.and(Filters.other(self), Filters.droid_starfighter)), -1));
        return modifiers;
    }
}
