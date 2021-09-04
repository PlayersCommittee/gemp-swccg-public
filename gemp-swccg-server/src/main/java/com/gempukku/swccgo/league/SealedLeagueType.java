package com.gempukku.swccgo.league;


/**
 * Defines the sealed league types.
 */
public enum SealedLeagueType {

    PREMIERE_ANH_SEALED("premiere_anh_sealed", "sealed"),
    HOTH_DAGOBAH_CC_SEALED("hoth_dagobah_cc_sealed", "sealed"),
    JP_SEALED("jp_sealed", "jp_sealed"),
    ENDOR_DSII_SEALED("endor_dsII_sealed", "sealed"),
    EPISODE_I_SEALED("episode_i_sealed", "sealed"),
    ALL_OF_THE_JEDI_SEALED("all_of_the_jedi_sealed", "sealed"),
    NOVELTY_SEALED("novelty_sealed","sealed"),
    WATTOS_CUBE_WITH_OBJECTIVE_PACKS("wattos_cube_with_objective", "cube"),
    WATTOS_CUBE_WITH_FIXED("wattos_cube_with_fixed", "cube"),
    ;

    public static SealedLeagueType getLeagueType(String sealedCode) {
        for (SealedLeagueType sealedLeagueType : SealedLeagueType.values()) {
            if (sealedLeagueType.getSealedCode().equals(sealedCode))
                return sealedLeagueType;
        }
        return null;
    }

    private String _sealedCode;
    private String _formatCode;

    SealedLeagueType(String sealedCode, String formatCode) {
        _sealedCode = sealedCode;
        _formatCode = formatCode;
    }

    /**
     * Gets the sealed league code.
     * @return the sealed league code
     */
    public String getSealedCode() {
        return _sealedCode;
    }

    /**
     * Gets the format code.
     * @return the format code
     */
    public String getFormatCode() {
        return _formatCode;
    }
}
