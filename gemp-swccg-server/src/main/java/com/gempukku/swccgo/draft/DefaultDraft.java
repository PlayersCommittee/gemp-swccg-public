package com.gempukku.swccgo.draft;

import com.gempukku.swccgo.SubscriptionConflictException;
import com.gempukku.swccgo.SubscriptionExpiredException;
import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.db.vo.CollectionType;
import com.gempukku.swccgo.game.CardCollection;
import com.gempukku.swccgo.game.DefaultCardCollection;
import com.gempukku.swccgo.game.MutableCardCollection;
import com.gempukku.swccgo.packagedProduct.PackagedProductStorage;
import com.gempukku.swccgo.tournament.TournamentCallback;

import java.util.*;

// TODO - it has to be thread safe
public class DefaultDraft implements Draft {
    // 35 seconds
    public static final int PICK_TIME = 35 * 1000;

    private CollectionsManager _collectionsManager;
    private CollectionType _collectionType;
    private PackagedProductStorage _packagedProductStorage;
    private DraftPack _draftPack;
    private List<String> _players;

    private List<MutableCardCollection> _cardChoices = new ArrayList<MutableCardCollection>();
    private Map<String, MutableCardCollection> _cardChoice = new HashMap<String, MutableCardCollection>();

    private int _playerCount;

    private long _lastPickStart;

    private int _nextPickNumber = 0;
    private int _nextPackIndex = 0;

    private boolean _finishedDraft;

    private Map<String, DraftCommunicationChannel> _playerDraftCommunications = new HashMap<String, DraftCommunicationChannel>();
    private int _nextChannelNumber = 0;

    public DefaultDraft(CollectionsManager collectionsManager, CollectionType collectionType, PackagedProductStorage packagedProductStorage, DraftPack draftPack, Set<String> players) {
        _collectionsManager = collectionsManager;
        _collectionType = collectionType;
        _packagedProductStorage = packagedProductStorage;
        _draftPack = draftPack;
        _players = new ArrayList(players);
        Collections.shuffle(_players);
        
        _playerCount = _players.size();

        CardCollection fixedCollection = _draftPack.getFixedCollection();
        for (String player : _players)
            _collectionsManager.addPlayerCollection(false, "New draft fixed collection", player, _collectionType, fixedCollection);
    }

    @Override
    public void advanceDraft(TournamentCallback draftCallback) {
        if (haveAllPlayersPicked()) {
            if (haveAllCardsBeenChosen()) {
                if (haveMorePacks()) {
                    openNextPacks();
                } else {
                    _finishedDraft = true;
                }
            } else {
                presentNewCardChoices();
            }
            for (DraftCommunicationChannel draftCommunicationChannel : _playerDraftCommunications.values())
                draftCommunicationChannel.draftChanged();
        } else {
            if (choiceTimePassed()) {
                forceRandomCardChoice();
                for (DraftCommunicationChannel draftCommunicationChannel : _playerDraftCommunications.values())
                    draftCommunicationChannel.draftChanged();
            }
        }
    }

    @Override
    public void playerChosenCard(String playerName, String cardId) {
        playerChosen(playerName, cardId);
        _playerDraftCommunications.get(playerName).draftChanged();
    }

    public void signUpForDraft(String playerName, DraftChannelVisitor draftChannelVisitor) {
        DraftCommunicationChannel draftCommunicationChannel = new DraftCommunicationChannel(_nextChannelNumber++);
        _playerDraftCommunications.put(playerName, draftCommunicationChannel);
        draftCommunicationChannel.processCommunicationChannel(getCardChoice(playerName), getChosenCards(playerName), draftChannelVisitor);
    }

    public DraftCommunicationChannel getCommunicationChannel(String playerName, int channelNumber) throws SubscriptionExpiredException, SubscriptionConflictException {
        DraftCommunicationChannel communicationChannel = _playerDraftCommunications.get(playerName);
        if (communicationChannel != null) {
            if (communicationChannel.getChannelNumber() == channelNumber) {
                return communicationChannel;
            } else {
                throw new SubscriptionConflictException();
            }
        } else {
            throw new SubscriptionExpiredException();
        }
    }

    public DraftCardChoice getCardChoice(String playerName) {
        MutableCardCollection cardChoice = _cardChoice.get(playerName);

        return new DefaultDraftCardChoice(cardChoice, _lastPickStart + PICK_TIME);
    }

    @Override
    public CardCollection getChosenCards(String playerName) {
        return _collectionsManager.getPlayerCollection(playerName, _collectionType.getCode());
    }

    @Override
    public boolean isFinished() {
        return _finishedDraft;
    }

    private void forceRandomCardChoice() {
        Map<String, MutableCardCollection> cardChoiceCopy = new HashMap<String, MutableCardCollection>(_cardChoice);

        for (Map.Entry<String, MutableCardCollection> playerChoices : cardChoiceCopy.entrySet()) {
            String playerName = playerChoices.getKey();
            MutableCardCollection collection = playerChoices.getValue();
            String cardId = collection.getAll().entrySet().iterator().next().getKey();
            playerChosen(playerName, cardId);
        }
    }

    private void playerChosen(String playerName, String cardId) {
        MutableCardCollection cardChoice = _cardChoice.get(playerName);
        if (cardChoice != null) {
            if (cardChoice.removeItem(cardId, 1)) {
                _collectionsManager.addItemsToPlayerCollection(false, "Pick in draft", playerName, _collectionType, Arrays.asList(CardCollection.Item.createItem(cardId, 1)));
                _cardChoice.remove(playerName);
            }
        }
    }

    private void presentNewCardChoices() {
        for (int i = 0; i < _players.size(); i++) {
            _cardChoice.put(_players.get(i), _cardChoices.get((i + _nextPickNumber) % _playerCount));
        }
        _nextPickNumber++;
        _lastPickStart = System.currentTimeMillis();
    }

    private void openNextPacks() {
        _cardChoices.clear();
        String packId = _draftPack.getPacks().get(_nextPackIndex);
        for (int i = 0; i < _playerCount; i++) {
            MutableCardCollection cardCollection = new DefaultCardCollection();
            cardCollection.addItem(packId, 1);
            cardCollection.openPack(packId, null, _packagedProductStorage);
            _cardChoices.add(cardCollection);
        }
        _nextPackIndex++;
        _nextPickNumber = 0;
    }

    private boolean choiceTimePassed() {
        return System.currentTimeMillis() > PICK_TIME + _lastPickStart;
    }

    private boolean haveMorePacks() {
        return _nextPackIndex < _draftPack.getPacks().size();
    }

    private boolean haveAllCardsBeenChosen() {
        return _cardChoices.size() == 0 || _cardChoices.get(0).getAll().size() == 0;
    }

    private boolean haveAllPlayersPicked() {
        return _cardChoice.isEmpty();
    }
}
