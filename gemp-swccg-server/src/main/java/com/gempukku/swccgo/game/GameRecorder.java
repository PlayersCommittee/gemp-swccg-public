package com.gempukku.swccgo.game;

import com.gempukku.swccgo.common.ApplicationConfiguration;
import com.gempukku.swccgo.game.state.EventSerializer;
import com.gempukku.swccgo.game.state.GameCommunicationChannel;
import com.gempukku.swccgo.game.state.GameEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class GameRecorder {
    private static String _possibleChars = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static int _charsCount = _possibleChars.length();

    private GameHistoryService _gameHistoryService;

    public GameRecorder(GameHistoryService gameHistoryService) {
        _gameHistoryService = gameHistoryService;
    }

    private String randomUid() {
        int length = 16;
        char[] chars = new char[length];
        Random rnd = new Random();
        for (int i = 0; i < length; i++)
            chars[i] = _possibleChars.charAt(rnd.nextInt(_charsCount));

        return new String(chars);
    }

    public InputStream getRecordedGame(String playerId, String gameId) throws IOException {
        final File file = getRecordingFile(playerId, gameId);
        if (!file.exists() || !file.isFile())
            return null;
        return new InflaterInputStream(new FileInputStream(file));
    }

    public GameRecordingInProgress recordGame(SwccgGameMediator swccgoGame, final String formatName, final String tournament, final Map<String, String> deckNames) {
        final Date startData = new Date();
        final Map<String, GameCommunicationChannel> recordingChannels = new HashMap<String, GameCommunicationChannel>();
        for (SwccgGameParticipant player : swccgoGame.getPlayersPlaying()) {
            String playerId = player.getPlayerId();
            GameCommunicationChannel recordChannel = new GameCommunicationChannel(playerId, 0);
            swccgoGame.addGameStateListener(playerId, recordChannel);
            recordingChannels.put(playerId, recordChannel);
        }
        final SwccgGameMediator mediator = swccgoGame;
        
        return new GameRecordingInProgress() {
            @Override
            public void finishRecording(String winner, String winReason, String loser, String loseReason) {
                Map<String, String> playerRecordingId = saveRecordedChannels(recordingChannels);
                _gameHistoryService.addGameHistory(winner, loser, winReason, loseReason, playerRecordingId.get(winner), playerRecordingId.get(loser), formatName, tournament, deckNames.get(winner), deckNames.get(loser), mediator.getDeckArchetypeLabel(winner), mediator.getDeckArchetypeLabel(loser), mediator.getWinningSideString(), startData, new Date());
            }
        };
    }

    public interface GameRecordingInProgress {
        public void finishRecording(String winner, String winReason, String loser, String loseReason);
    }

    private File getRecordingFile(String playerId, String gameId) {
        File gameReplayFolder = new File(ApplicationConfiguration.getProperty("application.root"), "replay");
        File playerReplayFolder = new File(gameReplayFolder, playerId);
        return new File(playerReplayFolder, gameId + ".xml.gz");
    }

    private OutputStream getRecordingWriteStream(String playerId, String gameId) throws IOException {
        File recordingFile = getRecordingFile(playerId, gameId);
        recordingFile.getParentFile().mkdirs();

        Deflater deflater = new Deflater(9);
        return new DeflaterOutputStream(new FileOutputStream(recordingFile), deflater);
    }

    private Map<String, String> saveRecordedChannels(Map<String, GameCommunicationChannel> gameProgress) {
        Map<String, String> result = new HashMap<String, String>();
        for (Map.Entry<String, GameCommunicationChannel> playerRecordings : gameProgress.entrySet()) {
            String playerId = playerRecordings.getKey();

            String gameRecordingId = getRecordingId(playerId);
            final List<GameEvent> gameEvents = playerRecordings.getValue().consumeGameEvents();

            try {
                OutputStream replayStream = getRecordingWriteStream(playerId, gameRecordingId);
                try {
                    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                    Document doc = documentBuilder.newDocument();
                    Element gameReplay = doc.createElement("gameReplay");
                    EventSerializer serializer = new EventSerializer();
                    for (GameEvent gameEvent : gameEvents) {
                        gameReplay.appendChild(serializer.serializeEvent(doc, gameEvent));
                    }

                    doc.appendChild(gameReplay);

                    // Prepare the DOM document for writing
                    Source source = new DOMSource(doc);

                    // Prepare the output file
                    Result streamResult = new StreamResult(replayStream);

                    // Write the DOM document to the file
                    Transformer xformer = TransformerFactory.newInstance().newTransformer();
                    xformer.transform(source, streamResult);
                } finally {
                    replayStream.close();
                }
            } catch (Exception exp) {

            }
            result.put(playerId, gameRecordingId);
        }
        return result;
    }

    private String getRecordingId(String playerId) {
        String result;
        File recordingFile;
        do {
            result = randomUid();
            recordingFile = getRecordingFile(playerId, result);
        } while (recordingFile.exists());
        return result;
    }
}
