package com.gempukku.swccgo.cards.set215.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 15
 * Type: Effect
 * Title: Cell 2187 (V)
 */
public class Card215_005 extends AbstractNormalEffect {
    public Card215_005() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Cell_2187, Uniqueness.UNIQUE, ExpansionSet.SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Aren't you a little short for a stormtrooper?'");
        setGameText("If [Set 20] Leia imprisoned, deploy on table. [Set 8] Luke is a spy and stormtrooper. Chewie, Leia, and stormtroopers are immune to Nevar Yalnal, Physical Choke, and Put All Sections On Alert. Once per turn, may [download] a Death Star site. Immune to This Is Some Rescue! [Immune to Alter.]");
        addIcons(Icon.A_NEW_HOPE, Icon.VIRTUAL_SET_15);
        addImmuneToCardTitle(Title.Alter);
        addImmuneToCardTitle(Title.This_Is_Some_Rescue);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(Icon.VIRTUAL_SET_20, Filters.Leia, Filters.imprisoned));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter set8Luke = Filters.and(Filters.Luke, Icon.VIRTUAL_SET_8);
        List<Modifier> modifiers = new ArrayList<>();
        modifiers.add(new KeywordModifier(self, set8Luke, Keyword.SPY));
        modifiers.add(new KeywordModifier(self, set8Luke, Keyword.STORMTROOPER));
        Filter chewieLeiaStormtroopers = Filters.or(Filters.Chewie, Filters.Leia, Filters.stormtrooper);
        modifiers.add(new ImmuneToTitleModifier(self, chewieLeiaStormtroopers, Title.Nevar_Yalnal));
        modifiers.add(new ImmuneToTitleModifier(self, chewieLeiaStormtroopers, Title.Physical_Choke));
        modifiers.add(new ImmuneToTitleModifier(self, chewieLeiaStormtroopers, Title.Put_All_Sections_On_Alert));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.CELL_2187__DOWNLOAD_DEATH_STAR_SITE;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a Death Star site from Reserve Deck");
            action.appendUsage(
                    new OncePerTurnEffect(action)
            );
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Death_Star_site, true)
            );

            actions.add(action);
        }

        return actions;
    }
}