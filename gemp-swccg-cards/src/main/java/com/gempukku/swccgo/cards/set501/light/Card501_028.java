package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Effect
 * Title: Slimy Piece Of Worm-Ridden Filth!
 */
public class Card501_028 extends AbstractNormalEffect {
    public Card501_028() {
        super(Side.LIGHT, 7, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Slimy Piece Of Worm-Ridden Filth!", Uniqueness.UNIQUE);
        setLore("'Aacccck!'");
        setGameText("Deploy on table. [Jabba's Palace] Leia may target a warrior at Audience Chamber instead of Jabba. May deploy [Jabba's Palace] Leia from Reserve Deck; reshuffle. Your aliens are immune to Dr. Evazan and Sniper. Seeking An Audience is immune to Alter. [Immune to Alter.]");
        addIcons(Icon.JABBAS_PALACE, Icon.VIRTUAL_SET_13);
        addImmuneToCardTitle(Title.Alter);
        setTestingText("Slimy Piece Of Worm-Ridden Filth!");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.title(Title.Seeking_An_Audience), Title.Alter));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.your(self.getOwner()), Filters.alien), Title.Sniper));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.your(self.getOwner()), Filters.alien), Title.Dr_Evazan));
        //Broken Link (JP leia still needs to be coded)
        modifiers.add(new ModifyGameTextModifier(self, Filters.and(Filters.Leia, Icon.JABBAS_PALACE), ModifyGameTextType.LEIA_JABBAS_PALACE__TARGET_WARRIOR_AT_AUDIENCE_CHAMBER_INSTEAD_OF_JABBA));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId gameTextActionId = GameTextActionId.SLIMY_PIECE_OF_WORM_RIDDEN_FILTH__DEPLOY_JABBAS_PALACE_LEIA;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId, Persona.LEIA)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Leia from Reserve Deck");
            action.setActionMsg("Deploy [Jabba's Palace] Leia from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Leia, Icon.JABBAS_PALACE), true));
            actions.add(action);

            return Collections.singletonList(action);
        }

        return null;
    }
}
