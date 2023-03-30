package com.gempukku.swccgo.cards.set101.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.BonusAbilitiesEnabledCondition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.DestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveGameTextCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premium (Premiere Introductory Two Player Game)
 * Type: Character
 * Subtype: Rebel
 * Title: Luke
 */
public class Card101_002 extends AbstractRebel {
    public Card101_002() {
        super(Side.LIGHT, 1, 4, 2, 3, 4, "Luke", Uniqueness.UNIQUE, ExpansionSet.PREMIERE_INTRO_TWO_PLAYER, Rarity.PM);
        setLore("Raised by guardians Owen and Beru Lars on a moisture farm on Tatooine, where Owen wanted him to stay. Nicknamed 'Wormie' by childhood friends Camie and Fixer.");
        setGameText("Must deploy on Tatooine, but may move elsewhere. May not be deployed if two or more of opponent's unique (•) characters on table. Your warriors at same site as Luke, or adjacent sites are forfeit +1.");
        addPersona(Persona.LUKE);
        addIcons(Icon.WARRIOR);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        if (game.useBonusAbilities())
            return Filters.any;
        return Filters.Deploys_on_Tatooine;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        boolean add = modifiers.add(new MayNotDeployModifier(self, new AndCondition(new NotCondition(new BonusAbilitiesEnabledCondition()), new OnTableCondition(self, 2, Filters.and(Filters.opponents(self), Filters.unique, Filters.character)))));
        modifiers.add(new PowerModifier(self, new BonusAbilitiesEnabledCondition(), 2));
        modifiers.add(new ForfeitModifier(self, new BonusAbilitiesEnabledCondition(), 2));
        modifiers.add(new DefenseValueModifier(self, new BonusAbilitiesEnabledCondition(), 2));
        modifiers.add(new DestinyModifier(self, self, new BonusAbilitiesEnabledCondition(), 2));
        modifiers.add(new DeployCostModifier(self, self, new BonusAbilitiesEnabledCondition(), -2));
        modifiers.add(new IconModifier(self, new BonusAbilitiesEnabledCondition(), Icon.PILOT));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.your(self), Filters.warrior, Filters.atSameOrAdjacentSite(self)), 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new BonusAbilitiesEnabledCondition(), 5));
        modifiers.add(new PowerModifier(self, Filters.hasPiloting(self), new BonusAbilitiesEnabledCondition(), 4));
        modifiers.add(new AddsBattleDestinyModifier(self, new BonusAbilitiesEnabledCondition(), 1));
        modifiers.add(new MayNotHaveGameTextCanceledModifier(self, new BonusAbilitiesEnabledCondition()));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__HEAR_ME_BABY_HOLD_TOGETHER__UPLOAD_GRABBER;
        // Check condition(s)
        if (game.useBonusAbilities()
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.LEIA)
                || GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.HAN)
                || GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.CHEWIE)
                || GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.OBIWAN)
                || GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.R2D2)
                || GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.C3PO)
        )) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Luke's friends from Reserve Deck");
            action.appendUsage(new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.or(Filters.Leia, Filters.Han, Filters.Chewie, Filters.ObiWan, Filters.R2D2, Filters.C3PO, Filters.Rey), Filters.here(self), false, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
