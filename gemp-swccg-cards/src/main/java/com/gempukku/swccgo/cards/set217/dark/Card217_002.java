package com.gempukku.swccgo.cards.set217.dark;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.BreakCoversEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 17
 * Type: Character
 * Subtype: Droid
 * Title: BB-9E
 */
public class Card217_002 extends AbstractDroid {
    public Card217_002() {
        super(Side.DARK, 0.5, 2, 2, 4, "BB-9E", Uniqueness.UNIQUE);
        setAlternateDestiny(5.5);
        setGameText("Deploys -1 to same site as BB-8. Cancels game text of BB-8 and/or Rose at same site. During your move phase, may place in Used Pile; 'break cover' of all Undercover spies here (if any). Immune to Restraining Bolt and Sorry About The Mess.");
        addIcons(Icon.EPISODE_VII, Icon.NAV_COMPUTER, Icon.VIRTUAL_SET_17);
        addModelType(ModelType.ASTROMECH);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.sameSiteAs(self, Filters.BB8)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new CancelsGameTextModifier(self, Filters.and(Filters.or(Filters.BB8, Filters.Rose), Filters.atSameSite(self))));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Restraining_Bolt));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Sorry_About_The_Mess));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        Filter targetFilter = Filters.and(Filters.undercover_spy, Filters.atSameSite(self));

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.MOVE)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Place in Used Pile");
            action.setActionMsg("'Break cover' of all undercover spies here (if any)");

            action.appendCost(
                    new PlaceCardInUsedPileFromTableEffect(action, self));

            Collection<PhysicalCard> undercoverSpies = Filters.filterAllOnTable(game, Filters.and(targetFilter, Filters.canBeTargetedBy(self)));
            action.appendEffect(
                    new BreakCoversEffect(action, undercoverSpies));

            actions.add(action);
        }

        return actions;
    }
}
