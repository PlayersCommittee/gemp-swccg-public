package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.InBombingRunBattleCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Starfighter
 * Title: Scimitar Squadron TIE
 */
public class Card9_168 extends AbstractStarfighter {
    public Card9_168() {
        super(Side.DARK, 2, 2, 1, null, 2, null, 4, "Scimitar Squadron TIE", Uniqueness.RESTRICTED_3);
        setLore("Bombers typically assigned to attack secondary targets and provide cover during bombing runs. Targeting systems calibrated to track fast-moving Rebel starfighters.");
        setGameText("Permanent pilot provides ability of 2. Power +3 during a Bombing Run battle. Cumulatively adds 1 to total weapon destiny of your other TIE/sa present firing Concussion Missiles.");
        addIcons(Icon.DEATH_STAR_II, Icon.PILOT);
        addKeywords(Keyword.SCIMITAR_SQUADRON, Keyword.NO_HYPERDRIVE);
        addModelType(ModelType.TIE_SA);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourOtherTieSaPresent = Filters.and(Filters.your(self), Filters.other(self), Filters.TIE_sa, Filters.present(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new InBombingRunBattleCondition(self), 3));
        modifiers.add(new TotalWeaponDestinyModifier(self, Filters.and(Filters.Concussion_Missiles, Filters.attachedTo(yourOtherTieSaPresent)),
                yourOtherTieSaPresent, 1, true));
        return modifiers;
    }
}
