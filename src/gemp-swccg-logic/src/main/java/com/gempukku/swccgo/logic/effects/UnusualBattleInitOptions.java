package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.modifiers.MayNotHideFromBattleModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotReactFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.NoBattleDamageModifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Options bag for initiating a battle outside the normal "your battle phase + location never battled this turn" path.
 * Shared by Here We Go Again, Counterattack, and future unusual battles. Do not fork a second re-battle system.
 */
public class UnusualBattleInitOptions {
    private final String _playerId;
    private final PhysicalCard _location;
    private final PhysicalCard _sourceCard;
    private final boolean _forFree;
    private final boolean _clearPriorParticipationForLocation;
    private final Collection<Modifier> _extraUntilEndOfBattleModifiers;

    public UnusualBattleInitOptions(String playerId, PhysicalCard location, PhysicalCard sourceCard, boolean forFree,
                                    boolean clearPriorParticipationForLocation, Collection<Modifier> extraUntilEndOfBattleModifiers) {
        _playerId = playerId;
        _location = location;
        _sourceCard = sourceCard;
        _forFree = forFree;
        _clearPriorParticipationForLocation = clearPriorParticipationForLocation;
        _extraUntilEndOfBattleModifiers = extraUntilEndOfBattleModifiers != null ? extraUntilEndOfBattleModifiers : Collections.emptyList();
    }

    /**
     * Shared Here We Go Again / Counterattack package: interrupt player is initiator, participation at the
     * re-battle location is cleared, initiate cost is paid, and printed react/hide/loser-BD restrictions apply.
     */
    public static UnusualBattleInitOptions reinitiateBattle(String playerId, PhysicalCard location, PhysicalCard sourceCard,
                                                            String opponent, Collection<Modifier> additionalModifiers) {
        List<Modifier> extras = new LinkedList<Modifier>();
        extras.addAll(restrictionModifiers(sourceCard, location, playerId, opponent));
        if (additionalModifiers != null) {
            extras.addAll(additionalModifiers);
        }
        return new UnusualBattleInitOptions(playerId, location, sourceCard, false, true, extras);
    }

    /**
     * React/hide/loser-BD package for this re-battle only.
     * Reacts allowed only for opponent of the interrupt player; neither side may react away or hide;
     * loser of this battle ignores battle damage.
     */
    public static List<Modifier> restrictionModifiers(PhysicalCard source, PhysicalCard location, String interruptPlayer, String opponent) {
        List<Modifier> modifiers = new ArrayList<Modifier>();
        modifiers.add(new MayNotReactToLocationModifier(source, Filters.sameCardId(location), interruptPlayer));
        modifiers.add(new MayNotReactFromLocationModifier(source, Filters.sameCardId(location), interruptPlayer));
        modifiers.add(new MayNotReactFromLocationModifier(source, Filters.sameCardId(location), opponent));
        modifiers.add(new MayNotHideFromBattleModifier(source, Filters.sameCardId(location)));
        modifiers.add(new NoBattleDamageModifier(source, Filters.sameCardId(location), interruptPlayer));
        modifiers.add(new NoBattleDamageModifier(source, Filters.sameCardId(location), opponent));
        return modifiers;
    }

    public String getPlayerId() {
        return _playerId;
    }

    public PhysicalCard getLocation() {
        return _location;
    }

    public PhysicalCard getSourceCard() {
        return _sourceCard;
    }

    public boolean isForFree() {
        return _forFree;
    }

    public boolean isClearPriorParticipationForLocation() {
        return _clearPriorParticipationForLocation;
    }

    public Collection<Modifier> getExtraUntilEndOfBattleModifiers() {
        return _extraUntilEndOfBattleModifiers;
    }
}
