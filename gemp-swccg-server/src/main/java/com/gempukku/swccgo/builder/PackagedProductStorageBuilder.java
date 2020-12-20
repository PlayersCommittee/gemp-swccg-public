package com.gempukku.swccgo.builder;

import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.packagedProduct.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * This builder class creates the packaged product storage and adds them to the object map.
 */
public class PackagedProductStorageBuilder {
    private static final Logger _logger = Logger.getLogger(PackagedProductStorageBuilder.class);

    /**
     * Adds the packaged product storage to the object map.
     * @param objectMap the object map
     */
    public static void fillObjectMap(Map<Type, Object> objectMap) {
        objectMap.put(PackagedProductStorage.class,
                PackagedProductStorageBuilder.createPackagedProductStorage(extract(objectMap, SwccgCardBlueprintLibrary.class)));
    }

    /**
     * Creates the packaged product storage.
     * @param library the blueprint library
     * @return the packaged product storage
     */
    private static PackagedProductStorage createPackagedProductStorage(SwccgCardBlueprintLibrary library) {
        try {
            PackagedProductStorage packStorage = new PackagedProductStorage();
            PackagedCardProduct product;

            // Add Standard Booster Packs
            product = new PremiereBoosterPack(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new ANewHopeBoosterPack(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new HothBoosterPack(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new DagobahBoosterPack(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new CloudCityBoosterPack(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new JabbasPalaceBoosterPack(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new SpecialEditionBoosterPack(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new EndorBoosterPack(library, false);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new EndorBoosterPack(library, true);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new DeathStarIIBoosterPack(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new TatooineBoosterPack(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new TatooineBoosterPack(library,false);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new CoruscantBoosterPack(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new CoruscantBoosterPack(library, false);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new TheedPalaceBoosterPack(library);
            packStorage.addPackagedProduct(product.getProductName(), product);

            // Add Standard Booster Boxes
            product = new PremiereBoosterBox(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new ANewHopeBoosterBox(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new HothBoosterBox(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new DagobahBoosterBox(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new CloudCityBoosterBox(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new JabbasPalaceBoosterBox(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new SpecialEditionBoosterBox(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new EndorBoosterBox(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new DeathStarIIBoosterBox(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new TatooineBoosterBox(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new CoruscantBoosterBox(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new TheedPalaceBoosterBox(library);
            packStorage.addPackagedProduct(product.getProductName(), product);

            // Add Reflections Booster Packs
            product = new ReflectionsBoosterPack(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new ReflectionsIIBoosterPack(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new ReflectionsIIIBoosterPack(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new ReflectionsIIIBoosterPack(library, false, false);
            packStorage.addPackagedProduct(product.getProductName(), product);


            // Add Reflections Booster Boxes
            product = new ReflectionsBoosterBox(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new ReflectionsIIBoosterBox(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new ReflectionsIIIBoosterBox(library);
            packStorage.addPackagedProduct(product.getProductName(), product);

            // Add Specialty Packs
            product = new JediPack(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new RebelLeaderPack(library);
            packStorage.addPackagedProduct(product.getProductName(), product);

            // Add Enhanced Packs
            product = new EnhancedPremierePack_BobaFett(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new EnhancedPremierePack_DarthVader(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new EnhancedPremierePack_Han(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new EnhancedPremierePack_Leia(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new EnhancedPremierePack_Luke(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new EnhancedPremierePack_ObiWan(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new EnhancedCloudCityPack_BobaFett(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new EnhancedCloudCityPack_Chewie(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new EnhancedCloudCityPack_IG88(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new EnhancedCloudCityPack_Lando(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new EnhancedJabbasPalacePack_Boushh(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new EnhancedJabbasPalacePack_MaraJade(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new EnhancedJabbasPalacePack_MasterLuke(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new EnhancedJabbasPalacePack_SeeThreepio(library);
            packStorage.addPackagedProduct(product.getProductName(), product);

            // Add Starter Decks/Sets and Pre-Constructed Decks
            product = new PremiereStarterSet(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new SpecialEditionDarkStarterDeck(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new SpecialEditionLightStarterDeck(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new DeathStarIIDarkPreConstructedDeck(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new DeathStarIILightPreConstructedDeck(library);
            packStorage.addPackagedProduct(product.getProductName(), product);

            // Add Sealed Decks
            product = new OfficialTournamentSealedDeck(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new JabbasPalaceSealedDeck(library);
            packStorage.addPackagedProduct(product.getProductName(), product);

            // Add Two-Player Game Boxes
            product = new PremiereIntroTwoPlayerGameBox(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new EmpireStrikesBackIntroTwoPlayerGameBox(library);
            packStorage.addPackagedProduct(product.getProductName(), product);

            // Add Anthology Boxes
            product = new FirstAnthologyBox(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new SecondAnthologyBox(library);
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new ThirdAnthologyBox(library);
            packStorage.addPackagedProduct(product.getProductName(), product);

            // Selections
            packStorage.addPackagedProduct("(S)Booster Choice -- Non-Reflections", new FixedPackBox("(S)Booster Choice -- Non-Reflections"));
            packStorage.addPackagedProduct("(S)Booster Choice -- Reflections", new FixedPackBox("(S)Booster Choice -- Reflections"));
            packStorage.addPackagedProduct("(S)Enhanced Cloud City Pack Choice", new FixedPackBox("(S)Enhanced Cloud City Pack Choice"));
            packStorage.addPackagedProduct("(S)Enhanced Jabba's Palace Pack Choice", new FixedPackBox("(S)Enhanced Jabba's Palace Pack Choice"));
            packStorage.addPackagedProduct("(S)Enhanced Premiere Pack Choice -- Dark", new FixedPackBox("(S)Enhanced Premiere Pack Choice -- Dark"));
            packStorage.addPackagedProduct("(S)Enhanced Premiere Pack Choice -- Light", new FixedPackBox("(S)Enhanced Premiere Pack Choice -- Light"));

            // Add Cube Packs
            product = new WattosCubeDraftPack(library, "Dark");
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new WattosCubeDraftPack(library, "Light");
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new WattosCubeFixedPack(library, "Dark");
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new WattosCubeFixedPack(library, "Light");
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new WattosCubeObjectivePack(library, "Dark");
            packStorage.addPackagedProduct(product.getProductName(), product);
            product = new WattosCubeObjectivePack(library, "Light");
            packStorage.addPackagedProduct(product.getProductName(), product);


            return packStorage;
        } catch (IOException exp) {
            _logger.error("Error while creating resource", exp);
            exp.printStackTrace();
        } catch (RuntimeException exp) {
            _logger.error("Error while creating resource", exp);
            exp.printStackTrace();
        }
        return null;
    }

    /**
     * Extracts the object of the specified class from the object map.
     * @param objectMap the object map
     * @param clazz the class
     * @param <T> the class type
     * @return the object
     */
    private static <T> T extract(Map<Type, Object> objectMap, Class<T> clazz) {
        T result = (T) objectMap.get(clazz);
        if (result == null)
            throw new RuntimeException("Unable to find class " + clazz.getName());
        return result;
    }
}
