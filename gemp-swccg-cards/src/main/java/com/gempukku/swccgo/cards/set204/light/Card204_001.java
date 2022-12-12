package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.BreakCoversEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotUseCardToTransportToOrFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 4
 * Type: Character
 * Subtype: Droid
 * Title: BB-8 (Beebee-Ate)
 */
public class Card204_001 extends AbstractDroid {
    public Card204_001() {
        super(Side.LIGHT, Math.PI, 2, 1, 4, "BB-8 (Beebee-Ate)", Uniqueness.UNIQUE, ExpansionSet.SET_4, Rarity.V);
        setAlternateDestiny(2 * Math.PI);
        addPersona(Persona.BB8);
        setGameText("If with a Resistance leader at a battleground site, Force drain +1 here. During your move phase, may place in Used Pile; 'break cover' of all Undercover spies here (if any). Elis Helrot may not transport cards to or from here. Immune to Restraining Bolt.");
        addIcons(Icon.EPISODE_VII, Icon.NAV_COMPUTER, Icon.VIRTUAL_SET_4);
        addModelType(ModelType.ASTROMECH);
    }


    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceDrainModifier(self, Filters.and(Filters.here(self), Filters.battleground_site), new WithCondition(self, Filters.Resistance_leader), 1, self.getOwner()));
        modifiers.add(new MayNotUseCardToTransportToOrFromLocationModifier(self, Filters.Elis_Helrot, Filters.here(self)));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Restraining_Bolt));
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
