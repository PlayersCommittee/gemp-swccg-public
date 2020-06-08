package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractSquadron;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Starship
 * Subtype: Squadron
 * Title: The Emperor's Shield
 */
public class Card9_172 extends AbstractSquadron {
    public Card9_172() {
        super(Side.DARK, 2, 5, 3, null, 3, null, 6, "The Emperor's Shield", Uniqueness.UNIQUE);
        setLore("Top outer rim pilots hand picked by Admiral Thrawn. Sent to protect the Emperor during his inspection of the second Death Star.");
        setGameText("Deploys -2 to Endor, Death Star II or same location as Thrawn. May deploy as a 'react'. Permanent pilots provide total ability of 3. Power +3 at Endor or when Emperor is at same or related location.");
        addIcons(Icon.DEATH_STAR_II);
        addIcon(Icon.PILOT, 3);
        addKeywords(Keyword.NO_HYPERDRIVE);
        addModelTypes(ModelType.TIE_LN, ModelType.TIE_LN, ModelType.TIE_LN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.or(Filters.Deploys_at_Endor, Filters.Deploys_at_Death_Star_II, Filters.sameLocationAs(self, Filters.Thrawn))));
        modifiers.add(new MayDeployAsReactModifier(self));
        return modifiers;
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        List<AbstractPermanentAboard> permanentsAboard = new ArrayList<AbstractPermanentAboard>();
        permanentsAboard.add(new AbstractPermanentPilot(1) {});
        permanentsAboard.add(new AbstractPermanentPilot(1) {});
        permanentsAboard.add(new AbstractPermanentPilot(1) {});
        return permanentsAboard;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new OrCondition(new AtCondition(self, Title.Endor), new AtCondition(self, Filters.Emperor, Filters.sameOrRelatedLocation(self))), 3));
        return modifiers;
    }
}
