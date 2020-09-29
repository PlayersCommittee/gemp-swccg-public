package com.gempukku.swccgo.game;

import com.gempukku.swccgo.PrivateInformationException;
import com.gempukku.swccgo.SubscriptionConflictException;
import com.gempukku.swccgo.SubscriptionExpiredException;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.communication.GameStateListener;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.state.GameCommunicationChannel;
import com.gempukku.swccgo.game.state.GameEvent;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.DefaultSwccgGame;
import com.gempukku.swccgo.logic.timing.DefaultUserFeedback;
import com.gempukku.swccgo.logic.timing.GameResultListener;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import com.google.common.base.Objects;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SwccgGameMediator {
    private static final Logger LOG = Logger.getLogger(SwccgGameMediator.class);

    private Map<String, GameCommunicationChannel> _communicationChannels = Collections.synchronizedMap(new HashMap<String, GameCommunicationChannel>());
    private DefaultUserFeedback _userFeedback;
    private SwccgGame _swccgoGame;
    private Map<String, Integer> _playerClocks = new HashMap<String, Integer>();
    private Map<String, Long> _decisionQuerySentTimes = new HashMap<String, Long>();
    private Set<SwccgGameParticipant> _playersPlaying = new HashSet<SwccgGameParticipant>();

    private String _gameId;
    private int _maxSecondsForGamePerPlayer;
    private boolean _allowSpectators;
    private boolean _cancelIfNoActions;
    private boolean _cancellable;
    private boolean _allowExtendGameTimer;
    private int _playerDecisionTimeoutPeriod; // in milliseconds
    private boolean _disablePlayerDecisionTimer;
    private int _secondsGameTimerExtended;

    private ReentrantReadWriteLock _lock = new ReentrantReadWriteLock(true);
    private ReentrantReadWriteLock.ReadLock _readLock = _lock.readLock();
    private ReentrantReadWriteLock.WriteLock _writeLock = _lock.writeLock();
    private int _channelNextIndex;
    private volatile boolean _destroyed;

    public SwccgGameMediator(String gameId, SwccgFormat swccgFormat, SwccgGameParticipant[] participants, SwccgCardBlueprintLibrary library, int maxSecondsForGamePerPlayer,
                             boolean allowSpectators, boolean cancelIfNoActions, boolean cancellable, boolean allowExtendGameTimer, int decisionTimeoutSeconds) {
        _gameId = gameId;
        _maxSecondsForGamePerPlayer = maxSecondsForGamePerPlayer;
        _allowSpectators = allowSpectators;
        _cancelIfNoActions = cancelIfNoActions;
        _cancellable = cancellable;
        _allowExtendGameTimer = allowExtendGameTimer;
        _playerDecisionTimeoutPeriod = decisionTimeoutSeconds * 1000;
        if (participants.length < 1)
            throw new IllegalArgumentException("Game can't have less than one participant");

        Map<String, SwccgDeck> decks = new HashMap<String, SwccgDeck>();

        for (SwccgGameParticipant participant : participants) {
            String participantId = participant.getPlayerId();
            decks.put(participantId, participant.getDeck());
            _playerClocks.put(participantId, 0);
            _playersPlaying.add(participant);
        }

        _userFeedback = new DefaultUserFeedback();
        _swccgoGame = new DefaultSwccgGame(swccgFormat, decks, _userFeedback, library);
        _userFeedback.setGame(_swccgoGame);
    }

    public boolean isDestroyed() {
        return _destroyed;
    }

    public void destroy() {
        _destroyed = true;
    }

    public String getGameId() {
        return _gameId;
    }

    public boolean isAllowSpectators() {
        return _allowSpectators;
    }

    public SwccgFormat getFormat() {
        return _swccgoGame.getFormat();
    }

    public void setPlayerAutoPassSettings(String playerId, Set<Phase> phases) {
        if (isPlayerPlaying(playerId)) {
            _swccgoGame.setPlayerAutoPassSettings(playerId, phases);
        }
    }

    public void sendMessageToPlayers(String message) {
        _swccgoGame.getGameState().sendMessage(message);
    }

    public void addGameStateListener(String playerId, GameStateListener listener) {
        _swccgoGame.addGameStateListener(playerId, listener);
    }

    public void removeGameStateListener(GameStateListener listener) {
        _swccgoGame.removeGameStateListener(listener);
    }

    public void addGameResultListener(GameResultListener listener) {
        _swccgoGame.addGameResultListener(listener);
    }

    public void removeGameResultListener(GameResultListener listener) {
        _swccgoGame.removeGameResultListener(listener);
    }

    public String getWinner() {
        return _swccgoGame.getWinner();
    }
    
    public String getWinningSideString() {
        if(_swccgoGame.isCancelled()||_swccgoGame.getWinner()==null||_swccgoGame.getSide(_swccgoGame.getWinner())==null)
            return "None";
        
        return _swccgoGame.getSide(_swccgoGame.getWinner()).getHumanReadable();
    }

    public List<SwccgGameParticipant> getPlayersPlaying() {
        return new LinkedList<SwccgGameParticipant>(_playersPlaying);
    }

    public boolean isPlayerPlaying(String playerId) {
        List<SwccgGameParticipant> players = getPlayersPlaying();
        for (SwccgGameParticipant player : players)
        {
            if (player.getPlayerId().equals(playerId))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the game status.
     * @return the game status
     */
    public String getGameStatus() {
        if (_swccgoGame.isCancelled())
            return "Cancelled";
        if (_swccgoGame.isFinished())
            return "Finished";
        final Phase currentPhase = _swccgoGame.getGameState().getCurrentPhase();
        if (currentPhase == Phase.PLAY_STARTING_CARDS)
            return "Preparation";
        return "Life Force: " + getPlayerLifeForce();
    }

    public boolean isFinished() {
        return _swccgoGame.isFinished();
    }

    /**
     * Produces the modifier text.
     * @param modifierCollector the modifier collector
     * @param card the card affected
     * @return the modifier text
     */
    private String produceModifierText(ModifierCollector modifierCollector, PhysicalCard card) {
        GameState gameState = _swccgoGame.getGameState();
        ModifiersQuerying modifiersQuerying = _swccgoGame.getModifiersQuerying();

        StringBuilder sb = new StringBuilder();
        List<Modifier> prevModifiers = modifierCollector.getPrevModifiers();
        List<Modifier> currentModifiers = modifierCollector.getCurrentModifiers();
        if (!currentModifiers.isEmpty()) {
            for (Modifier curModifier : currentModifiers) {
                PhysicalCard curSource = curModifier.getSource(gameState);
                String curText = curModifier.getText(gameState, modifiersQuerying, card);
                if (curText != null) {

                    boolean foundMatch = false;
                    for (Modifier prevModifier : prevModifiers) {
                        PhysicalCard prevSource = prevModifier.getSource(gameState);
                        String prevText = prevModifier.getText(gameState, modifiersQuerying, card);
                        if (Objects.equal(prevSource, curSource) && Objects.equal(prevText, curText)
                                && prevModifier.getModifierType() != ModifierType.EQUALIZE_FORCE_ICONS) {
                            foundMatch = true;
                            break;
                        }
                    }
                    if (!foundMatch) {
                        sb.append("<div class='cardModifier'>");
                        sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").append(curText);
                        if (curSource != null) {
                            if (curSource.isBluffCard())
                                sb.append(" from 'Bluff card'");
                            else
                                sb.append(" from ").append(GameUtils.getCardLink(curSource));
                        } else {
                            sb.append(" from <i>System</i>");
                        }
                        sb.append("</div>");
                    }
                }
            }
            modifierCollector.clear();
        }

        return sb.toString();
    }

    /**
     * Produces the card information to be shown on the user interface.
     * @param player the player requesting the card info
     * @param cardId the card ID
     * @return the card information
     */
    public String produceCardInfo(Player player, int cardId) {
        _readLock.lock();
        try {
            StringBuilder sb = new StringBuilder();
            GameState gameState = _swccgoGame.getGameState();
            ModifiersQuerying modifiersQuerying = _swccgoGame.getModifiersQuerying();
            ModifierCollector modifierCollector = new ModifierCollectorImpl();
            String darkPlayer = _swccgoGame.getDarkPlayer();
            String lightPlayer = _swccgoGame.getLightPlayer();
            PhysicalCard card = gameState.findCardById(cardId);
            if (card == null) {
                return sb.toString();
            }
            Zone cardZone = card.getZone();
            if (cardZone == null) {
                return sb.toString();
            }

            // Show special card indicators
            if (card.isBluffCard()) {
                sb.append("<div>").append("Bluff card").append("</div>");
            }
            if (card.isCombatCard()) {
                sb.append("<div>").append("Combat card").append("</div>");
            }
            if (card.isProbeCard()) {
                sb.append("<div>").append("Probe card").append("</div>");
            }
            if (card.isLiberationCard()) {
                sb.append("<div>").append("Liberation card").append("</div>");
            }

            if (cardZone.isInPlay() || cardZone == Zone.HAND) {

                // Show owner (and if stolen)
                sb.append("<div>");
                sb.append("Owner: ").append(card.getOwner());
                if (card.isStolen()) {
                    sb.append(" ('stolen')");
                }
                sb.append("</div>");
                sb.append("<br>");

                // Show location-specific information (if applicable)
                if (card.getBlueprint().getCardCategory() == CardCategory.LOCATION) {

                    // Battleground
                    boolean battleground = modifiersQuerying.isBattleground(gameState, card, null, modifierCollector);
                    sb.append("<div>");
                    sb.append("Battleground: ").append(battleground ? "Yes" : "No");
                    sb.append(produceModifierText(modifierCollector, card));
                    sb.append("</div>");

                    // Force drain amount
                    float darkForceDrainAmount = modifiersQuerying.getForceDrainAmount(gameState, card, darkPlayer, modifierCollector);
                    sb.append("<div>");
                    sb.append("Force drain amount (Dark): ").append(GuiUtils.formatAsString(darkForceDrainAmount));
                    sb.append(produceModifierText(modifierCollector, card));
                    sb.append("</div>");
                    float lightForceDrainAmount = modifiersQuerying.getForceDrainAmount(gameState, card, lightPlayer, modifierCollector);
                    sb.append("<div>");
                    sb.append("Force drain amount (Light): ").append(GuiUtils.formatAsString(lightForceDrainAmount));
                    sb.append(produceModifierText(modifierCollector, card));
                    sb.append("</div>");

                    // Force generation
                    float darkForceGeneration = modifiersQuerying.getForceGenerationFromLocation(gameState, card, darkPlayer, modifierCollector);
                    sb.append("<div>");
                    sb.append("Force generation (Dark): ").append(GuiUtils.formatAsString(darkForceGeneration));
                    sb.append(produceModifierText(modifierCollector, card));
                    sb.append("</div>");
                    float lightForceGeneration = modifiersQuerying.getForceGenerationFromLocation(gameState, card, lightPlayer, modifierCollector);
                    sb.append("<div>");
                    sb.append("Force generation (Light): ").append(GuiUtils.formatAsString(lightForceGeneration));
                    sb.append(produceModifierText(modifierCollector, card));
                    sb.append("</div>");

                    // Force icons
                    int darkForceIcons = modifiersQuerying.getIconCount(gameState, card, Icon.DARK_FORCE, modifierCollector);
                    sb.append("<div>");
                    sb.append("Force icons (Dark): ").append(darkForceIcons);
                    sb.append(produceModifierText(modifierCollector, card));
                    sb.append("</div>");
                    int lightForceIcons = modifiersQuerying.getIconCount(gameState, card, Icon.LIGHT_FORCE, modifierCollector);
                    sb.append("<div>");
                    sb.append("Force icons (Light): ").append(lightForceIcons);
                    sb.append(produceModifierText(modifierCollector, card));
                    sb.append("</div>");

                    // Show parsec and related system info (if applicable)
                    if (card.getSystemOrbited() != null) {
                        PhysicalCard planet = Filters.findFirstFromTopLocationsOnTable(_swccgoGame, Filters.and(Filters.planet_system, Filters.title(card.getSystemOrbited())));
                        if (planet != null) {
                            if (Filters.mobile_system.accepts(gameState, modifiersQuerying, card)) {
                                sb.append("<div>");
                                sb.append("Parsec: ").append(card.getParsec()).append(" (Orbiting: ").append(GameUtils.getCardLink(planet)).append(")");
                                sb.append("</div>");
                            } else if (Filters.asteroid_sector.accepts(gameState, modifiersQuerying, card)) {
                                sb.append("<div>");
                                sb.append("Related to: ").append(GameUtils.getCardLink(planet));
                                sb.append("</div>");
                            } else if (Filters.Big_One_Asteroid_Cave_Or_Space_Slug_Belly.accepts(gameState, modifiersQuerying, card)) {
                                PhysicalCard bigOne = Filters.findFirstFromTopLocationsOnTable(_swccgoGame, Filters.and(Filters.Big_One, Filters.isOrbiting(card.getSystemOrbited())));
                                sb.append("<div>");
                                sb.append("Related to: ").append(GameUtils.getCardLink(bigOne));
                                sb.append("</div>");
                            }
                        } else {
                            sb.append("<div>");
                            sb.append("Parsec: ").append(card.getParsec()).append(" (Orbiting: Unnamed System)");
                            sb.append("</div>");
                        }
                    } else if (Filters.mobile_system.accepts(gameState, modifiersQuerying, card)) {
                        sb.append("<div>");
                        sb.append("Parsec: ").append(card.getParsec()).append(" (Deep Space)");
                        sb.append("</div>");
                    }
                    if (card.getRelatedStarshipOrVehicle() != null) {
                        sb.append("<div>");
                        sb.append("Related to: ").append(GameUtils.getCardLink(card.getRelatedStarshipOrVehicle()));
                        sb.append("</div>");
                    }
                } else {

                    // Show ability (if applicable)
                    if (card.getBlueprint().hasAbilityAttribute() || card.isDejarikHologramAtHolosite()) {
                        float ability = modifiersQuerying.getAbility(gameState, card, modifierCollector);
                        sb.append("<div>");
                        sb.append("Ability: ").append(GuiUtils.formatAsString(ability));
                        sb.append(produceModifierText(modifierCollector, card));
                        sb.append("</div>");
                    }

                    // Show armor (if applicable)
                    if (card.getBlueprint().hasArmorAttribute()) {
                        float armor = modifiersQuerying.getArmor(gameState, card, modifierCollector);
                        sb.append("<div>");
                        sb.append("Armor: ").append(GuiUtils.formatAsString(armor));
                        sb.append(produceModifierText(modifierCollector, card));
                        sb.append("</div>");
                    }

                    // Show defense value (if applicable)
                    if (card.getBlueprint().hasAbilityAttribute() || card.getBlueprint().hasManeuverAttribute()
                            || card.getBlueprint().hasArmorAttribute() || card.getBlueprint().hasSpecialDefenseValueAttribute()
                            || card.isDejarikHologramAtHolosite()) {
                        float forfeit = modifiersQuerying.getDefenseValue(gameState, card, modifierCollector);
                        sb.append("<div>");
                        sb.append("Defense value: ").append(GuiUtils.formatAsString(forfeit));
                        sb.append(produceModifierText(modifierCollector, card));
                        sb.append("</div>");
                    }

                    // Show destiny
                    float destiny = modifiersQuerying.getDestiny(gameState, card, modifierCollector);
                    sb.append("<div>");
                    sb.append("Destiny: ").append(GuiUtils.formatAsString(destiny));
                    sb.append(produceModifierText(modifierCollector, card));
                    sb.append("</div>");

                    // Show ferocity (if applicable)
                    if (card.getBlueprint().hasFerocityAttribute()) {
                        Float ferocityDestinyInAttack = gameState.isDuringAttack() ? gameState.getAttackState().getFerocityDestinyTotal(card) : null;
                        float ferocity = modifiersQuerying.getFerocity(gameState, card, ferocityDestinyInAttack, modifierCollector);
                        sb.append("<div>");
                        sb.append("Ferocity: ").append(GuiUtils.formatAsString(ferocity));
                        sb.append(produceModifierText(modifierCollector, card));
                        sb.append("</div>");
                    }

                    // Show forfeit (if applicable)
                    if (card.getBlueprint().hasForfeitAttribute()) {
                        float forfeit = modifiersQuerying.getForfeit(gameState, card, modifierCollector);
                        sb.append("<div>");
                        sb.append("Forfeit: ").append(GuiUtils.formatAsString(forfeit));
                        sb.append(produceModifierText(modifierCollector, card));
                        sb.append("</div>");
                    }

                    // Show if card gametext is canceled
                    if (card.isGameTextCanceled()
                            || (card.isLocationGameTextCanceledForPlayer(darkPlayer) && card.isLocationGameTextCanceledForPlayer(lightPlayer))) {
                        modifiersQuerying.getCardsMarkingGameTextCanceled(gameState, card, modifierCollector);
                        modifiersQuerying.getCardsMarkingGameTextCanceledForPlayer(gameState, card, darkPlayer, modifierCollector);
                        modifiersQuerying.getCardsMarkingGameTextCanceledForPlayer(gameState, card, lightPlayer, modifierCollector);
                        sb.append("<div>");
                        sb.append("Game text canceled: Yes");
                        sb.append(produceModifierText(modifierCollector, card));
                        sb.append("</div>");
                    } else if (card.isLocationGameTextCanceledForPlayer(darkPlayer)) {
                        modifiersQuerying.getCardsMarkingGameTextCanceledForPlayer(gameState, card, darkPlayer, modifierCollector);
                        sb.append("<div>");
                        sb.append("Game text canceled: Dark Side");
                        sb.append(produceModifierText(modifierCollector, card));
                        sb.append("</div>");
                    } else if (card.isLocationGameTextCanceledForPlayer(lightPlayer)) {
                        modifiersQuerying.getCardsMarkingGameTextCanceledForPlayer(gameState, card, lightPlayer, modifierCollector);
                        sb.append("<div>");
                        sb.append("Game text canceled: Light Side");
                        sb.append(produceModifierText(modifierCollector, card));
                        sb.append("</div>");
                    }

                    // Show hyperspeed (if applicable)
                    if (card.getBlueprint().hasHyperspeedAttribute() && !modifiersQuerying.hasNoHyperdrive(gameState, card)) {
                        float hyperspeed = modifiersQuerying.getHyperspeed(gameState, card, modifierCollector);
                        sb.append("<div>");
                        sb.append("Hyperspeed: ").append(GuiUtils.formatAsString(hyperspeed));
                        sb.append(produceModifierText(modifierCollector, card));
                        sb.append("</div>");
                    }

                    // Show immunity to attrition (if applicable)
                    if (card.getBlueprint().hasImmunityToAttritionAttribute()) {
                        float immunityToAttritionLessThan = modifiersQuerying.getImmunityToAttritionLessThan(gameState, card, modifierCollector);
                        float immunityToAttritionOfExactly = modifiersQuerying.getImmunityToAttritionOfExactly(gameState, card, modifierCollector);
                        if (immunityToAttritionLessThan == Float.MAX_VALUE) {
                            sb.append("<div>");
                            sb.append("Immunity to attrition: All");
                            sb.append(produceModifierText(modifierCollector, card));
                            sb.append("</div>");
                        } else if (immunityToAttritionLessThan > 0 || immunityToAttritionOfExactly > 0) {
                            sb.append("<div>");
                            if (immunityToAttritionOfExactly >= immunityToAttritionLessThan)
                                sb.append("Immunity to attrition: Exactly ").append(GuiUtils.formatAsString(immunityToAttritionOfExactly));
                            else
                                sb.append("Immunity to attrition: Less than ").append(GuiUtils.formatAsString(immunityToAttritionLessThan));
                            sb.append(produceModifierText(modifierCollector, card));
                            sb.append("</div>");
                        }
                    }

                    // Show keywords attributes
                    StringBuilder keywordSb = new StringBuilder();
                    for (Keyword keyword : Keyword.values()) {
                        if (keyword.isInfoDisplayable()) {
                            if (modifiersQuerying.hasKeyword(gameState, card, keyword)) {
                                keywordSb.append(keyword.getHumanReadable()).append(", ");
                            }
                        }
                    }
                    if (keywordSb.length() > 2) {
                        keywordSb.setLength(keywordSb.length() - 2);
                        sb.append("<div>");
                        sb.append("Keywords: ").append(keywordSb);
                        sb.append(produceModifierText(modifierCollector, card));
                        sb.append("</div>");
                    }

                    // Show landspeed (if applicable)
                    if (card.getBlueprint().hasLandspeedAttribute()) {
                        float landspeed = modifiersQuerying.getLandspeed(gameState, card, modifierCollector);
                        sb.append("<div>");
                        sb.append("Landspeed: ").append(GuiUtils.formatAsString(landspeed));
                        sb.append(produceModifierText(modifierCollector, card));
                        sb.append("</div>");
                    }

                    // Show maneuver (if applicable)
                    if (card.getBlueprint().hasManeuverAttribute()) {
                        float maneuver = modifiersQuerying.getManeuver(gameState, card, modifierCollector);
                        sb.append("<div>");
                        sb.append("Maneuver: ").append(GuiUtils.formatAsString(maneuver));
                        sb.append(produceModifierText(modifierCollector, card));
                        sb.append("</div>");
                    }

                    // Show Space Slug mouth (if applicable)
                    if (Filters.Space_Slug.accepts(_swccgoGame, card)) {
                        sb.append("<div>");
                        if (card.isMouthClosed()) {
                            sb.append("Mouth: Closed");
                        } else {
                            sb.append("Mouth: Open");
                        }
                        sb.append("</div>");
                    }

                    // Show movement direction
                    if (card.getMovementDirection() == MovementDirection.LEFT) {
                        sb.append("<div>");
                        sb.append("Movement direction: Left");
                        sb.append("</div>");
                    } else if (card.getMovementDirection() == MovementDirection.RIGHT) {
                        sb.append("<div>");
                        sb.append("Movement direction: Right");
                        sb.append("</div>");
                    }

                    // Show politics (if applicable)
                    if (card.getBlueprint().hasPoliticsAttribute()) {
                        float politics = modifiersQuerying.getPolitics(gameState, card, modifierCollector);
                        sb.append("<div>");
                        sb.append("Politics: ").append(GuiUtils.formatAsString(politics));
                        sb.append(produceModifierText(modifierCollector, card));
                        sb.append("</div>");
                    }

                    // Show power (if applicable)
                    if (card.getBlueprint().hasPowerAttribute()) {
                        float power = modifiersQuerying.getPower(gameState, card, modifierCollector);
                        sb.append("<div>");
                        sb.append("Power: ").append(GuiUtils.formatAsString(power));
                        sb.append(produceModifierText(modifierCollector, card));
                        sb.append("</div>");
                    }

                    // Show race destiny (if applicable)
                    if (card.getRaceDestinyForPlayer() != null) {
                        float raceDestiny = modifiersQuerying.getRaceDestiny(gameState, card, modifierCollector);
                        PhysicalCard stackedOn = card.getStackedOn();
                        String forPlayerText = (stackedOn != null && !Filters.Podracer.accepts(gameState, modifiersQuerying, stackedOn)) ? (" for " + gameState.getSide(card.getRaceDestinyForPlayer()).getHumanReadable()) : "";
                        sb.append("<div>");
                        sb.append("Race destiny").append(forPlayerText).append(": ").append(GuiUtils.formatAsString(raceDestiny));
                        sb.append(produceModifierText(modifierCollector, card));
                        sb.append("</div>");
                    }

                    // Show if card gametext is canceled
                    if (card.isSuspended()) {
                        modifiersQuerying.getCardsMarkingCardSuspended(gameState, card, modifierCollector);
                        sb.append("<div>");
                        sb.append("Suspended: Yes");
                        sb.append(produceModifierText(modifierCollector, card));
                        sb.append("</div>");
                    }

                    // Show other card attributes (if applicable)
                    StringBuilder otherAttrSb = new StringBuilder();
                    if (gameState.isApprentice(card)) {
                        otherAttrSb.append("Apprentice, ");
                    }
                    if (card.isBlownAway()) {
                        otherAttrSb.append("Blown Away, ");
                    }
                    if (card.isCollapsed()) {
                        otherAttrSb.append("Collapsed, ");
                    }
                    if (card.isConcealed()) {
                        otherAttrSb.append("Concealed, ");
                    }
                    if (card.isCrashed()) {
                        otherAttrSb.append("Crashed, ");
                    }
                    if (card.isCrossedOver()) {
                        otherAttrSb.append("Crossed Over, ");
                    }
                    if (card.isDamaged()) {
                        otherAttrSb.append("Damaged, ");
                    }
                    if (card.isHit()) {
                        otherAttrSb.append("Hit, ");
                    }
                    if (card.isImprisoned()) {
                        otherAttrSb.append("Imprisoned, ");
                    }
                    if (card.isFrozen()) {
                        otherAttrSb.append("Frozen, ");
                    }
                    if (card.isMakingBombingRun()) {
                        otherAttrSb.append("Making Bombing Run, ");
                    }
                    if (card.isMissing()) {
                        otherAttrSb.append("Missing, ");
                    }
                    if (card.getSoupEaten() != null) {
                        otherAttrSb.append("Soup Eaten, ");
                    }
                    if (card.isSpaceSlugBelly()) {
                        otherAttrSb.append("Space Slug Belly, ");
                    }
                    if (card.isBinaryOff()) {
                        otherAttrSb.append("Turned Off, ");
                    }
                    if (card.isUndercover()) {
                        otherAttrSb.append("Undercover, ");
                    }
                    if (otherAttrSb.length() > 2) {
                        otherAttrSb.setLength(otherAttrSb.length() - 2);
                        sb.append("<br>");
                        sb.append("<div>");
                        sb.append("Other attributes: ").append(otherAttrSb);
                        sb.append("</div>");
                    }

                    // Show other displayable information (if applicable)
                    String extraDisplayableInformation = card.getBlueprint().getDisplayableInformation(_swccgoGame, card);
                    if (extraDisplayableInformation != null) {
                        sb.append("<div>");
                        sb.append("Extra information: ").append(extraDisplayableInformation);
                        sb.append("</div>");
                    }

                    sb.append("<br>");

                    // Cards aboard this card (if applicable)
                    if (card.getBlueprint().getCardCategory() == CardCategory.STARSHIP || card.getBlueprint().getCardCategory() == CardCategory.VEHICLE) {

                        List<PhysicalCard> pilotsAboard = gameState.getPilotCardsAboard(modifiersQuerying, card, false);
                        int numPermanentPilots = modifiersQuerying.getPermanentPilotsAboard(gameState, card).size();
                        if (!pilotsAboard.isEmpty()) {
                            sb.append("<div>");
                            if (card.getBlueprint().getCardSubtype() == CardSubtype.TRANSPORT) {
                                sb.append("Drivers: ");
                            } else {
                                sb.append("Pilots: ");
                            }
                            sb.append(GameUtils.getAppendedNames(pilotsAboard));
                            if (numPermanentPilots == 0) {
                                sb.append("</div>");
                            }
                        }
                        if (numPermanentPilots > 0) {
                            if (!pilotsAboard.isEmpty()) {
                                sb.append(", Permanent Pilot");
                            } else {
                                sb.append("<div>");
                                sb.append("Pilots: Permanent Pilot");
                            }
                            if (numPermanentPilots > 1) {
                                sb.append("s");
                            }
                            sb.append("</div>");
                        }
                        List<PhysicalCard> passengersAboard = gameState.getPassengerCardsAboard(card);
                        if (passengersAboard != null && !passengersAboard.isEmpty()) {
                            sb.append("<div>");
                            sb.append("Passengers: ");
                            sb.append(GameUtils.getAppendedNames(passengersAboard));
                            sb.append("</div>");
                        }
                        List<PhysicalCard> capitalStarshipsAboard = gameState.getCardsInCapitalStarshipCapacitySlots(card);
                        if (!capitalStarshipsAboard.isEmpty()) {
                            sb.append("<div>");
                            sb.append("Capital starships in cargo hold: ");
                            sb.append(GameUtils.getAppendedNames(capitalStarshipsAboard));
                            sb.append("</div>");
                        }
                        List<PhysicalCard> starfightersOrTIEsAboard = gameState.getCardsInStarfighterOrTIECapacitySlots(card);
                        if (!starfightersOrTIEsAboard.isEmpty()) {
                            sb.append("<div>");
                            sb.append("Starfighters/TIEs in cargo hold: ");
                            sb.append(GameUtils.getAppendedNames(starfightersOrTIEsAboard));
                            sb.append("</div>");
                        }
                        List<PhysicalCard> vehiclesAboard = gameState.getCardsInVehicleCapacitySlots(card);
                        if (!vehiclesAboard.isEmpty()) {
                            sb.append("<div>");
                            sb.append("Vehicles in cargo hold: ");
                            sb.append(GameUtils.getAppendedNames(vehiclesAboard));
                            sb.append("</div>");
                        }
                    }

                    // Card this card is aboard (if applicable)
                    PhysicalCard attachedTo = card.getAttachedTo();
                    if (attachedTo != null) {
                        StringBuilder aboardSb = new StringBuilder();

                        if (attachedTo.getBlueprint().getCardCategory() == CardCategory.STARSHIP
                                || attachedTo.getBlueprint().getCardCategory() == CardCategory.VEHICLE) {
                            if (card.isPilotOf()) {
                                sb.append("<div>");
                                if (attachedTo.getBlueprint().getCardSubtype() == CardSubtype.TRANSPORT) {
                                    sb.append("Driver of: ").append(GameUtils.getCardLink(attachedTo));
                                } else {
                                    sb.append("Pilot of: ").append(GameUtils.getCardLink(attachedTo));
                                }
                                sb.append("</div>");
                            } else if (card.isPassengerOf()) {
                                sb.append("<div>");
                                sb.append("Passenger of: ").append(GameUtils.getCardLink(attachedTo));
                                sb.append("</div>");
                            } else if (card.isInCargoHoldAsCapitalStarship()) {
                                sb.append("<div>");
                                sb.append("In capital starship cargo hold of: ").append(GameUtils.getCardLink(attachedTo));
                                sb.append("</div>");
                            } else if (card.isInCargoHoldAsStarfighterOrTIE()) {
                                sb.append("<div>");
                                sb.append("In starfighter/TIE cargo hold of: ").append(GameUtils.getCardLink(attachedTo));
                                sb.append("</div>");
                            } else if (card.isInCargoHoldAsVehicle()) {
                                sb.append("<div>");
                                sb.append("In vehicle cargo hold of: ").append(GameUtils.getCardLink(attachedTo));
                                sb.append("</div>");
                            }
                        }
                        sb.append(aboardSb);
                    }
                }

                // Target cards (if applicable)
                Map<TargetId, PhysicalCard> targets = card.getTargetedCards(gameState);
                PhysicalCard apprentice = targets.get(TargetId.JEDI_TEST_APPRENTICE);
                if (apprentice != null) {
                    sb.append("<div>");
                    sb.append("Apprentice: ");
                    sb.append(GameUtils.getCardLink(apprentice));
                    sb.append("</div>");
                }
                PhysicalCard mentor = targets.get(TargetId.JEDI_TEST_MENTOR);
                if (mentor != null) {
                    sb.append("<div>");
                    sb.append("Mentor: ");
                    sb.append(GameUtils.getCardLink(mentor));
                    sb.append("</div>");
                }

                // Stacked cards (if applicable)
                List<PhysicalCard> stackedCards = gameState.getStackedCards(card);
                if (!stackedCards.isEmpty()) {
                    sb.append("<div>");
                    sb.append("Stacked cards: ").append(stackedCards.size());
                    sb.append("</div>");
                }

                if (!targets.isEmpty()) {
                    List<PhysicalCard> otherTargets = new ArrayList<PhysicalCard>();
                    for (TargetId targetId : targets.keySet()) {
                        if (targetId != TargetId.JEDI_TEST_APPRENTICE && targetId != TargetId.JEDI_TEST_MENTOR) {
                            otherTargets.add(targets.get(targetId));
                        }
                    }
                    if (!otherTargets.isEmpty()) {
                        sb.append("<div>");
                        sb.append("Targets: ").append(GameUtils.getAppendedNames(otherTargets));
                        sb.append("</div>");
                    }
                }
            }

            // 'Insert card' in Reserve Deck information (if applicable)
            if (cardZone == Zone.TOP_OF_RESERVE_DECK) {
                List<PhysicalCard> insertCards = new LinkedList<PhysicalCard>(Filters.filter(gameState.getCardPile(card.getZoneOwner(), Zone.RESERVE_DECK, false), _swccgoGame, Filters.insertCard));
                if (!insertCards.isEmpty()) {
                    Collections.shuffle(insertCards);
                    sb.append("<div>");
                    sb.append("'Insert' cards: ").append(GameUtils.getAppendedNames(insertCards));
                    sb.append("</div>");
                }
            }

            // Sabacc information (if applicable)
            if (cardZone == Zone.SABACC_HAND || cardZone == Zone.REVEALED_SABACC_HAND) {
                float sabaccValue = card.getSabaccValue();
                sb.append("<div>");
                sb.append("Sabacc value: ").append(((sabaccValue == -1) ? "Not set" : GuiUtils.formatAsString(sabaccValue)));
                PhysicalCard cloningCard = card.getSabaccCardCloned();
                if (cloningCard != null) {
                    sb.append(" (cloning value from ").append(GameUtils.getCardLink(cloningCard)).append(")");
                }
                sb.append("</div>");
            }

            // Show affecting cards
            if (cardZone.isInPlay() || cardZone == Zone.HAND) {
                for (Modifier modifier : modifiersQuerying.getModifiersAffecting(gameState, card)) {
                    modifierCollector.addModifier(modifier);
                }
                StringBuilder otherAttrSb = new StringBuilder();
                otherAttrSb.append(produceModifierText(modifierCollector, card));
                if (otherAttrSb.length() > 0) {
                    sb.append("<br>");
                    sb.append("<div>");
                    sb.append("Other modifiers: ");
                    sb.append(otherAttrSb);
                    sb.append("</div>");
                }
            }

            // Surround with div (if any card info)
            if (sb.length() > 0) {
                sb.insert(0, "<div class='cardInfo'>");
                sb.append("</div>");
            }

            return sb.toString();

        } finally {
            _readLock.unlock();
        }
    }

    public void startGame() {
        _writeLock.lock();
        try {
            _swccgoGame.startGame();
            startClocksForUsersPendingDecision();
        } finally {
            _writeLock.unlock();
        }
    }

    public void cleanup() {
        _writeLock.lock();
        try {
            long currentTime = System.currentTimeMillis();
            Map<String, GameCommunicationChannel> channelsCopy = new HashMap<String, GameCommunicationChannel>(_communicationChannels);
            for (Map.Entry<String, GameCommunicationChannel> playerChannels : channelsCopy.entrySet()) {
                String playerId = playerChannels.getKey();
                // Channel is stale (user no longer connected to game, to save memory, we remove the channel
                // User can always reconnect and establish a new channel
                GameCommunicationChannel channel = playerChannels.getValue();
                if (currentTime > channel.getLastAccessed() + _playerDecisionTimeoutPeriod) {
                    _swccgoGame.removeGameStateListener(channel);
                    _communicationChannels.remove(playerId);
                }
            }

            if (_swccgoGame.getGameState() != null && _swccgoGame.getWinner() == null) {
                for (Map.Entry<String, Long> playerDecision : new HashMap<String, Long>(_decisionQuerySentTimes).entrySet()) {
                    String playerId = playerDecision.getKey();
                    long decisionSent = playerDecision.getValue();

                    // After 3 minutes after start of game, if both players have not made at
                    // least one decision, just cancel the game.
                    if (!_userFeedback.haveBothPlayersMadeAtLeastOneDecision()
                            && _cancelIfNoActions
                            && (currentTime > decisionSent + (1000 * 60 * 3))) {
                        addTimeSpentOnDecisionToUserClock(playerId);
                        _swccgoGame.performAutoCancelGame();
                    }
                    else if (!_disablePlayerDecisionTimer && (currentTime > decisionSent + _playerDecisionTimeoutPeriod)) {
                        addTimeSpentOnDecisionToUserClock(playerId);
                        _swccgoGame.playerLost(playerId, GameEndReason.LOSS__DECISION_TIMEOUT);
                    }
                }

                for (Map.Entry<String, Integer> playerClock : _playerClocks.entrySet()) {
                    String player = playerClock.getKey();
                    if (_maxSecondsForGamePerPlayer + _secondsGameTimerExtended - playerClock.getValue() - getCurrentUserPendingTime(player) < 0) {
                        addTimeSpentOnDecisionToUserClock(player);
                        _swccgoGame.playerLost(player, GameEndReason.LOSS__GAME_TIMEOUT);
                    }
                }
            }
        } finally {
            _writeLock.unlock();
        }
    }

    public void extendGameTimer(Player player, int minutesToExtend) {
        if (!_allowExtendGameTimer) {
            _userFeedback.sendWarning(player.getName(), "You can't extend the game timer for this game");
            return;
        }

        if (_secondsGameTimerExtended > 0)
            return;

        String playerId = player.getName();
        _writeLock.lock();
        try {
            if (isPlayerPlaying(playerId)) {
                _swccgoGame.requestExtendGameTimer(playerId, minutesToExtend);
                _secondsGameTimerExtended = _swccgoGame.getGameTimerExtendedInMinutes() * 60;
                if (_secondsGameTimerExtended > 0) {
                    _swccgoGame.getGameState().sendMessage("The game timer has been extended by " + minutesToExtend + " minutes, by request of all players");
                }
            }
        } finally {
            _writeLock.unlock();
        }
    }

    public void disableActionTimer(Player player) {
        if (_disablePlayerDecisionTimer)
            return;

        String playerId = player.getName();
        _writeLock.lock();
        try {
            if (isPlayerPlaying(playerId)) {
                _swccgoGame.requestDisableActionTimer(playerId);
                _disablePlayerDecisionTimer = _swccgoGame.isActionTimerDisabled();
                if (_disablePlayerDecisionTimer) {
                    _swccgoGame.getGameState().sendMessage("The action timer has been disabled, by request of all players");
                }
            }
        } finally {
            _writeLock.unlock();
        }
    }

    public void concede(Player player) {
        String playerId = player.getName();
        _writeLock.lock();
        try {
            if (_swccgoGame.getWinner() == null && isPlayerPlaying(playerId)) {
                addTimeSpentOnDecisionToUserClock(playerId);
                _swccgoGame.playerLost(playerId, GameEndReason.LOSS__CONCEDED);
            }
        } finally {
            _writeLock.unlock();
        }
    }

    public void cancel(Player player) {
        if (!_cancellable) {
            _userFeedback.sendWarning(player.getName(), "You can't cancel this game");
        }

        String playerId = player.getName();
        _writeLock.lock();
        try {
            if (isPlayerPlaying(playerId))
                _swccgoGame.requestCancel(playerId);
        } finally {
            _writeLock.unlock();
        }
    }

    public synchronized void playerAnswered(Player player, int channelNumber, int decisionId, String answer) throws SubscriptionConflictException, SubscriptionExpiredException {
        String playerName = player.getName();
        _writeLock.lock();
        try {
            GameCommunicationChannel communicationChannel = _communicationChannels.get(playerName);
            if (communicationChannel != null) {
                if (communicationChannel.getChannelNumber() == channelNumber) {
                    AwaitingDecision awaitingDecision = _userFeedback.getAwaitingDecision(playerName);
                    if (awaitingDecision != null) {
                        if (awaitingDecision.getAwaitingDecisionId() == decisionId && !_swccgoGame.isFinished()) {
                            try {
                                _userFeedback.participantDecided(playerName);
                                awaitingDecision.decisionMade(answer);

                                // Decision successfully made, add the time to user clock
                                addTimeSpentOnDecisionToUserClock(playerName);

                                _swccgoGame.carryOutPendingActionsUntilDecisionNeeded();
                                startClocksForUsersPendingDecision();

                            } catch (DecisionResultInvalidException decisionResultInvalidException) {
                                // Participant provided wrong answer - send a warning message, and ask again for the same decision
                                _userFeedback.sendWarning(playerName, decisionResultInvalidException.getWarningMessage());
                                _userFeedback.sendAwaitingDecision(playerName, awaitingDecision);
                            } catch (RuntimeException runtimeException) {
                                LOG.error("Error processing game decision", runtimeException);
                                _swccgoGame.abortGame();
                            }
                        }
                    }
                } else {
                    throw new SubscriptionConflictException();
                }
            } else {
                throw new SubscriptionExpiredException();
            }
        } finally {
            _writeLock.unlock();
        }
    }

    public GameCommunicationChannel getCommunicationChannel(Player player, int channelNumber) throws PrivateInformationException, SubscriptionConflictException, SubscriptionExpiredException {
        String playerName = player.getName();
        if (!player.getType().contains("a") && !_allowSpectators && !isPlayerPlaying(playerName))
            throw new PrivateInformationException();

        _readLock.lock();
        try {
            GameCommunicationChannel communicationChannel = _communicationChannels.get(playerName);
            if (communicationChannel != null) {
                if (communicationChannel.getChannelNumber() == channelNumber) {
                    return communicationChannel;
                } else {
                    throw new SubscriptionConflictException();
                }
            } else {
                throw new SubscriptionExpiredException();
            }
        } finally {
            _readLock.unlock();
        }
    }

    public void processVisitor(GameCommunicationChannel communicationChannel, int channelNumber, String playerName, ParticipantCommunicationVisitor visitor) {
        _readLock.lock();
        try {
            visitor.visitChannelNumber(channelNumber);
            for (GameEvent gameEvent : communicationChannel.consumeGameEvents())
                visitor.visitGameEvent(gameEvent);

            String warning = _userFeedback.consumeWarning(playerName);
            if (warning != null)
                visitor.visitGameEvent(new GameEvent(GameEvent.Type.W).message(warning));

            Map<String, Integer> secondsLeft = new HashMap<String, Integer>();
            for (Map.Entry<String, Integer> playerClock : _playerClocks.entrySet()) {
                String playerClockName = playerClock.getKey();
                secondsLeft.put(playerClockName, _maxSecondsForGamePerPlayer + _secondsGameTimerExtended - playerClock.getValue() - getCurrentUserPendingTime(playerClockName));
            }
            visitor.visitClock(secondsLeft);
        } finally {
            _readLock.unlock();
        }
    }

    public void signupUserForGame(Player player, ParticipantCommunicationVisitor visitor) throws PrivateInformationException {
        String playerName = player.getName();
        if (!player.hasType(Player.Type.ADMIN) && !_allowSpectators && !isPlayerPlaying(playerName))
            throw new PrivateInformationException();

        // Only allow viewing of playtesting formats game if player is a playtester or admin
        if (_swccgoGame.getFormat().isPlaytesting()
                && !(player.hasType(Player.Type.ADMIN)
                || player.hasType(Player.Type.PLAY_TESTER))) {
            throw new PrivateInformationException();
        }
        
        _readLock.lock();
        try {
            int number = _channelNextIndex;
            _channelNextIndex++;

            GameCommunicationChannel participantCommunicationChannel = new GameCommunicationChannel(playerName, number);
            _communicationChannels.put(playerName, participantCommunicationChannel);

            _swccgoGame.addGameStateListener(playerName, participantCommunicationChannel);

            visitor.visitChannelNumber(number);

            for (GameEvent gameEvent : participantCommunicationChannel.consumeGameEvents())
                visitor.visitGameEvent(gameEvent);

            Map<String, Integer> secondsLeft = new HashMap<String, Integer>();
            for (Map.Entry<String, Integer> playerClock : _playerClocks.entrySet()) {
                String playerId = playerClock.getKey();
                secondsLeft.put(playerId, _maxSecondsForGamePerPlayer + _secondsGameTimerExtended - playerClock.getValue() - getCurrentUserPendingTime(playerId));
            }
            visitor.visitClock(secondsLeft);
        } finally {
            _readLock.unlock();
        }
    }

    private void startClocksForUsersPendingDecision() {
        long currentTime = System.currentTimeMillis();
        Set<String> users = _userFeedback.getUsersPendingDecision();
        for (String user : users)
            _decisionQuerySentTimes.put(user, currentTime);
    }

    private void addTimeSpentOnDecisionToUserClock(String participantId) {
        Long queryTime = _decisionQuerySentTimes.remove(participantId);
        if (queryTime != null) {
            long currentTime = System.currentTimeMillis();
            long diffSec = (currentTime - queryTime) / 1000;
            _playerClocks.put(participantId, _playerClocks.get(participantId) + (int) diffSec);
        }
    }

    private int getCurrentUserPendingTime(String participantId) {
        if (!_decisionQuerySentTimes.containsKey(participantId))
            return 0;
        long queryTime = _decisionQuerySentTimes.get(participantId);
        long currentTime = System.currentTimeMillis();
        return (int) ((currentTime - queryTime) / 1000);
    }

    private String getPlayerLifeForce() {
        StringBuilder stringBuilder = new StringBuilder();
        for (SwccgGameParticipant player : _playersPlaying) {
            stringBuilder.append(_swccgoGame.getGameState().getPlayerLifeForce(player.getPlayerId())).append(", ");
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
        }
        return stringBuilder.toString();
    }

    /**
     * Gets the deck archetype being played by the specified player.
     * @param playerId the player
     * @return the deck archetype label
     */
    public String getDeckArchetypeLabel(String playerId) {
        if (_swccgoGame.getGameState().getCurrentPhase() == Phase.PLAY_STARTING_CARDS) {
            return null;
        }
        PhysicalCard startingLocation = _swccgoGame.getModifiersQuerying().getStartingLocation(playerId);
        PhysicalCard objective = _swccgoGame.getGameState().getObjectivePlayed(playerId);
        PhysicalCard startingInterrupt = _swccgoGame.getGameState().getStartingInterruptPlayed(playerId);

        // Based on starting location
        if (startingLocation != null) {
            if (Filters.Massassi_Throne_Room.accepts(_swccgoGame, startingLocation)) {
                // Throne Room Mains
                return "TRM";
            }
            if (Filters.Main_Power_Generators.accepts(_swccgoGame, startingLocation)) {
                // Echo Base Operations
                return "EBO";
            }
            if (startingInterrupt != null) {
                if (Filters.Careful_Planning.accepts(_swccgoGame, startingInterrupt)
                        && startingInterrupt.getBlueprint().hasVirtualSuffix()
                        && startingLocation.getBlueprint().getSystemName() != null) {
                    // Careful Planning (v)
                    return startingLocation.getBlueprint().getSystemName() + " CPv";
                }
                if (Filters.Combat_Readiness.accepts(_swccgoGame, startingInterrupt)
                        && startingInterrupt.getBlueprint().hasVirtualSuffix()
                        && startingLocation.getBlueprint().getSystemName() != null) {
                    // Combat Readiness (v)
                    return startingLocation.getBlueprint().getSystemName() + " CRv";
                }
                if(Filters.Slip_Sliding_Away.accepts(_swccgoGame, startingInterrupt)
                        && startingInterrupt.getBlueprint().hasVirtualSuffix()
                        && startingLocation.getBlueprint().getTitle() != null){
                    // Slip Sliding Away (v)
                    return startingLocation.getBlueprint().getTitle() + " SSAv";
                }
            }
        }

        // Based on Objective
        if (objective != null) {
            if (Filters.or(Filters.Agents_In_The_Court, Filters.No_Love_For_The_Empire).accepts(_swccgoGame, objective)) {
                // Agents In The Court
                return "AITC";
            }
            if (Filters.or(Filters.Agents_Of_Black_Sun, Filters.Vengeance_Of_The_Dark_Prince).accepts(_swccgoGame, objective)) {
                // Agents Of Black Sun
                return "AOBS";
            }
            if (Filters.or(Filters.A_Stunning_Move, Filters.A_Valuable_Hostage).accepts(_swccgoGame, objective)) {
                // A Stunning Move
                return "ASM";
            }
            if (Filters.or(Filters.Bring_Him_Before_Me, Filters.Take_Your_Fathers_Place).accepts(_swccgoGame, objective)) {
                // Bring Him Before Me
                return "BHBM";
            }
            if (Filters.or(Filters.Carbon_Chamber_Testing, Filters.My_Favorite_Decoration).accepts(_swccgoGame, objective)) {
                // Carbon Chamber Testing
                return "CCT";
            }
            if (Filters.or(Filters.Well_Handle_This, Filters.Duel_Of_The_Fates, Filters.Let_Them_Make_The_First_Move, Filters.At_Last_We_Will_Have_Revenge).accepts(_swccgoGame, objective)) {
                // Combat
                return "Combat";
            }
            if (Filters.or(Filters.Court_Of_The_Vile_Gangster, Filters.I_Shall_Enjoy_Watching_You_Die).accepts(_swccgoGame, objective)) {
                // Court Of The Vile Gangster
                return "Court";
            }
            if (Filters.or(Filters.Dantooine_Base_Operations, Filters.More_Dangerous_Than_You_Realize).accepts(_swccgoGame, objective)) {
                // Dantooine Base Operations
                return "DBO";
            }
            if (Filters.or(Filters.City_In_The_Clouds, Filters.You_Truly_Belong_Here_With_Us, Filters.Twin_Suns_Of_Tatooine, Filters.Well_Trained_In_The_Jedi_Arts).accepts(_swccgoGame, objective)) {
                // Demo Deck
                return "Demo";
            }
            if (Filters.or(Filters.Diplomatic_Mission_To_Alderaan, Filters.A_Weakness_Can_Be_Found).accepts(_swccgoGame, objective)) {
                // Diplomatic Mission To Alderaan
                return "Diplo";
            }
            if (Filters.or(Filters.Endor_Operations, Filters.Imperial_Outpost).accepts(_swccgoGame, objective)) {
                if (startingInterrupt != null && Filters.Operational_As_Planned.accepts(_swccgoGame, startingInterrupt)) {
                    // That Thing's Operational
                    return "TTO";
                }
                // Endor Operations
                return "Endor Ops";
            }
            if (Filters.or(Filters.He_Is_The_Chosen_One, Filters.He_Will_Bring_Balance).accepts(_swccgoGame, objective)) {
                // He Is The Chosen One
                return "HITCO";
            }
            if (Filters.or(Filters.Hidden_Base, Filters.Systems_Will_Slip_Through_Your_Fingers).accepts(_swccgoGame, objective)) {
                // Hidden Base
                return "Hidden Base";
            }
            if (Filters.or(Filters.Hunt_Down_And_Destroy_The_Jedi, Filters.Their_Fire_Has_Gone_Out_Of_The_Universe).accepts(_swccgoGame, objective)) {
                // Hunt Down
                return "Hunt Down";
            }
            if (Filters.or(Filters.The_Hyperdrive_Generators_Gone, Filters.Well_Need_A_New_One).accepts(_swccgoGame, objective)) {
                // The Hyperdrive Generators Gone
                return "Hyperdrive";
            }
            if (Filters.or(Filters.I_Want_That_Map, Filters.And_Now_Youll_Give_It_To_Me).accepts(_swccgoGame, objective)) {
                // I Want That Map
                return "Map";
            }
            if (Filters.or(Filters.Imperial_Entanglements, Filters.No_One_To_Stop_Us_This_Time).accepts(_swccgoGame, objective)) {
                // Imperial Entanglements
                return "IE";
            }
            if (Filters.or(Filters.Invasion, Filters.In_Complete_Control).accepts(_swccgoGame, objective)) {
                // Invasion
                return "Invasion";
            }
            if (Filters.or(Filters.ISB_Operations, Filters.Empires_Sinister_Agents).accepts(_swccgoGame, objective)) {
                // ISB Operations
                return "ISB";
            }
            if (Filters.or(Filters.The_Galaxy_May_Need_A_Legend, Filters.We_Need_Luke_Skywalker).accepts(_swccgoGame, objective)) {
                // The Galaxy May Need A Legend
                return "Legend";
            }
            if (Filters.or(Filters.Massassi_Base_Operations, Filters.One_In_A_Million).accepts(_swccgoGame, objective)) {
                // Massassi Base Operations
                return "MBO";
            }
            if (Filters.or(Filters.My_Kind_Of_Scum, Filters.Fearless_And_Inventive).accepts(_swccgoGame, objective)) {
                // My Kind Of Scum
                return "MKOS";
            }
            if (Filters.or(Filters.Mind_What_You_Have_Learned, Filters.Save_You_It_Can).accepts(_swccgoGame, objective)) {
                // Mind What Your Have Learned
                return "MWYHL";
            }
            if (Filters.or(Filters.They_Have_No_Idea_Were_Coming, Filters.Until_We_Win_Or_The_Chances_Are_Spent).accepts(_swccgoGame, objective)) {
                // They Have No Idea We're Coming
                return "No Idea";
            }
            if (Filters.or(Filters.Old_Allies, Filters.We_Need_Your_Help).accepts(_swccgoGame, objective)) {
                // Old Allies
                return "Old Allies";
            }
            if (Filters.or(Filters.Local_Uprising, Filters.Liberation, Filters.Imperial_Occupation, Filters.Imperial_Control).accepts(_swccgoGame, objective)) {
                // Operatives
                return "Operatives";
            }
            if (Filters.or(Filters.You_Can_Either_Profit_By_This, Filters.Or_Be_Destroyed).accepts(_swccgoGame, objective)) {
                // Your Can Either Profit By This...
                return "Profit";
            }
            if (Filters.or(Filters.Quiet_Mining_Colony, Filters.Independent_Operation).accepts(_swccgoGame, objective)) {
                // Quiet Mining Colony
                return "QMC";
            }
            if (Filters.or(Filters.Ralltiir_Operations, Filters.In_The_Hands_Of_The_Empire).accepts(_swccgoGame, objective)) {
                // Ralltiir Operations
                return "Ralltiir Ops";
            }
            if (Filters.or(Filters.Rebel_Strike_Team, Filters.Garrison_Destroyed).accepts(_swccgoGame, objective)) {
                // Rebel Strike Team
                return "RST";
            }
            if (Filters.or(Filters.Rescue_The_Princess, Filters.Sometimes_I_Amaze_Even_Myself).accepts(_swccgoGame, objective)) {
                // Rescue The Princess
                return "RTP";
            }
            if (Filters.or(Filters.Plead_My_Case_To_The_Senate, Filters.Sanity_And_Compassion, Filters.My_Lord_Is_That_Legal, Filters.I_Will_Make_It_Legal).accepts(_swccgoGame, objective)) {
                // Senate
                return "Senate";
            }
            if (Filters.or(Filters.Set_Your_Course_For_Alderaan, Filters.The_Ultimate_Power_In_The_Universe).accepts(_swccgoGame, objective)) {
                // Set Your Course For Alderaan
                return "SYCFA";
            }
            if (Filters.or(Filters.This_Deal_Is_Getting_Worse_All_The_Time, Filters.Pray_I_Dont_Alter_It_Any_Further).accepts(_swccgoGame, objective)) {
                // This Deal Is Getting Worse All The Time
                return "TDIGWATT";
            }
            if (Filters.or(Filters.There_Is_Good_In_Him, Filters.I_Can_Save_Him).accepts(_swccgoGame, objective)) {
                // There Is Good In Him
                return "TIGIH";
            }
            if (Filters.or(Filters.No_Money_No_Parts_No_Deal, Filters.Youre_A_Slave).accepts(_swccgoGame, objective)) {
                // Watto
                return "Watto";
            }
            if (Filters.or(Filters.We_Have_A_Plan, Filters.They_Will_Be_Lost_And_Confused).accepts(_swccgoGame, objective)) {
                // WHAP
                return "WHAP";
            }
            if (Filters.or(Filters.Watch_Your_Step, Filters.This_Place_Can_Be_A_Little_Rough).accepts(_swccgoGame, objective)) {
                // Watch Your Step
                return "WYS";
            }
            if (Filters.or(Filters.Yavin_4_Operations, Filters.The_Time_To_Fight_Is_Now).accepts(_swccgoGame, objective)) {
                // Yavin 4 Operations
                return "Y4O";
            }
        }

        return "Other";
    }
}
