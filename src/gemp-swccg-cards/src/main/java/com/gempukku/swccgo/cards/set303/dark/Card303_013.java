package com.gempukku.swccgo.cards.set303.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.logic.modifiers.CancelForceIconsModifier;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Shadow Academy
 * Type: Location
 * Subtype: Site
 * Title: Shadow Academy: Training Grounds
 */
public class Card303_013 extends AbstractSite {
    public Card303_013() {
        super(Side.DARK, Title.Shadow_Academy_Study_Room, Title.Arx, Uniqueness.UNIQUE, ExpansionSet.SA, Rarity.U);
        setLocationDarkSideGameText("If opponent has presence here, your Force icon here is cancelled.");
        setLocationLightSideGameText("");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 0);
        addIcons(Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
		addKeywords(Keyword.SHADOW_ACADEMY_LOCATION);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelForceIconsModifier(self, Filters.here(self), new OccupiesCondition(game.getOpponent(playerOnDarkSideOfLocation), self), playerOnDarkSideOfLocation));
        return modifiers;
    }
}