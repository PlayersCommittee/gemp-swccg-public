package com.gempukku.swccgo.cards.set225.dark;

import java.util.LinkedList;
import java.util.List;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

/**
 * Set: Set 25
 * Type: Character
 * Subtype: Alien
 * Title: Bib Fortuna (V)
 */
public class Card225_001 extends AbstractAlien {
    public Card225_001() {
        super(Side.DARK, 1, 2, 3, 1, 3, Title.Bib, Uniqueness.UNIQUE, ExpansionSet.SET_25, Rarity.V);
        setLore("Twi'lek leader and majordomo of Jabba's palace. Succeeded Jabba's last majordomo, Naroon Cuthus. Plotting to kill Jabba.");
        setGameText("Deploys free to opponent's Audience Chamber. May [download] [Jabba's Palace] No Bargain. While with Jabba, Bib is power +2 and Jabba is immune to attrition. While at Audience Chamber, I Must Be Allowed To Speak may not target this site.");
        addPersona(Persona.BIB);
        addIcons(Icon.JABBAS_PALACE, Icon.VIRTUAL_SET_25);
        setSpecies(Species.TWILEK);
        addKeywords(Keyword.LEADER);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.and(Filters.opponents(self), Icon.JABBAS_PALACE, Filters.site)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.BIB_FORTUNA__DOWNLOAD_CARD;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Title.No_Bargain)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy card from Reserve Deck");
            action.setActionMsg("Deploy [Jabba's Palace] No Bargain here from Reserve Deck");

            // Perform result(s)
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.and(Icon.JABBAS_PALACE, Filters.No_Bargain), Filters.here(self), true));

            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition withJabba = new WithCondition(self, Filters.Jabba);
        Condition atAudienceChamber = new AtCondition(self, Filters.Audience_Chamber);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, withJabba, 2));
        modifiers.add(new ImmuneToAttritionModifier(self, Filters.Jabba, withJabba));

        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.sameSite(self), atAudienceChamber, Filters.title(Title.I_Must_Be_Allowed_To_Speak)));

        return modifiers;
    }
}
