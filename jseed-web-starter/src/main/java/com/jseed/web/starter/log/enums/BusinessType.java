package com.jseed.web.starter.log.enums;

/**
 * Business operation type
 * 
 * @author cyrus
 */
public enum BusinessType
{
    /**
     * Other
     */
    OTHER,

    /**
     * Create
     */
    INSERT,

    /**
     * Update
     */
    UPDATE,

    /**
     * Delete
     */
    DELETE,

    /**
     * Grant access
     */
    GRANT,

    /**
     * Export
     */
    EXPORT,

    /**
     * Import
     */
    IMPORT,

    /**
     * Force sign-out
     */
    FORCE,

    /**
     * Generate code
     */
    GENCODE,
    
    /**
     * Clear data
     */
    CLEAN,
}
