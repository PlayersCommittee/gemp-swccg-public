package com.gempukku.swccgo.cards.set9.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Type: Character
 * Subtype: Imperial
 * Title: Colonel Davod Jon
 */
public class Card9_104 extends AbstractImperial {
    public Card9_104() {
        super(Side.DARK, 2, 4, 3, 2, 5, "Colonel Davod Jon", Uniqueness.UNIQUE, ExpansionSet.DEATH_STAR_II, Rarity.U);
        setLore("Scout. Special forces leader. Convinced superiors that wilderness survival and recon training are necessary for militia dominance, despite technological superiority.");
        setGameText("While at an exterior planet site, power +2, immune to attrition < 3 and draws one battle destiny if not able to otherwise. When present with a Scomp link, cancels Surprise Assault at a related site where you have a spy or scout.");
        addIcons(Icon.DEATH_STAR_II, Icon.WARRIOR);
        addKeywords(Keyword.SCOUT, Keyword.LEADER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atExteriorPlanetSite = new AtCondition(self, Filters.exterior_planet_site);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, atExteriorPlanetSite, 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, atExteriorPlanetSite, 3));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, atExteriorPlanetSite, 1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Surprise_Assault)
                && GameConditions.isDuringForceDrainAt(game, Filters.and(Filters.relatedSite(self),
                Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.or(Filters.spy, Filters.scout)))))
                && GameConditions.isAtScompLink(game, self)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}
