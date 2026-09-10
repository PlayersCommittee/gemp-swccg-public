package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SubtractDestinyFromFerocityModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Type: Effect
 * Subtype: Immediate
 * Title: Disarming Creature
 */
public class Card3_033 extends AbstractImmediateEffect {
    public Card3_033() {
        super(Side.LIGHT, 6, PlayCardZoneOption.ATTACHED, Title.Disarming_Creature, Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.R1);
        setLore("Luke's defensive maneuver put him out of arm's way.");
        setGameText("If you have a character with a weapon at same site as any creature (except Space Slug), deploy on that creature during any deploy phase, Creature is Disarmed (each time ferocity is calculated, draw destiny, subtract that amount). (Immune to Control.)");
        addIcons(Icon.HOTH);
        addKeywords(Keyword.DISARMING_CARD);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected boolean canPlayCardDuringCurrentPhase(String playerId, SwccgGame game, PhysicalCard self) {
        return GameConditions.isDuringEitherPlayersPhase(game, Phase.DEPLOY);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.creature, Filters.not(Filters.Space_Slug),
                Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.character_with_a_weapon)));
    }

    @Override
    public Filter getValidTargetFilterToRemainAttachedTo(SwccgGame game, PhysicalCard self) {
        return Filters.and(Filters.creature, Filters.not(Filters.Space_Slug));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SubtractDestinyFromFerocityModifier(self, Filters.hasAttached(self)));
        return modifiers;
    }
}