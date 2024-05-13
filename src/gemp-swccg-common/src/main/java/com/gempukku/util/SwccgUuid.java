package com.gempukku.util;

import java.util.UUID;
import java.util.Random;
import java.time.Duration;
import java.time.LocalDateTime;

public class SwccgUuid {
    /*
     * UUID version 1 is based on the current timestamp, concatenated with the MAC address.
     * While Java handles the heavy lift for v3 and v4, values must be fed to the UUID Constructor to generate the other types.
     * The least significant bit on a v1 UUID would normally be a MAC address.
     * Here we generate the least significant bit randomly.
     * @return Long
     */
    private static long get64LeastSignificantBitsForVersion1() {
        Random random = new Random();
        long random63BitLong = random.nextLong() & 0x3FFFFFFFFFFFFFFFL;
        long variant3BitFlag = 0x8000000000000000L;
        return random63BitLong + variant3BitFlag;
    }
    /*
     * UUID version 1 is based on the current timestamp, concatenated with the MAC address.
     * While Java handles the heavy lift for v3 and v4, values must be fed to the UUID Constructor to generate the other types.
     * The least significant bit on a v1 UUID would normally be a MAC address.
     * Here we generate the least significant bit randomly.
     * @return Long
     */
    private static long get64MostSignificantBitsForVersion1() {
        LocalDateTime start = LocalDateTime.of(1582, 10, 15, 0, 0, 0);
        Duration duration = Duration.between(start, LocalDateTime.now());
        long seconds = duration.getSeconds();
        long nanos = duration.getNano();
        long timeForUuidIn100Nanos = seconds * 10000000 + nanos * 100;
        long least12SignificatBitOfTime = (timeForUuidIn100Nanos & 0x000000000000FFFFL) >> 4;
        long version = 1 << 12;
        return
          (timeForUuidIn100Nanos & 0xFFFFFFFFFFFF0000L) + version + least12SignificatBitOfTime;
    }

    /*
     * UUID version 1 is based on the current timestamp, measured in units of 100 nanoseconds from October 15, 1582, 
     * concatenated with the MAC address of the device where the UUID is created.
     * Java provides an implementation for the v3 and v4.
     * While Java handles the heavy lift for v3 and v4, values must be fed to the UUID Constructor to generate the other types.
     * The least significant bit on a v1 UUID would normally be a MAC address.
     * The most significant bit on a v1 UUID is a timestamp.
     * @return UUID
     */
    public static UUID generateType1UUID() {
        long most64SigBits = get64MostSignificantBitsForVersion1();
        long least64SigBits = get64LeastSignificantBitsForVersion1();
        return new UUID(most64SigBits, least64SigBits);
    }
    /*
     * Generates a new UUIDv1.
     * Convert the UUIDv1 to a String.
     * Reverse the UUIDv1.
     * An example UUID might be: 01ecccb2-b8f8-1c98-b034-c1432f6403a0
     * With followup UUID's have the similar start: 01ecccab-a9e3-1df4-85e3-322f46c1f07a
     *                                              01ecccbb-b594-179c-bd4a-b7a81ecc1053
     *                                              01ecccb1-7d2e-1326-9bc0-11fbdcf672ac
     *                                              01ecccba-ab51-1f06-a846-4f3ea6aa89e4
     * If you wanted to route based on URL, then /hall/{tableId} would be the same if you routed
     * based on a wildcard like /hall/01*
     * To solve that problem, we reverse the UUID so that the most unique part of the UUID, the end, becomes the beginning:
     * 01ecccb2-b8f8-1c98-b034-c1432f6403a0 -> 0a3046f2341c-430b-89c1-8f8b-2bccce10
     * If routing based on the previous example, we would route to unique backend servers based on the hex code:
     * 01ecccab-a9e3-1df4-85e3-322f46c1f07a: /hall/a7*
     * 01ecccbb-b594-179c-bd4a-b7a81ecc1053: /hall/35*
     * 01ecccb1-7d2e-1326-9bc0-11fbdcf672ac: /hall/ca*
     * 01ecccba-ab51-1f06-a846-4f3ea6aa89e4: /hall/4e*
     * @return String
     */
    public static String generateNewTableId() {
        UUID uuid1=generateType1UUID();
        return new StringBuffer(uuid1.toString()).reverse().toString();
    }
}
