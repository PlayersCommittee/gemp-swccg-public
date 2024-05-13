package com.gempukku.swccgo.cards.set221.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RecirculateEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetArmorModifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Character
 * Subtype: Imperial
 * Title: General Tagge (V)
 */
public class Card221_020 extends AbstractImperial {
    public Card221_020() {
        super(Side.DARK, 1, 3, 3, 3, 5, Title.Tagge, Uniqueness.UNIQUE, ExpansionSet.SET_21, Rarity.V);
        setVirtualSuffix(true);
        setLore("Oversees defense operations of Death Star. Outstanding tactician. No-nonsense leader. Member of House of Tagge, a powerful noble family and corporate conglomerate.");
        setGameText("Your non-[Maintenance] troopers have armor = 4. At same and adjacent sites where you have a non-pilot trooper, your total power is +3. Once per game, if with two troopers, may re-circulate.");
        addKeywords(Keyword.GENERAL, Keyword.LEADER);
        addIcons(Icon.WARRIOR, Icon.VIRTUAL_SET_21);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ResetArmorModifier(self, Filters.and(Filters.your(self), Filters.not(Icon.MAINTENANCE), Filters.trooper), 4));
        modifiers.add(new TotalPowerModifier(self, Filters.and(Filters.sameOrAdjacentSite(self), Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.not(Filters.pilot), Filters.trooper))), 3, playerId));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {

        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.GENERAL_TAGGE__ONCE_PER_GAME_RECIRCULATE;

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.isWith(game, self, 2, Filters.trooper)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Re-circulate");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerGameEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new RecirculateEffect(action, playerId));

            actions.add(action);
        }

        return actions;
    }
}
