package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Effect
 * Title: Imperial Arrest Order
 */
public class Card8_126 extends AbstractNormalEffect {
    public Card8_126() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Imperial_Arrest_Order, Uniqueness.UNIQUE);
        setLore("When an Imperial blockade raises the alert level, all independent ships are scanned and any suspicious characters on planet are detained and interrogated.");
        setGameText("Deploy on table. Unique (•) Imperials of ability < 3 are forfeit +2. Nabrun Leids and Elis Helrot are limited to owner's move phase and exterior sites. Once during each of your deploy phases, may deploy one docking bay from Reserve Deck; reshuffle. (Immune to Alter.)");
        addIcons(Icon.ENDOR);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.unique, Filters.Imperial, Filters.abilityLessThan(3)), 2));
        modifiers.add(new ModifyGameTextModifier(self, Filters.or(Filters.Nabrun_Leids, Filters.Elis_Helrot), ModifyGameTextType.NABRUN_LEIDS_ELIS_HELROT__LIMIT_USAGE));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.IMPERIAL_ARREST_ORDER__DOWNLOAD_DOCKING_BAY;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy docking bay from Reserve Deck");
            action.setActionMsg("Deploy a docking bay from Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.docking_bay, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}