package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractCapitalStarship;
import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardAboardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 3
 * Type: Starship
 * Subtype: Capital
 * Title: Jabba's Space Cruiser (V)
 */

public class Card601_012 extends AbstractCapitalStarship {
    public Card601_012() {
        super(Side.DARK, 2, 4, 5, 5, null, 4, 6, Title.Jabbas_Space_Cruiser, Uniqueness.UNIQUE);
        setLore("Flying fortress of Jabba Desilijic Tiure. Reaches speeds of 800 kph in atmosphere. The crime lord installed hidden gunports as an unpleasant surprise for would-be pirates.");
        setGameText("Deploys and moves like a starfighter. Permanent pilot provides ability of 2. May add 1 alien pilot and 6 passengers. Your [Independent] starships may deploy here as a 'react'. If Jabba here, immune to attrition.");
        addIcons(Icon.INDEPENDENT, Icon.PILOT, Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.BLOCK_3, Icon.SPECIAL_EDITION);
        addModelType(ModelType.UBRIKKIAN_LUXURY_SPACE_YACHT);
        addKeywords(Keyword.CRUISER);
        setPilotCapacity(1);
        setPassengerCapacity(6);
        setVirtualSuffix(true);
        setAsLegacy(true);
    }

    @Override
    public boolean isDeploysAndMovesLikeStarfighter() {
        return true;
    }

    // Must be alien pilot
    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.alien;
    }

    // one permanent pilot of ability 2
    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        List<AbstractPermanentAboard> permanentsAboard = new ArrayList<AbstractPermanentAboard>();
        permanentsAboard.add(new AbstractPermanentPilot(2) {});
        return permanentsAboard;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionModifier(self, new HereCondition(self, Filters.Jabba)));
        modifiers.add(new MayDeployOtherCardsAsReactToLocationModifier(self, "Deploy starship as a react", self.getOwner(), Filters.and(Filters.your(self), Icon.INDEPENDENT, Filters.starship), Filters.here(self)));
        return modifiers;
    }
}