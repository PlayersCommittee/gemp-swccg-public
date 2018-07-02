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
 * Title: The Emperor's Sword
 */
public class Card9_173 extends AbstractSquadron {
    public Card9_173() {
        super(Side.DARK, 2, 5, 3, null, 3, null, 6, "The Emperor's Sword", Uniqueness.UNIQUE);
        setLore("Elite pilots sworn to defend the Emperor to the death. Assigned older TIEs due to their years of experience with them. Stationed at Coruscant but always travel with the Emperor.");
        setGameText("Deploys -2 to Coruscant or Death Star II. May deploy as a 'react'. Permanent pilots provide total ability of 3. Power +3 at Coruscant or when Emperor is at same or related location.");
        addIcons(Icon.DEATH_STAR_II);
        addIcon(Icon.PILOT, 3);
        addKeywords(Keyword.NO_HYPERDRIVE);
        addModelTypes(ModelType.TIE_LN, ModelType.TIE_LN, ModelType.TIE_LN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -2, Filters.or(Filters.Deploys_at_Coruscant, Filters.Deploys_at_Death_Star_II)));
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
        modifiers.add(new PowerModifier(self, new OrCondition(new AtCondition(self, Title.Coruscant), new AtCondition(self, Filters.Emperor, Filters.sameOrRelatedLocation(self))), 3));
        return modifiers;
    }
}
