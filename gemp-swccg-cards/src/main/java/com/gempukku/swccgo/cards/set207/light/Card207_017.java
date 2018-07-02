package com.gempukku.swccgo.cards.set207.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 7
 * Type: Starship
 * Subtype: Starfighter
 * Title: Ghost
 */
public class Card207_017 extends AbstractStarfighter {
    public Card207_017() {
        super(Side.LIGHT, 2, 3, 4, null, 4, 5, 6, Title.Ghost, Uniqueness.UNIQUE);
        setLore("Phoenix Squadron.");
        setGameText("May add 2 pilots, 3 passengers, and Phantom. Has ship-docking capability. May deploy Phantom here from Reserve deck; reshuffle. While Hera piloting, immune to attrition < 5 (may not be canceled).");
        addIcons(Icon.NAV_COMPUTER, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_7);
        addKeywords(Keyword.PHOENIX_SQUADRON, Keyword.SHIP_DOCKING_CAPABILITY);
        addModelType(ModelType.MODIFIED_VCX_FREIGHTER);
        setPilotCapacity(2);
        setPassengerCapacity(3);
        setStarfighterCapacity(1, Filters.Phantom);
        setMatchingPilotFilter(Filters.Hera);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition heraPiloting = new HasPilotingCondition(self, Filters.Hera);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, heraPiloting, 5));
        modifiers.add(new ImmunityToAttritionMayNotBeCanceledModifier(self, heraPiloting));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActionsEvenIfUnpiloted(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.GHOST__DOWNLOAD_PHANTOM;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.Phantom)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Phantom from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.Phantom, Filters.here(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
