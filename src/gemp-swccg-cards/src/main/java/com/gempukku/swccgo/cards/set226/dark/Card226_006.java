package com.gempukku.swccgo.cards.set226.dark;

import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitIncreaseLimitModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

/**
 * Set: Set 26
 * Type: Effect
 * Title: I'm Sorry (V)
 */
public class Card226_006 extends AbstractNormalEffect {
    public Card226_006() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Im_Sorry, Uniqueness.UNIQUE, ExpansionSet.SET_26, Rarity.V);
        setLore("'I'm sorry, too.'");
        setGameText("If your [Cloud City] objective on table, deploy on table. Elis Helrot, Stunning Leader, and Surreptitious Glance may not be played. Once per turn, may [download] an interior Cloud City site. Your unique (•) characters with printed forfeit < 5 are forfeit +2 (limit +2). [Immune to Alter.]");
        addIcons(Icon.TATOOINE, Icon.CLOUD_CITY, Icon.VIRTUAL_SET_26);
        addImmuneToCardTitle(Title.Alter);
        setVirtualSuffix(true);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.and(Filters.your(playerId), Icon.CLOUD_CITY, Filters.Objective));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        Filter yourUniqueCharactersWithPrintedForfeitLessThanFive = Filters.and(Filters.your(playerId), Filters.unique, Filters.character, Filters.printedForfeitValueLessThan(5));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotPlayModifier(self, Filters.or(Filters.Elis_Helrot, Filters.Stunning_Leader, Filters.Surreptitious_Glance)));
        modifiers.add(new ForfeitModifier(self, yourUniqueCharactersWithPrintedForfeitLessThanFive, 2));
        modifiers.add(new ForfeitIncreaseLimitModifier(self, yourUniqueCharactersWithPrintedForfeitLessThanFive, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.IM_SORRY_V__DOWNLOAD_SITE;

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);

            action.setText("Deploy site from Reserve Deck");
            action.setActionMsg("Deploy an interior Cloud City site from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.interior_site, Filters.Cloud_City_site), true));
            actions.add(action);
        }
        return actions;
    }
}
