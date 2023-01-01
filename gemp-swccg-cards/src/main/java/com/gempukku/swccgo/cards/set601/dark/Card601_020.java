package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyForWeaponTargetingModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Block 7
 * Type: Effect
 * Title: Mercenary Slavers
 */
public class Card601_020 extends AbstractNormalEffect {
    public Card601_020() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, "Mercenary Slavers", Uniqueness.UNIQUE, ExpansionSet.LEGACY, Rarity.V);
        setLore("Hunters who track down escaped Wookiee slaves and report on their location. The Empire and Trandoshans pay a high price for that kind of information.");
        setGameText("Unless your [Reflections II] objective on table, deploy on a site. Your smugglers and gangsters are slavers. Slavers are forfeit +2 and weapon destinies targeting them (or starships they pilot) are -1. 'Trandoshan' on your sites may be treated as 'slaver.' [Immune to Alter.]");
        addKeywords(Keyword.DEPLOYS_ON_SITE);
        addIcons(Icon.LEGACY_BLOCK_7, Icon.SPECIAL_EDITION);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return !Filters.canSpot(game, self, Filters.and(Filters.your(self), Icon.REFLECTIONS_II, Filters.Objective));
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.site;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, Filters.and(Filters.your(self), Filters.or(Filters.smuggler, Filters.gangster)), Keyword.SLAVER));
        modifiers.add(new ForfeitModifier(self, Filters.slaver, 2));
        modifiers.add(new EachWeaponDestinyForWeaponTargetingModifier(self, Filters.or(Filters.slaver, Filters.and(Filters.starship, Filters.hasPiloting(self, Filters.slaver))), -1));
        modifiers.add(new ModifyGameTextModifier(self, Filters.and(Filters.your(self), Filters.site), ModifyGameTextType.LEGACY__YOUR_SITES__TREAT_TRANDOSHAN_AS_SLAVER));
        return modifiers;
    }
}